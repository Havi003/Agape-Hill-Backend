package com.agapehill.agape_hill_backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agapehill.agape_hill_backend.domain.entity.FeeStructureEntity;
import com.agapehill.agape_hill_backend.domain.entity.FeeStructureItemEntity;
import com.agapehill.agape_hill_backend.dto.request.FeeStructureItemRequest;
import com.agapehill.agape_hill_backend.dto.request.FeeStructureRequest;
import com.agapehill.agape_hill_backend.dto.response.FeeStructureItemResponse;
import com.agapehill.agape_hill_backend.dto.response.FeeStructureResponse;
import com.agapehill.agape_hill_backend.dto.response.WsHeader;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.repository.FeeStructureItemRepository;
import com.agapehill.agape_hill_backend.repository.FeeStructureRepository;
import com.agapehill.agape_hill_backend.service.FeeStructureService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FeeStructureServiceImpl implements FeeStructureService {

    private final FeeStructureRepository feeStructureRepo;
    private final FeeStructureItemRepository feeStructureItemRepo;

    @Override
    public Mono<WsResponse<List<FeeStructureResponse>>> getFeeStructures(UUID academicYearId, UUID termId, String classGroup) {
        return feeStructureRepo.findAll()
                .filter(structure -> academicYearId == null || academicYearId.equals(structure.getAcademicYearId()))
                .filter(structure -> termId == null || termId.equals(structure.getTermId()))
                .filter(structure -> classGroup == null || classGroup.isBlank() || classGroup.equalsIgnoreCase(structure.getClassGroup()))
                .flatMap(this::mapStructure)
                .collectList()
                .map(list -> response("200", "Fee structures retrieved", list));
    }

    @Override
    public Mono<WsResponse<FeeStructureResponse>> getFeeStructure(UUID feeStructureId) {
        return feeStructureRepo.findById(feeStructureId)
                .switchIfEmpty(Mono.error(new RuntimeException("Fee structure not found")))
                .flatMap(this::mapStructure)
                .map(body -> response("200", "Fee structure retrieved", body));
    }

    @Override
    public Mono<WsResponse<FeeStructureResponse>> createFeeStructure(FeeStructureRequest request) {
        LocalDateTime now = LocalDateTime.now();
        FeeStructureEntity structure = new FeeStructureEntity(
                null,
                request.getAcademicYearId(),
                request.getTermId(),
                request.getClassGroup(),
                request.getName(),
                "DRAFT",
                now,
                now
        );

        return feeStructureRepo.save(structure)
                .flatMap(this::mapStructure)
                .map(body -> response("200", "Fee structure created", body));
    }

    @Override
    public Mono<WsResponse<FeeStructureResponse>> updateFeeStructure(UUID feeStructureId, FeeStructureRequest request) {
        return feeStructureRepo.findById(feeStructureId)
                .switchIfEmpty(Mono.error(new RuntimeException("Fee structure not found")))
                .flatMap(structure -> {
                    structure.setAcademicYearId(request.getAcademicYearId());
                    structure.setTermId(request.getTermId());
                    structure.setClassGroup(request.getClassGroup());
                    structure.setName(request.getName());
                    structure.setUpdatedAt(LocalDateTime.now());
                    return feeStructureRepo.save(structure);
                })
                .flatMap(this::mapStructure)
                .map(body -> response("200", "Fee structure updated", body));
    }

    @Override
    public Mono<WsResponse<FeeStructureResponse>> publishFeeStructure(UUID feeStructureId) {
        return updateStatus(feeStructureId, "PUBLISHED", "Fee structure published");
    }

    @Override
    public Mono<WsResponse<FeeStructureResponse>> archiveFeeStructure(UUID feeStructureId) {
        return updateStatus(feeStructureId, "ARCHIVED", "Fee structure archived");
    }

    @Override
    @Transactional
    public Mono<WsResponse<FeeStructureResponse>> duplicateFeeStructure(UUID feeStructureId) {
        return feeStructureRepo.findById(feeStructureId)
                .switchIfEmpty(Mono.error(new RuntimeException("Fee structure not found")))
                .flatMap(source -> {
                    LocalDateTime now = LocalDateTime.now();
                    FeeStructureEntity copy = new FeeStructureEntity(
                            null,
                            source.getAcademicYearId(),
                            source.getTermId(),
                            source.getClassGroup(),
                            source.getName() + " Copy",
                            "DRAFT",
                            now,
                            now
                    );
                    return feeStructureRepo.save(copy)
                            .flatMap(savedCopy -> feeStructureItemRepo.findByFeeStructureId(source.getId())
                                    .flatMap(item -> {
                                        FeeStructureItemEntity copyItem = new FeeStructureItemEntity(
                                                null,
                                                savedCopy.getId(),
                                                item.getItemName(),
                                                item.getAmount(),
                                                item.getItemType(),
                                                item.getAppliesToClassGroup(),
                                                item.getDescription(),
                                                item.isActive(),
                                                now,
                                                now
                                        );
                                        return feeStructureItemRepo.save(copyItem);
                                    })
                                    .then(Mono.just(savedCopy)));
                })
                .flatMap(this::mapStructure)
                .map(body -> response("200", "Fee structure duplicated", body));
    }

    @Override
    public Mono<WsResponse<FeeStructureItemResponse>> addFeeStructureItem(UUID feeStructureId, FeeStructureItemRequest request) {
        LocalDateTime now = LocalDateTime.now();
        FeeStructureItemEntity item = new FeeStructureItemEntity(
                null,
                feeStructureId,
                request.getItemName(),
                defaultAmount(request.getAmount()),
                request.getItemType(),
                request.getAppliesToClassGroup(),
                request.getDescription(),
                true,
                now,
                now
        );

        return feeStructureItemRepo.save(item)
                .map(this::mapItem)
                .map(body -> response("200", "Fee structure item created", body));
    }

    @Override
    public Mono<WsResponse<FeeStructureItemResponse>> updateFeeStructureItem(UUID feeStructureId, UUID itemId, FeeStructureItemRequest request) {
        return feeStructureItemRepo.findById(itemId)
                .filter(item -> item.getFeeStructureId().equals(feeStructureId))
                .switchIfEmpty(Mono.error(new RuntimeException("Fee structure item not found")))
                .flatMap(item -> {
                    item.setItemName(request.getItemName());
                    item.setAmount(defaultAmount(request.getAmount()));
                    item.setItemType(request.getItemType());
                    item.setAppliesToClassGroup(request.getAppliesToClassGroup());
                    item.setDescription(request.getDescription());
                    item.setUpdatedAt(LocalDateTime.now());
                    return feeStructureItemRepo.save(item);
                })
                .map(this::mapItem)
                .map(body -> response("200", "Fee structure item updated", body));
    }

    @Override
    public Mono<WsResponse<Void>> deleteFeeStructureItem(UUID feeStructureId, UUID itemId) {
        return feeStructureItemRepo.findById(itemId)
                .filter(item -> item.getFeeStructureId().equals(feeStructureId))
                .switchIfEmpty(Mono.error(new RuntimeException("Fee structure item not found")))
                .flatMap(feeStructureItemRepo::delete)
                .thenReturn(response("200", "Fee structure item deleted", null));
    }

    private Mono<WsResponse<FeeStructureResponse>> updateStatus(UUID feeStructureId, String status, String message) {
        return feeStructureRepo.findById(feeStructureId)
                .switchIfEmpty(Mono.error(new RuntimeException("Fee structure not found")))
                .flatMap(structure -> {
                    structure.setStatus(status);
                    structure.setUpdatedAt(LocalDateTime.now());
                    return feeStructureRepo.save(structure);
                })
                .flatMap(this::mapStructure)
                .map(body -> response("200", message, body));
    }

    private Mono<FeeStructureResponse> mapStructure(FeeStructureEntity structure) {
        return feeStructureItemRepo.findByFeeStructureId(structure.getId())
                .map(this::mapItem)
                .collectList()
                .map(items -> {
                    BigDecimal compulsory = totalByType(items, "COMPULSORY");
                    BigDecimal optional = totalByType(items, "OPTIONAL");
                    return new FeeStructureResponse(
                            structure.getId(),
                            structure.getAcademicYearId(),
                            structure.getTermId(),
                            structure.getClassGroup(),
                            structure.getName(),
                            structure.getStatus(),
                            compulsory,
                            optional,
                            items
                    );
                });
    }

    private FeeStructureItemResponse mapItem(FeeStructureItemEntity item) {
        return new FeeStructureItemResponse(
                item.getId(),
                item.getItemName(),
                item.getAmount(),
                item.getItemType(),
                item.getAppliesToClassGroup(),
                item.getDescription(),
                item.isActive()
        );
    }

    private BigDecimal totalByType(List<FeeStructureItemResponse> items, String type) {
        return items.stream()
                .filter(item -> type.equalsIgnoreCase(item.getItemType()))
                .map(FeeStructureItemResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private <T> WsResponse<T> response(String code, String message, T body) {
        return new WsResponse<>(new WsHeader(code, message), body);
    }
}
