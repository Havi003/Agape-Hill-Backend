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

import com.agapehill.agape_hill_backend.dto.request.AcademicTermRequest;
import com.agapehill.agape_hill_backend.dto.request.AcademicYearRequest;
import com.agapehill.agape_hill_backend.dto.response.AcademicTermResponse;
import com.agapehill.agape_hill_backend.dto.response.AcademicYearResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.service.AcademicCalendarService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AcademicCalendarController {

    private final AcademicCalendarService academicCalendarService;

    @GetMapping("/api/academic-years")
    public Mono<WsResponse<List<AcademicYearResponse>>> getAcademicYears() {
        return academicCalendarService.getAcademicYears();
    }

    @PostMapping("/api/academic-years")
    public Mono<WsResponse<AcademicYearResponse>> createAcademicYear(@RequestBody AcademicYearRequest request) {
        return academicCalendarService.createAcademicYear(request);
    }

    @PutMapping("/api/academic-years/{yearId}")
    public Mono<WsResponse<AcademicYearResponse>> updateAcademicYear(@PathVariable UUID yearId, @RequestBody AcademicYearRequest request) {
        return academicCalendarService.updateAcademicYear(yearId, request);
    }

    @PostMapping("/api/academic-years/{yearId}/activate")
    public Mono<WsResponse<AcademicYearResponse>> activateAcademicYear(@PathVariable UUID yearId) {
        return academicCalendarService.activateAcademicYear(yearId);
    }

    @GetMapping("/api/academic-terms")
    public Mono<WsResponse<List<AcademicTermResponse>>> getAcademicTerms(@RequestParam(required = false) UUID academicYearId) {
        return academicCalendarService.getAcademicTerms(academicYearId);
    }

    @PostMapping("/api/academic-terms")
    public Mono<WsResponse<AcademicTermResponse>> createAcademicTerm(@RequestBody AcademicTermRequest request) {
        return academicCalendarService.createAcademicTerm(request);
    }

    @PutMapping("/api/academic-terms/{termId}")
    public Mono<WsResponse<AcademicTermResponse>> updateAcademicTerm(@PathVariable UUID termId, @RequestBody AcademicTermRequest request) {
        return academicCalendarService.updateAcademicTerm(termId, request);
    }

    @PostMapping("/api/academic-terms/{termId}/activate")
    public Mono<WsResponse<AcademicTermResponse>> activateAcademicTerm(@PathVariable UUID termId) {
        return academicCalendarService.activateAcademicTerm(termId);
    }

    @PostMapping("/api/academic-terms/{termId}/close")
    public Mono<WsResponse<AcademicTermResponse>> closeAcademicTerm(@PathVariable UUID termId) {
        return academicCalendarService.closeAcademicTerm(termId);
    }
}
