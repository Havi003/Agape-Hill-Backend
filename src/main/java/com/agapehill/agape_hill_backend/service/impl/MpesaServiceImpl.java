package com.agapehill.agape_hill_backend.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.agapehill.agape_hill_backend.config.MpesaProperties;
import com.agapehill.agape_hill_backend.domain.entity.FeePaymentEntity;
import com.agapehill.agape_hill_backend.domain.entity.FeeStatusEntity;
import com.agapehill.agape_hill_backend.domain.entity.MpesaTransactionEntity;
import com.agapehill.agape_hill_backend.domain.entity.StudentEntity;
import com.agapehill.agape_hill_backend.dto.request.MpesaConfirmationRequest;
import com.agapehill.agape_hill_backend.dto.request.MpesaRegisterUrlRequest;
import com.agapehill.agape_hill_backend.dto.request.MpesaValidationRequest;
import com.agapehill.agape_hill_backend.dto.response.MpesaAccessTokenResponse;
import com.agapehill.agape_hill_backend.dto.response.MpesaCallbackResponse;
import com.agapehill.agape_hill_backend.dto.response.MpesaRegisterUrlResponse;
import com.agapehill.agape_hill_backend.repository.FeePaymentRepository;
import com.agapehill.agape_hill_backend.repository.FeesStatusRepository;
import com.agapehill.agape_hill_backend.repository.MpesaTransactionRepository;
import com.agapehill.agape_hill_backend.repository.StudentRepository;
import com.agapehill.agape_hill_backend.service.MpesaService;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MpesaServiceImpl implements MpesaService {

    private final MpesaTransactionRepository mpesaTransactionRepo;
    private final FeePaymentRepository feePaymentRepo;
    private final StudentRepository studentRepo;
    private final FeesStatusRepository feeStatusRepo;
    private final MpesaProperties mpesaProperties;
    private final WebClient mpesaWebClient;

    @Override
    public Mono<String> generateAccessToken() {
        return mpesaWebClient.get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header("Authorization", buildBasicAuthHeader())
                .retrieve()
                .bodyToMono(MpesaAccessTokenResponse.class)
                .map(MpesaAccessTokenResponse::getAccessToken);
    }

    @Override
    public Mono<MpesaRegisterUrlResponse> registerC2BUrls() {
        MpesaRegisterUrlRequest request = new MpesaRegisterUrlRequest(
                mpesaProperties.getShortCode(),
                mpesaProperties.getResponseType(),
                mpesaProperties.getConfirmationUrl(),
                mpesaProperties.getValidationUrl()
        );

        return generateAccessToken()
                .flatMap(accessToken -> mpesaWebClient.post()
                        .uri("/mpesa/c2b/v1/registerurl")
                        .header("Authorization", "Bearer " + accessToken)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(MpesaRegisterUrlResponse.class));
    }

    @Override
    public Mono<MpesaCallbackResponse> validatePayment(MpesaValidationRequest request) {
        ParsedBillReference parsed = parseBillReference(request.getBillRefNumber());

        if (parsed.admissionNumber().isBlank()) {
            return Mono.just(new MpesaCallbackResponse("C2B00011", "Missing admission number"));
        }

        return studentRepo.findByAdmissionNumberIgnoreCase(parsed.admissionNumber())
                .map(student -> new MpesaCallbackResponse("0", "Accepted"))
                .switchIfEmpty(Mono.just(new MpesaCallbackResponse("C2B00011", "Student admission number not found")));
    }

    @Override
    @Transactional
    public Mono<MpesaCallbackResponse> processConfirmation(MpesaConfirmationRequest request) {
        if (request.getTransId() == null || request.getTransId().isBlank()) {
            return Mono.just(new MpesaCallbackResponse("0", "Callback received without transaction id"));
        }

        return mpesaTransactionRepo.existsByTransId(request.getTransId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(new MpesaCallbackResponse("0", "Duplicate transaction ignored"));
                    }

                    ParsedBillReference parsed = parseBillReference(request.getBillRefNumber());

                    return studentRepo.findByAdmissionNumberIgnoreCase(parsed.admissionNumber())
                            .flatMap(student -> processMatchedPayment(request, parsed, student))
                            .switchIfEmpty(processUnmatchedPayment(request, parsed));
                });
    }

    private Mono<MpesaCallbackResponse> processMatchedPayment(
            MpesaConfirmationRequest request,
            ParsedBillReference parsed,
            StudentEntity student
    ) {
        LocalDateTime now = LocalDateTime.now();

        MpesaTransactionEntity transaction = buildTransaction(
                request,
                parsed,
                student.getId(),
                "APPLIED",
                now,
                "Payment matched and applied"
        );

        return mpesaTransactionRepo.save(transaction)
                .flatMap(savedTransaction -> {
                    FeePaymentEntity feePayment = new FeePaymentEntity(
                            null,
                            student.getId(),
                            savedTransaction.getId(),
                            request.getTransAmount(),
                            now,
                            now
                    );

                    return feePaymentRepo.save(feePayment)
                            .then(updateStudentFeeStatus(student.getId(), request.getTransAmount()));
                })
                .thenReturn(new MpesaCallbackResponse("0", "Payment accepted and applied"));
    }

    private Mono<MpesaCallbackResponse> processUnmatchedPayment(
            MpesaConfirmationRequest request,
            ParsedBillReference parsed
    ) {
        LocalDateTime now = LocalDateTime.now();

        MpesaTransactionEntity transaction = buildTransaction(
                request,
                parsed,
                null,
                "UNMATCHED",
                now,
                "No student found for admission number"
        );

        return mpesaTransactionRepo.save(transaction)
                .thenReturn(new MpesaCallbackResponse("0", "Payment accepted but unmatched"));
    }

    private Mono<FeeStatusEntity> updateStudentFeeStatus(UUID studentId, BigDecimal amountPaid) {
        BigDecimal paidAmount = defaultAmount(amountPaid);

        return feeStatusRepo.findById(studentId)
                .flatMap(feeStatus -> {
                    BigDecimal currentPaid = defaultAmount(feeStatus.getTotalPaid());
                    BigDecimal totalBilled = defaultAmount(feeStatus.getTotalBilled());

                    BigDecimal newPaid = currentPaid.add(paidAmount);
                    BigDecimal newBalance = totalBilled.subtract(newPaid);

                    feeStatus.setTotalPaid(newPaid);
                    feeStatus.setBalance(newBalance);
                    feeStatus.setNew(false);

                    return feeStatusRepo.save(feeStatus);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    FeeStatusEntity feeStatus = new FeeStatusEntity(
                            studentId,
                            BigDecimal.ZERO,
                            paidAmount,
                            BigDecimal.ZERO.subtract(paidAmount),
                            true
                    );

                    return feeStatusRepo.save(feeStatus);
                }));
    }

    private MpesaTransactionEntity buildTransaction(
            MpesaConfirmationRequest request,
            ParsedBillReference parsed,
            UUID studentId,
            String status,
            LocalDateTime now,
            String resultMessage
    ) {
        return new MpesaTransactionEntity(
                null,
                request.getTransId(),
                request.getTransAmount(),
                request.getBillRefNumber(),
                parsed.admissionNumber(),
                parsed.paymentPurpose(),
                request.getMsisdn(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName(),
                studentId,
                status,
                Json.of("{}"),
                now,
                "APPLIED".equals(status) ? now : null,
                resultMessage
        );
    }

    private ParsedBillReference parseBillReference(String billRefNumber) {
        if (billRefNumber == null || billRefNumber.isBlank()) {
            return new ParsedBillReference("", "FEES");
        }

        String[] parts = billRefNumber.trim().split("\\s+");
        String admissionNumber = parts[0].trim();
        String purpose = parts.length > 1 ? parts[1].trim().toUpperCase(Locale.ROOT) : "FEES";

        return new ParsedBillReference(admissionNumber, purpose);
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private String buildBasicAuthHeader() {
        String credentials = mpesaProperties.getConsumerKey() + ":" + mpesaProperties.getConsumerSecret();
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encodedCredentials;
    }

    private record ParsedBillReference(String admissionNumber, String paymentPurpose) {
    }
}
