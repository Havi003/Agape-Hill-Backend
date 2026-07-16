package com.agapehill.agape_hill_backend.service;

import java.util.List;
import java.util.UUID;

import com.agapehill.agape_hill_backend.dto.request.FeeStructureItemRequest;
import com.agapehill.agape_hill_backend.dto.request.FeeStructureRequest;
import com.agapehill.agape_hill_backend.dto.response.FeeStructureItemResponse;
import com.agapehill.agape_hill_backend.dto.response.FeeStructureResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;

import reactor.core.publisher.Mono;

public interface FeeStructureService {
    Mono<WsResponse<List<FeeStructureResponse>>> getFeeStructures(UUID academicYearId, UUID termId, String classGroup);
    Mono<WsResponse<FeeStructureResponse>> getFeeStructure(UUID feeStructureId);
    Mono<WsResponse<FeeStructureResponse>> createFeeStructure(FeeStructureRequest request);
    Mono<WsResponse<FeeStructureResponse>> updateFeeStructure(UUID feeStructureId, FeeStructureRequest request);
    Mono<WsResponse<FeeStructureResponse>> publishFeeStructure(UUID feeStructureId);
    Mono<WsResponse<FeeStructureResponse>> archiveFeeStructure(UUID feeStructureId);
    Mono<WsResponse<FeeStructureResponse>> duplicateFeeStructure(UUID feeStructureId);
    Mono<WsResponse<FeeStructureItemResponse>> addFeeStructureItem(UUID feeStructureId, FeeStructureItemRequest request);
    Mono<WsResponse<FeeStructureItemResponse>> updateFeeStructureItem(UUID feeStructureId, UUID itemId, FeeStructureItemRequest request);
    Mono<WsResponse<Void>> deleteFeeStructureItem(UUID feeStructureId, UUID itemId);
}
