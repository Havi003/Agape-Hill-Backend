package com.agapehill.agape_hill_backend.service;

import java.util.List;
import java.util.UUID;

import com.agapehill.agape_hill_backend.dto.request.StudentRequest;
import com.agapehill.agape_hill_backend.dto.response.NextOfKinResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentDashboardResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;

import reactor.core.publisher.Mono;

public interface StudentService {
    Mono<WsResponse <StudentResponse>> createStudent (StudentRequest request);
    Mono<WsResponse <StudentDashboardResponse>> getStudentDashboardStats();
    Mono <WsResponse <List<StudentResponse>>> getAllStudentsWithBalance (String search);
    Mono<WsResponse<NextOfKinResponse>> getNextOfKinInformation(UUID studentId);
    Mono<WsResponse<List<StudentResponse>>> createStudentsInBulk(List<StudentRequest> requests);

}
