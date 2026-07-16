package com.agapehill.agape_hill_backend.service;

import java.util.List;
import java.util.UUID;

import com.agapehill.agape_hill_backend.dto.request.AcademicTermRequest;
import com.agapehill.agape_hill_backend.dto.request.AcademicYearRequest;
import com.agapehill.agape_hill_backend.dto.response.AcademicTermResponse;
import com.agapehill.agape_hill_backend.dto.response.AcademicYearResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;

import reactor.core.publisher.Mono;

public interface AcademicCalendarService {
    Mono<WsResponse<List<AcademicYearResponse>>> getAcademicYears();
    Mono<WsResponse<AcademicYearResponse>> createAcademicYear(AcademicYearRequest request);
    Mono<WsResponse<AcademicYearResponse>> updateAcademicYear(UUID yearId, AcademicYearRequest request);
    Mono<WsResponse<AcademicYearResponse>> activateAcademicYear(UUID yearId);
    Mono<WsResponse<List<AcademicTermResponse>>> getAcademicTerms(UUID academicYearId);
    Mono<WsResponse<AcademicTermResponse>> createAcademicTerm(AcademicTermRequest request);
    Mono<WsResponse<AcademicTermResponse>> updateAcademicTerm(UUID termId, AcademicTermRequest request);
    Mono<WsResponse<AcademicTermResponse>> activateAcademicTerm(UUID termId);
    Mono<WsResponse<AcademicTermResponse>> closeAcademicTerm(UUID termId);
}
