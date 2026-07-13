package com.agapehill.agape_hill_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.agapehill.agape_hill_backend.dto.request.StudentRequest;
import com.agapehill.agape_hill_backend.dto.response.NextOfKinResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentDashboardResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.service.StudentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * POST /api/students/create
     * Action: Create a new student registration
     */
    @PostMapping("/create")
    public Mono<WsResponse<StudentResponse>> createStudent(@RequestBody StudentRequest request) {
        return studentService.createStudent(request);
    }

    /**
     * POST /api/students/bulk-create
     * Action: Create multiple student registrations in bulk (e.g., from an Excel-to-JSON parsing UI)
     */
    @PostMapping("/bulk-create")
    public Mono<WsResponse<List<StudentResponse>>> createStudentsInBulk(@RequestBody List<StudentRequest> requests) {
        return studentService.createStudentsInBulk(requests);
    }

    /**
     * GET /api/students
     * Action: List all students or search by name/admission number/NEMIS
     * Logic: If 'search' param is missing, returns the full list.
     */
    @GetMapping
    public Mono<WsResponse<List<StudentResponse>>> getAllStudents(
            @RequestParam(required = false) String search) {
        return studentService.getAllStudentsWithBalance(search);
    }

    /**
     * GET /api/students/dashboard-stats
     * Action: Retrieve summary data for the dashboard cards
     */
    @GetMapping("/dashboard-stats")
    public Mono<WsResponse<StudentDashboardResponse>> getStudentDashboardStats() {
        return studentService.getStudentDashboardStats();
    }

     /**
     * GET /api/students/{studentId}/next-of-kin
     * Action: Retrieve Next of Kin information for a given student
     */
    @GetMapping("/{studentId}/next-of-kin")
    public Mono<WsResponse<NextOfKinResponse>> getNextOfKinInformation(@PathVariable UUID studentId) {
        return studentService.getNextOfKinInformation(studentId);
    }
}
