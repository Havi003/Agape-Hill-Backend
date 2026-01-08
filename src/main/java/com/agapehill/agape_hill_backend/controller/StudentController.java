package com.agapehill.agape_hill_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.agapehill.agape_hill_backend.dto.request.StudentRequest;
import com.agapehill.agape_hill_backend.dto.response.StudentDashboardResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentResponse;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.service.StudentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
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
}