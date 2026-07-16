package com.agapehill.agape_hill_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agapehill.agape_hill_backend.dto.request.GenerateBillsRequest;
import com.agapehill.agape_hill_backend.dto.request.ManualPaymentRequest;
import com.agapehill.agape_hill_backend.dto.request.StudentFeeOptionsUpdateRequest;
import com.agapehill.agape_hill_backend.dto.response.BillingPreviewResponse;
import com.agapehill.agape_hill_backend.dto.response.FeeDashboardResponse;
import com.agapehill.agape_hill_backend.dto.response.GenerateBillsResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentFeeOptionResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentPaymentResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentStatementResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.service.BillingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/api/students/{studentId}/fee-options")
    public Mono<WsResponse<List<StudentFeeOptionResponse>>> getStudentFeeOptions(
            @PathVariable UUID studentId,
            @RequestParam UUID academicYearId,
            @RequestParam UUID termId) {
        return billingService.getStudentFeeOptions(studentId, academicYearId, termId);
    }

    @PutMapping("/api/students/{studentId}/fee-options")
    public Mono<WsResponse<List<StudentFeeOptionResponse>>> updateStudentFeeOptions(
            @PathVariable UUID studentId,
            @RequestBody StudentFeeOptionsUpdateRequest request) {
        return billingService.updateStudentFeeOptions(studentId, request);
    }

    @PostMapping("/api/billing/terms/{termId}/preview")
    public Mono<WsResponse<List<BillingPreviewResponse>>> previewTermBills(
            @PathVariable UUID termId,
            @RequestBody GenerateBillsRequest request) {
        request.setTermId(termId);
        return billingService.previewTermBills(request);
    }

    @PostMapping("/api/billing/terms/{termId}/generate")
    public Mono<WsResponse<GenerateBillsResponse>> generateTermBills(
            @PathVariable UUID termId,
            @RequestBody GenerateBillsRequest request) {
        request.setTermId(termId);
        return billingService.generateTermBills(request);
    }

    @PostMapping("/api/billing/terms/{termId}/recalculate")
    public Mono<WsResponse<GenerateBillsResponse>> recalculateTermBalances(@PathVariable UUID termId) {
        return billingService.recalculateTermBalances(termId);
    }

    @GetMapping("/api/billing/dashboard")
    public Mono<WsResponse<FeeDashboardResponse>> getFeeDashboard(
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) UUID termId) {
        return billingService.getFeeDashboard(academicYearId, termId);
    }

    @GetMapping("/api/billing/students/{studentId}/statement")
    public Mono<WsResponse<StudentStatementResponse>> getStudentStatement(
            @PathVariable UUID studentId,
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) UUID termId) {
        return billingService.getStudentStatement(studentId, academicYearId, termId);
    }

    @PostMapping("/api/billing/students/{studentId}/recalculate")
    public Mono<WsResponse<StudentStatementResponse>> recalculateStudentBalance(@PathVariable UUID studentId) {
        return billingService.recalculateStudentBalance(studentId);
    }

    @PostMapping("/api/payments/manual")
    public Mono<WsResponse<StudentPaymentResponse>> recordManualPayment(@RequestBody ManualPaymentRequest request) {
        return billingService.recordManualPayment(request);
    }
}
