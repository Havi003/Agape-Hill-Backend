package com.agapehill.agape_hill_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agapehill.agape_hill_backend.domain.entity.AcademicTermEntity;
import com.agapehill.agape_hill_backend.domain.entity.AcademicYearEntity;
import com.agapehill.agape_hill_backend.dto.request.AcademicTermRequest;
import com.agapehill.agape_hill_backend.dto.request.AcademicYearRequest;
import com.agapehill.agape_hill_backend.dto.response.AcademicTermResponse;
import com.agapehill.agape_hill_backend.dto.response.AcademicYearResponse;
import com.agapehill.agape_hill_backend.dto.response.WsHeader;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.repository.AcademicTermRepository;
import com.agapehill.agape_hill_backend.repository.AcademicYearRepository;
import com.agapehill.agape_hill_backend.service.AcademicCalendarService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AcademicCalendarServiceImpl implements AcademicCalendarService {

    private final AcademicYearRepository academicYearRepo;
    private final AcademicTermRepository academicTermRepo;

    @Override
    public Mono<WsResponse<List<AcademicYearResponse>>> getAcademicYears() {
        return academicYearRepo.findAll()
                .map(this::mapYear)
                .collectList()
                .map(years -> response("200", "Academic years retrieved", years));
    }

    @Override
    public Mono<WsResponse<AcademicYearResponse>> createAcademicYear(AcademicYearRequest request) {
        LocalDateTime now = LocalDateTime.now();
        AcademicYearEntity year = new AcademicYearEntity(
                null,
                request.getYearName(),
                request.getStartDate(),
                request.getEndDate(),
                request.isActive(),
                now,
                now
        );

        return academicYearRepo.save(year)
                .map(saved -> response("200", "Academic year created", mapYear(saved)));
    }

    @Override
    public Mono<WsResponse<AcademicYearResponse>> updateAcademicYear(UUID yearId, AcademicYearRequest request) {
        return academicYearRepo.findById(yearId)
                .switchIfEmpty(Mono.error(new RuntimeException("Academic year not found")))
                .flatMap(year -> {
                    year.setYearName(request.getYearName());
                    year.setStartDate(request.getStartDate());
                    year.setEndDate(request.getEndDate());
                    year.setActive(request.isActive());
                    year.setUpdatedAt(LocalDateTime.now());
                    return academicYearRepo.save(year);
                })
                .map(saved -> response("200", "Academic year updated", mapYear(saved)));
    }

    @Override
    @Transactional
    public Mono<WsResponse<AcademicYearResponse>> activateAcademicYear(UUID yearId) {
        return academicYearRepo.findAll()
                .flatMap(year -> {
                    year.setActive(year.getId().equals(yearId));
                    year.setUpdatedAt(LocalDateTime.now());
                    return academicYearRepo.save(year);
                })
                .filter(AcademicYearEntity::isActive)
                .single()
                .map(saved -> response("200", "Academic year activated", mapYear(saved)));
    }

    @Override
    public Mono<WsResponse<List<AcademicTermResponse>>> getAcademicTerms(UUID academicYearId) {
        return (academicYearId == null ? academicTermRepo.findAll() : academicTermRepo.findByAcademicYearId(academicYearId))
                .map(this::mapTerm)
                .collectList()
                .map(terms -> response("200", "Academic terms retrieved", terms));
    }

    @Override
    public Mono<WsResponse<AcademicTermResponse>> createAcademicTerm(AcademicTermRequest request) {
        LocalDateTime now = LocalDateTime.now();
        AcademicTermEntity term = new AcademicTermEntity(
                null,
                request.getAcademicYearId(),
                request.getTermName(),
                request.getStartDate(),
                request.getEndDate(),
                "UPCOMING",
                false,
                now,
                now
        );

        return academicTermRepo.save(term)
                .map(saved -> response("200", "Academic term created", mapTerm(saved)));
    }

    @Override
    public Mono<WsResponse<AcademicTermResponse>> updateAcademicTerm(UUID termId, AcademicTermRequest request) {
        return academicTermRepo.findById(termId)
                .switchIfEmpty(Mono.error(new RuntimeException("Academic term not found")))
                .flatMap(term -> {
                    term.setAcademicYearId(request.getAcademicYearId());
                    term.setTermName(request.getTermName());
                    term.setStartDate(request.getStartDate());
                    term.setEndDate(request.getEndDate());
                    term.setUpdatedAt(LocalDateTime.now());
                    return academicTermRepo.save(term);
                })
                .map(saved -> response("200", "Academic term updated", mapTerm(saved)));
    }

    @Override
    @Transactional
    public Mono<WsResponse<AcademicTermResponse>> activateAcademicTerm(UUID termId) {
        return academicTermRepo.findAll()
                .flatMap(term -> {
                    boolean selected = term.getId().equals(termId);
                    term.setActive(selected);
                    term.setStatus(selected ? "ACTIVE" : ("ACTIVE".equals(term.getStatus()) ? "UPCOMING" : term.getStatus()));
                    term.setUpdatedAt(LocalDateTime.now());
                    return academicTermRepo.save(term);
                })
                .filter(AcademicTermEntity::isActive)
                .single()
                .map(saved -> response("200", "Academic term activated", mapTerm(saved)));
    }

    @Override
    public Mono<WsResponse<AcademicTermResponse>> closeAcademicTerm(UUID termId) {
        return academicTermRepo.findById(termId)
                .switchIfEmpty(Mono.error(new RuntimeException("Academic term not found")))
                .flatMap(term -> {
                    term.setActive(false);
                    term.setStatus("CLOSED");
                    term.setUpdatedAt(LocalDateTime.now());
                    return academicTermRepo.save(term);
                })
                .map(saved -> response("200", "Academic term closed", mapTerm(saved)));
    }

    private AcademicYearResponse mapYear(AcademicYearEntity year) {
        return new AcademicYearResponse(year.getId(), year.getYearName(), year.getStartDate(), year.getEndDate(), year.isActive());
    }

    private AcademicTermResponse mapTerm(AcademicTermEntity term) {
        return new AcademicTermResponse(term.getId(), term.getAcademicYearId(), term.getTermName(), term.getStartDate(), term.getEndDate(), term.getStatus(), term.isActive());
    }

    private <T> WsResponse<T> response(String code, String message, T body) {
        return new WsResponse<>(new WsHeader(code, message), body);
    }
}
