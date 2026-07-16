package com.agapehill.agape_hill_backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agapehill.agape_hill_backend.domain.entity.FeePaymentEntity;
import com.agapehill.agape_hill_backend.domain.entity.FeeStatusEntity;
import com.agapehill.agape_hill_backend.domain.entity.FeeStructureItemEntity;
import com.agapehill.agape_hill_backend.domain.entity.StudentChargeEntity;
import com.agapehill.agape_hill_backend.domain.entity.StudentEntity;
import com.agapehill.agape_hill_backend.domain.entity.StudentFeeOptionEntity;
import com.agapehill.agape_hill_backend.dto.request.GenerateBillsRequest;
import com.agapehill.agape_hill_backend.dto.request.ManualPaymentRequest;
import com.agapehill.agape_hill_backend.dto.request.StudentFeeOptionRequest;
import com.agapehill.agape_hill_backend.dto.request.StudentFeeOptionsUpdateRequest;
import com.agapehill.agape_hill_backend.dto.response.BillingPreviewResponse;
import com.agapehill.agape_hill_backend.dto.response.FeeDashboardResponse;
import com.agapehill.agape_hill_backend.dto.response.GenerateBillsResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentChargeResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentFeeOptionResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentPaymentResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentStatementResponse;
import com.agapehill.agape_hill_backend.dto.response.WsHeader;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.repository.FeePaymentRepository;
import com.agapehill.agape_hill_backend.repository.FeeStructureItemRepository;
import com.agapehill.agape_hill_backend.repository.FeeStructureRepository;
import com.agapehill.agape_hill_backend.repository.FeesStatusRepository;
import com.agapehill.agape_hill_backend.repository.StudentChargeRepository;
import com.agapehill.agape_hill_backend.repository.StudentFeeOptionRepository;
import com.agapehill.agape_hill_backend.repository.StudentRepository;
import com.agapehill.agape_hill_backend.service.BillingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final StudentFeeOptionRepository studentFeeOptionRepo;
    private final FeeStructureRepository feeStructureRepo;
    private final FeeStructureItemRepository feeStructureItemRepo;
    private final StudentChargeRepository studentChargeRepo;
    private final FeePaymentRepository feePaymentRepo;
    private final FeesStatusRepository feeStatusRepo;
    private final StudentRepository studentRepo;

    @Override
    public Mono<WsResponse<List<StudentFeeOptionResponse>>> getStudentFeeOptions(UUID studentId, UUID academicYearId, UUID termId) {
        return studentFeeOptionRepo.findByStudentIdAndAcademicYearIdAndTermId(studentId, academicYearId, termId)
                .map(this::mapOption)
                .collectList()
                .map(options -> response("200", "Student fee options retrieved", options));
    }

    @Override
    @Transactional
    public Mono<WsResponse<List<StudentFeeOptionResponse>>> updateStudentFeeOptions(UUID studentId, StudentFeeOptionsUpdateRequest request) {
        return Flux.fromIterable(request.getOptions() == null ? List.<StudentFeeOptionRequest>of() : request.getOptions())
                .concatMap(option -> upsertOption(studentId, request, option))
                .map(this::mapOption)
                .collectList()
                .map(options -> response("200", "Student fee options updated", options));
    }

    @Override
    public Mono<WsResponse<List<BillingPreviewResponse>>> previewTermBills(GenerateBillsRequest request) {
        return selectedStudents(request)
                .flatMap(student -> buildPreview(student, request))
                .collectList()
                .map(preview -> response("200", "Billing preview generated", preview));
    }

    @Override
    @Transactional
    public Mono<WsResponse<GenerateBillsResponse>> generateTermBills(GenerateBillsRequest request) {
        return selectedStudents(request)
                .concatMap(student -> studentChargeRepo.existsByStudentIdAndTermId(student.getId(), request.getTermId())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.just(new BillingGenerationResult(false, BigDecimal.ZERO));
                            }
                            return createChargesForStudent(student, request)
                                    .flatMap(total -> {
                                        if (total.compareTo(BigDecimal.ZERO) <= 0) {
                                            return Mono.just(new BillingGenerationResult(false, BigDecimal.ZERO));
                                        }
                                        return recalculateBalanceEntity(student.getId()).thenReturn(new BillingGenerationResult(true, total));
                                    });
                        }))
                .collectList()
                .map(results -> {
                    int generated = (int) results.stream().filter(BillingGenerationResult::generated).count();
                    int skipped = results.size() - generated;
                    BigDecimal total = results.stream().map(BillingGenerationResult::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    return response("200", "Term bills generated", new GenerateBillsResponse(generated, skipped, total, "Billing completed"));
                });
    }

    @Override
    public Mono<WsResponse<StudentStatementResponse>> getStudentStatement(UUID studentId, UUID academicYearId, UUID termId) {
        return buildStatement(studentId, termId)
                .map(statement -> response("200", "Student statement retrieved", statement));
    }

    @Override
    public Mono<WsResponse<StudentStatementResponse>> recalculateStudentBalance(UUID studentId) {
        return recalculateBalanceEntity(studentId)
                .then(buildStatement(studentId, null))
                .map(statement -> response("200", "Student balance recalculated", statement));
    }

    @Override
    public Mono<WsResponse<GenerateBillsResponse>> recalculateTermBalances(UUID termId) {
        return studentChargeRepo.findAll()
                .filter(charge -> termId.equals(charge.getTermId()))
                .map(StudentChargeEntity::getStudentId)
                .distinct()
                .concatMap(this::recalculateBalanceEntity)
                .count()
                .map(count -> response("200", "Term balances recalculated", new GenerateBillsResponse(count.intValue(), 0, BigDecimal.ZERO, "Recalculated " + count + " students")));
    }

    @Override
    public Mono<WsResponse<FeeDashboardResponse>> getFeeDashboard(UUID academicYearId, UUID termId) {
        return Mono.zip(
                studentChargeRepo.findAll()
                        .filter(charge -> matchesFilter(academicYearId, charge.getAcademicYearId()))
                        .filter(charge -> matchesFilter(termId, charge.getTermId()))
                        .filter(charge -> "ACTIVE".equalsIgnoreCase(charge.getStatus()))
                        .collectList(),
                feePaymentRepo.findAll()
                        .filter(payment -> matchesFilter(academicYearId, payment.getAcademicYearId()))
                        .filter(payment -> matchesFilter(termId, payment.getTermId()))
                        .collectList(),
                feeStructureRepo.findAll()
                        .filter(structure -> matchesFilter(academicYearId, structure.getAcademicYearId()))
                        .filter(structure -> matchesFilter(termId, structure.getTermId()))
                        .collectList(),
                studentRepo.count()
        ).map(tuple -> {
            List<StudentChargeEntity> charges = tuple.getT1();
            List<FeePaymentEntity> payments = tuple.getT2();
            BigDecimal totalBilled = charges.stream()
                    .map(StudentChargeEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPaid = payments.stream()
                    .map(FeePaymentEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal netBalance = totalBilled.subtract(totalPaid);
            BigDecimal totalBalance = netBalance.compareTo(BigDecimal.ZERO) > 0 ? netBalance : BigDecimal.ZERO;
            BigDecimal totalCredit = netBalance.compareTo(BigDecimal.ZERO) < 0 ? netBalance.abs() : BigDecimal.ZERO;
            long publishedStructures = tuple.getT3().stream()
                    .filter(structure -> "PUBLISHED".equalsIgnoreCase(structure.getStatus()))
                    .count();
            long draftStructures = tuple.getT3().stream()
                    .filter(structure -> "DRAFT".equalsIgnoreCase(structure.getStatus()))
                    .count();
            Set<UUID> billedStudents = charges.stream()
                    .map(StudentChargeEntity::getStudentId)
                    .collect(Collectors.toSet());
            boolean configurationReady = publishedStructures > 0;
            String readinessMessage = configurationReady
                    ? "Published fee structures are available for billing."
                    : "No published fee structures are available for the selected scope.";

            FeeDashboardResponse body = new FeeDashboardResponse(
                    academicYearId,
                    termId,
                    totalBilled,
                    totalPaid,
                    totalBalance,
                    totalCredit,
                    charges.size(),
                    payments.size(),
                    tuple.getT4(),
                    billedStudents.size(),
                    tuple.getT3().size(),
                    publishedStructures,
                    draftStructures,
                    configurationReady,
                    readinessMessage
            );
            return response("200", "Fee dashboard retrieved", body);
        });
    }

    @Override
    @Transactional
    public Mono<WsResponse<StudentPaymentResponse>> recordManualPayment(ManualPaymentRequest request) {
        LocalDateTime now = LocalDateTime.now();
        FeePaymentEntity payment = new FeePaymentEntity(
                null,
                request.getStudentId(),
                null,
                request.getAcademicYearId(),
                request.getTermId(),
                defaultAmount(request.getAmount()),
                request.getPaymentMethod() == null ? "MANUAL" : request.getPaymentMethod(),
                request.getReference(),
                request.getNotes(),
                request.getPaidAt() == null ? now : request.getPaidAt(),
                now
        );

        return feePaymentRepo.save(payment)
                .flatMap(saved -> recalculateBalanceEntity(saved.getStudentId()).thenReturn(saved))
                .map(this::mapPayment)
                .map(body -> response("200", "Manual payment recorded", body));
    }

    private Mono<StudentFeeOptionEntity> upsertOption(UUID studentId, StudentFeeOptionsUpdateRequest request, StudentFeeOptionRequest option) {
        LocalDateTime now = LocalDateTime.now();
        return studentFeeOptionRepo.findByStudentIdAndAcademicYearIdAndTermIdAndOptionName(
                        studentId,
                        request.getAcademicYearId(),
                        request.getTermId(),
                        option.getOptionName()
                )
                .defaultIfEmpty(new StudentFeeOptionEntity(
                        null,
                        studentId,
                        request.getAcademicYearId(),
                        request.getTermId(),
                        option.getOptionName(),
                        false,
                        null,
                        now,
                        now
                ))
                .flatMap(entity -> {
                    entity.setEnabled(option.isEnabled());
                    entity.setAmountOverride(option.getAmountOverride());
                    entity.setUpdatedAt(now);
                    return studentFeeOptionRepo.save(entity);
                });
    }

    private Flux<StudentEntity> selectedStudents(GenerateBillsRequest request) {
        Flux<StudentEntity> students = request.getStudentIds() != null && !request.getStudentIds().isEmpty()
                ? Flux.fromIterable(request.getStudentIds()).flatMap(studentRepo::findById)
                : studentRepo.findAll();

        return students.filter(student -> request.getClassGroup() == null
                || request.getClassGroup().isBlank()
                || request.getClassGroup().equalsIgnoreCase(resolveClassGroup(student.getStudentClass())));
    }

    private Mono<BillingPreviewResponse> buildPreview(StudentEntity student, GenerateBillsRequest request) {
        return activeItemsForStudent(student, request)
                .collectList()
                .flatMap(items -> {
                    BigDecimal compulsory = totalItems(items, "COMPULSORY");
                    BigDecimal optional = totalItems(items, "OPTIONAL");
                    BigDecimal newTermBill = compulsory.add(optional);

                    return Mono.zip(
                            studentChargeRepo.sumActiveChargesByStudentId(student.getId()).defaultIfEmpty(BigDecimal.ZERO),
                            feePaymentRepo.sumAmountByStudentId(student.getId()).defaultIfEmpty(BigDecimal.ZERO)
                    ).map(tuple -> {
                        BigDecimal previousBalance = tuple.getT1().subtract(tuple.getT2());
                        BigDecimal credit = previousBalance.compareTo(BigDecimal.ZERO) < 0 ? previousBalance.abs() : BigDecimal.ZERO;
                        BigDecimal finalBalance = previousBalance.add(newTermBill);
                        return new BillingPreviewResponse(
                                student.getId(),
                                student.getAdmissionNumber(),
                                student.getFullName(),
                                student.getStudentClass(),
                                compulsory,
                                optional,
                                previousBalance,
                                credit,
                                newTermBill,
                                finalBalance
                        );
                    });
                });
    }

    private Mono<BigDecimal> createChargesForStudent(StudentEntity student, GenerateBillsRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return activeItemsForStudent(student, request)
                .concatMap(item -> {
                    StudentChargeEntity charge = new StudentChargeEntity(
                            null,
                            student.getId(),
                            request.getAcademicYearId(),
                            request.getTermId(),
                            item.getId(),
                            item.getItemName(),
                            item.getAmount(),
                            "OPTIONAL".equalsIgnoreCase(item.getItemType()) ? "OPTIONAL" : "TERM_FEE",
                            "ACTIVE",
                            now,
                            now
                    );
                    return studentChargeRepo.save(charge);
                })
                .map(StudentChargeEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Flux<FeeStructureItemEntity> activeItemsForStudent(StudentEntity student, GenerateBillsRequest request) {
        String classGroup = resolveClassGroup(student.getStudentClass());

        return feeStructureRepo.findByTermIdAndClassGroupAndStatus(request.getTermId(), classGroup, "PUBLISHED")
                .flatMapMany(structure -> feeStructureItemRepo.findByFeeStructureIdAndActiveTrue(structure.getId()))
                .flatMap(item -> {
                    if (!"OPTIONAL".equalsIgnoreCase(item.getItemType())) {
                        return Mono.just(item);
                    }

                    return studentFeeOptionRepo.findByStudentIdAndAcademicYearIdAndTermIdAndOptionName(
                                    student.getId(),
                                    request.getAcademicYearId(),
                                    request.getTermId(),
                                    item.getItemName()
                            )
                            .filter(StudentFeeOptionEntity::isEnabled)
                            .map(option -> {
                                if (option.getAmountOverride() != null) {
                                    item.setAmount(option.getAmountOverride());
                                }
                                return item;
                            });
                });
    }

    private Mono<FeeStatusEntity> recalculateBalanceEntity(UUID studentId) {
        return Mono.zip(
                        studentChargeRepo.sumActiveChargesByStudentId(studentId).defaultIfEmpty(BigDecimal.ZERO),
                        feePaymentRepo.sumAmountByStudentId(studentId).defaultIfEmpty(BigDecimal.ZERO)
                )
                .flatMap(tuple -> {
                    BigDecimal billed = tuple.getT1();
                    BigDecimal paid = tuple.getT2();
                    BigDecimal balance = billed.subtract(paid);

                    return feeStatusRepo.findById(studentId)
                            .defaultIfEmpty(new FeeStatusEntity(studentId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, true))
                            .flatMap(status -> {
                                status.setTotalBilled(billed);
                                status.setTotalPaid(paid);
                                status.setBalance(balance);
                                status.setNew(status.getStudentId() != null && status.isNew());
                                return feeStatusRepo.save(status);
                            });
                });
    }

    private Mono<StudentStatementResponse> buildStatement(UUID studentId, UUID termId) {
        return studentRepo.findById(studentId)
                .switchIfEmpty(Mono.error(new RuntimeException("Student not found")))
                .flatMap(student -> {
                    Flux<StudentChargeEntity> charges = termId == null
                            ? studentChargeRepo.findByStudentIdOrderByCreatedAtDesc(studentId)
                            : studentChargeRepo.findByStudentIdAndTermIdOrderByCreatedAtDesc(studentId, termId);

                    Flux<FeePaymentEntity> payments = termId == null
                            ? feePaymentRepo.findByStudentIdOrderByPaidAtDesc(studentId)
                            : feePaymentRepo.findByStudentIdAndTermIdOrderByPaidAtDesc(studentId, termId);

                    return Mono.zip(
                            charges.map(this::mapCharge).collectList(),
                            payments.map(this::mapPayment).collectList(),
                            studentChargeRepo.sumActiveChargesByStudentId(studentId).defaultIfEmpty(BigDecimal.ZERO),
                            feePaymentRepo.sumAmountByStudentId(studentId).defaultIfEmpty(BigDecimal.ZERO)
                    ).map(tuple -> new StudentStatementResponse(
                            student.getId(),
                            student.getAdmissionNumber(),
                            student.getFullName(),
                            student.getStudentClass(),
                            tuple.getT3(),
                            tuple.getT4(),
                            tuple.getT3().subtract(tuple.getT4()),
                            tuple.getT1(),
                            tuple.getT2()
                    ));
                });
    }

    private StudentFeeOptionResponse mapOption(StudentFeeOptionEntity option) {
        return new StudentFeeOptionResponse(
                option.getId(),
                option.getStudentId(),
                option.getAcademicYearId(),
                option.getTermId(),
                option.getOptionName(),
                option.isEnabled(),
                option.getAmountOverride()
        );
    }

    private StudentChargeResponse mapCharge(StudentChargeEntity charge) {
        return new StudentChargeResponse(
                charge.getId(),
                charge.getTermId(),
                charge.getDescription(),
                charge.getChargeType(),
                charge.getAmount(),
                charge.getStatus(),
                charge.getCreatedAt()
        );
    }

    private StudentPaymentResponse mapPayment(FeePaymentEntity payment) {
        return new StudentPaymentResponse(
                payment.getId(),
                payment.getPaymentMethod(),
                payment.getMpesaTransactionId() == null ? null : payment.getMpesaTransactionId().toString(),
                payment.getReference(),
                payment.getAmount(),
                payment.getPaidAt()
        );
    }

    private BigDecimal totalItems(List<FeeStructureItemEntity> items, String type) {
        return items.stream()
                .filter(item -> type.equalsIgnoreCase(item.getItemType()))
                .map(FeeStructureItemEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String resolveClassGroup(String studentClass) {
        if (studentClass == null) {
            return "LOWER_PRIMARY";
        }

        String normalized = studentClass.toLowerCase();
        if (normalized.contains("ecd") || normalized.contains("nursery") || normalized.contains("pp")) {
            return "ECD_NURSERY";
        }
        if (normalized.contains("4") || normalized.contains("5") || normalized.contains("6") || normalized.contains("upper")) {
            return "UPPER_PRIMARY";
        }
        return "LOWER_PRIMARY";
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private boolean matchesFilter(UUID expected, UUID actual) {
        return expected == null || expected.equals(actual);
    }

    private <T> WsResponse<T> response(String code, String message, T body) {
        return new WsResponse<>(new WsHeader(code, message), body);
    }

    private record BillingGenerationResult(boolean generated, BigDecimal amount) {
    }
}
