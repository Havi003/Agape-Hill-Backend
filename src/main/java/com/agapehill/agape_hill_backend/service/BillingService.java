package com.agapehill.agape_hill_backend.service;

import java.util.List;
import java.util.UUID;

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

import reactor.core.publisher.Mono;

public interface BillingService {
    Mono<WsResponse<List<StudentFeeOptionResponse>>> getStudentFeeOptions(UUID studentId, UUID academicYearId, UUID termId);
    Mono<WsResponse<List<StudentFeeOptionResponse>>> updateStudentFeeOptions(UUID studentId, StudentFeeOptionsUpdateRequest request);
    Mono<WsResponse<List<BillingPreviewResponse>>> previewTermBills(GenerateBillsRequest request);
    Mono<WsResponse<GenerateBillsResponse>> generateTermBills(GenerateBillsRequest request);
    Mono<WsResponse<StudentStatementResponse>> getStudentStatement(UUID studentId, UUID academicYearId, UUID termId);
    Mono<WsResponse<StudentStatementResponse>> recalculateStudentBalance(UUID studentId);
    Mono<WsResponse<GenerateBillsResponse>> recalculateTermBalances(UUID termId);
    Mono<WsResponse<FeeDashboardResponse>> getFeeDashboard(UUID academicYearId, UUID termId);
    Mono<WsResponse<StudentPaymentResponse>> recordManualPayment(ManualPaymentRequest request);
}
