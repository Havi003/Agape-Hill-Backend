package com.agapehill.agape_hill_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agapehill.agape_hill_backend.dto.request.FeeStructureItemRequest;
import com.agapehill.agape_hill_backend.dto.request.FeeStructureRequest;
import com.agapehill.agape_hill_backend.dto.response.FeeStructureItemResponse;
import com.agapehill.agape_hill_backend.dto.response.FeeStructureResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.service.FeeStructureService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/fee-structures")
@RequiredArgsConstructor
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    @GetMapping
    public Mono<WsResponse<List<FeeStructureResponse>>> getFeeStructures(
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) UUID termId,
            @RequestParam(required = false) String classGroup) {
        return feeStructureService.getFeeStructures(academicYearId, termId, classGroup);
    }

    @PostMapping
    public Mono<WsResponse<FeeStructureResponse>> createFeeStructure(@RequestBody FeeStructureRequest request) {
        return feeStructureService.createFeeStructure(request);
    }

    @GetMapping("/{feeStructureId}")
    public Mono<WsResponse<FeeStructureResponse>> getFeeStructure(@PathVariable UUID feeStructureId) {
        return feeStructureService.getFeeStructure(feeStructureId);
    }

    @PutMapping("/{feeStructureId}")
    public Mono<WsResponse<FeeStructureResponse>> updateFeeStructure(@PathVariable UUID feeStructureId, @RequestBody FeeStructureRequest request) {
        return feeStructureService.updateFeeStructure(feeStructureId, request);
    }

    @PostMapping("/{feeStructureId}/publish")
    public Mono<WsResponse<FeeStructureResponse>> publishFeeStructure(@PathVariable UUID feeStructureId) {
        return feeStructureService.publishFeeStructure(feeStructureId);
    }

    @PostMapping("/{feeStructureId}/archive")
    public Mono<WsResponse<FeeStructureResponse>> archiveFeeStructure(@PathVariable UUID feeStructureId) {
        return feeStructureService.archiveFeeStructure(feeStructureId);
    }

    @PostMapping("/{feeStructureId}/duplicate")
    public Mono<WsResponse<FeeStructureResponse>> duplicateFeeStructure(@PathVariable UUID feeStructureId) {
        return feeStructureService.duplicateFeeStructure(feeStructureId);
    }

    @PostMapping("/{feeStructureId}/items")
    public Mono<WsResponse<FeeStructureItemResponse>> addFeeStructureItem(@PathVariable UUID feeStructureId, @RequestBody FeeStructureItemRequest request) {
        return feeStructureService.addFeeStructureItem(feeStructureId, request);
    }

    @PutMapping("/{feeStructureId}/items/{itemId}")
    public Mono<WsResponse<FeeStructureItemResponse>> updateFeeStructureItem(
            @PathVariable UUID feeStructureId,
            @PathVariable UUID itemId,
            @RequestBody FeeStructureItemRequest request) {
        return feeStructureService.updateFeeStructureItem(feeStructureId, itemId, request);
    }

    @DeleteMapping("/{feeStructureId}/items/{itemId}")
    public Mono<WsResponse<Void>> deleteFeeStructureItem(@PathVariable UUID feeStructureId, @PathVariable UUID itemId) {
        return feeStructureService.deleteFeeStructureItem(feeStructureId, itemId);
    }
}
