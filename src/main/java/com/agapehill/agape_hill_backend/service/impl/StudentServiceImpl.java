package com.agapehill.agape_hill_backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agapehill.agape_hill_backend.domain.entity.FeeStatusEntity;
import com.agapehill.agape_hill_backend.domain.entity.NextOfKinEntity;
import com.agapehill.agape_hill_backend.domain.entity.StudentEntity;
import com.agapehill.agape_hill_backend.dto.request.NextOfKinRequest;
import com.agapehill.agape_hill_backend.dto.request.StudentRequest;
import com.agapehill.agape_hill_backend.dto.response.*;
import com.agapehill.agape_hill_backend.repository.*;
import com.agapehill.agape_hill_backend.service.StudentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
private final StudentRepository studentRepo;
    private final NextOfKinRepository nokRepo;
    private final FeesStatusRepository feeStatusRepo;

    // =========================================================
    // 1. CREATE SINGLE STUDENT
    // =========================================================
    @Override
    @Transactional
    public Mono<WsResponse<StudentResponse>> createStudent(StudentRequest request) {
        return saveStudentEntityGroup(request)
                .map(response -> new WsResponse<>(
                        new WsHeader("200", "Student Registered Successfully"),
                        response
                ));
    }

    // =========================================================
    // 2. CREATE STUDENTS IN BULK (New Functionality)
    // =========================================================
    @Override
    @Transactional
    public Mono<WsResponse<List<StudentResponse>>> createStudentsInBulk(List<StudentRequest> requests) {
        return Flux.fromIterable(requests)
                .concatMap(this::saveStudentEntityGroup)
                .collectList()
                .map(savedStudents -> new WsResponse<>(
                        new WsHeader("200", savedStudents.size() + " Students Registered Successfully"),
                        savedStudents
                ));
    }

    // =========================================================
    // HELPER: CORE CREATION LOGIC (Used by Single & Bulk)
    // =========================================================
    private Mono<StudentResponse> saveStudentEntityGroup(StudentRequest request) {
        BigDecimal billed = request.getTotalBilled() != null ? request.getTotalBilled() : BigDecimal.ZERO;
        BigDecimal paid = request.getTotalPaid() != null ? request.getTotalPaid() : BigDecimal.ZERO;
        BigDecimal balance = billed.subtract(paid);

        return generateAdmissionNumber()
                .flatMap(admissionNumber -> saveStudentEntityGroup(request, billed, paid, balance, admissionNumber));
    }

    private Mono<String> generateAdmissionNumber() {
        return studentRepo.findHighestAgapeHillAdmissionSequence()
                .defaultIfEmpty(0)
                .map(highestSequence -> String.format("AH%03d", highestSequence + 1));
    }

    private Mono<StudentResponse> saveStudentEntityGroup(
            StudentRequest request,
            BigDecimal billed,
            BigDecimal paid,
            BigDecimal balance,
            String admissionNumber
    ) {
        StudentEntity student = new StudentEntity(
                null,
                admissionNumber,
                request.getFullName(),
                request.getStudentGender(),
                request.getNemisNumber(),
                request.getDateOfBirth(),
                request.getStudentClass(),

                LocalDate.now()
        );

        return studentRepo.save(student)
                .flatMap(savedStudent -> {
                    FeeStatusEntity feeStatus = new FeeStatusEntity(
                            savedStudent.getId(), billed, paid, balance, true
                    );

                    NextOfKinEntity nextOfKin = new NextOfKinEntity(
                            null, savedStudent.getId(),
                            request.getKinName(), request.getKinRelationship(),
                            request.getKinContact(), request.getKinAddress(),
                            request.getKinEmail()
                    );

                    return feeStatusRepo.save(feeStatus)
                            .flatMap(savedFee -> nokRepo.save(nextOfKin)
                                    .map(savedNok -> {
                                        FeeStatusResponse feeDto = new FeeStatusResponse(
                                                savedFee.getTotalBilled(),
                                                savedFee.getTotalPaid(),
                                                savedFee.getBalance()
                                        );

                                        return new StudentResponse(
                                                savedStudent.getId(),
                                                savedStudent.getAdmissionNumber(),
                                                savedStudent.getFullName(),
                                                savedStudent.getStudentClass(),
                                                savedStudent.getGender(),
                                                savedStudent.getNemisNumber(),
                                                savedStudent.getRegisteredDate(),
                                                savedStudent.getDateOfBirth(),
                                                feeDto,
                                                savedNok.getKinName(),
                                                savedNok.getKinRelationship(),
                                                savedNok.getKinContact()
                                        );
                                    })
                            );
                });
    }

    // =========================================================
    // 2. DASHBOARD STATS
    // =========================================================
    public Mono<WsResponse<StudentDashboardResponse>> getStudentDashboardStats() {

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        return Mono.zip(
                        studentRepo.count(),
                        studentRepo.countByGenderIgnoreCase("Male"),
                        studentRepo.countByGenderIgnoreCase("Female"),
                        studentRepo.countByRegisteredDateAfter(firstDayOfMonth.minusDays(1))
                )
                .map(tuple -> {

                    long total = tuple.getT1();
                    long male = tuple.getT2();
                    long female = tuple.getT3();
                    long newThisMonth = tuple.getT4();

                    double malePerc = total > 0 ? (male * 100.0) / total : 0;
                    double femalePerc = total > 0 ? (female * 100.0) / total : 0;

                    StudentDashboardResponse stats = new StudentDashboardResponse(
                            total,
                            male,
                            female,
                            newThisMonth,
                            malePerc,
                            femalePerc
                    );

                    return new WsResponse<>(
                            new WsHeader("200", "Dashboard data Retrieved"),
                            stats
                    );
                });
    }

    // =========================================================
    // 3. GET ALL STUDENTS (Fixed: Added defaultIfEmpty to feeStatusRepo)
    // =========================================================
    public Mono<WsResponse<List<StudentResponse>>> getAllStudentsWithBalance(String search) {

        Flux<StudentEntity> studentFlux =
                (search == null || search.isBlank())
                        ? studentRepo.findAll()
                        : studentRepo.searchStudents(search);

        return studentFlux
                .flatMap(student ->

                        Mono.zip(
                                        feeStatusRepo.findById(student.getId())
                                                // Default added here so zip doesn't cancel if a fee record is missing
                                                .defaultIfEmpty(new FeeStatusEntity(student.getId(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, true)),
                                        nokRepo.findByStudentId(student.getId())
                                                .defaultIfEmpty(
                                                new NextOfKinEntity(
                                                        null,
                                                        student.getId(),
                                                        "N/A",
                                                        "N/A",
                                                        "N/A",
                                                        null,
                                                        null
                                                )
                                        )
                                )
                                .map(tuple -> {

                                    FeeStatusEntity fee = tuple.getT1();
                                    NextOfKinEntity nok = tuple.getT2();

                                    FeeStatusResponse feeDto = new FeeStatusResponse(
                                            fee.getTotalBilled(),
                                            fee.getTotalPaid(),
                                            fee.getBalance()
                                    );

                                    return new StudentResponse(
                                            student.getId(),
                                            student.getAdmissionNumber(),
                                            student.getFullName(),
                                            student.getStudentClass(),
                                            student.getGender(),
                                            student.getNemisNumber(),
                                            student.getRegisteredDate(),
                                            student.getDateOfBirth(),
                                            feeDto,
                                            nok.getKinName(),
                                            nok.getKinRelationship(),
                                            nok.getKinContact()
                                    );
                                })
                )
                .collectList()
                .map(list -> new WsResponse<>(
                        new WsHeader("200", "Students retrieved successfully"),
                        list
                ));
    }

    // =========================================================
    // 4. GET NEXT OF KIN 
    // =========================================================
    @Override
    public Mono<WsResponse<NextOfKinResponse>> getNextOfKinInformation(UUID studentId) {

        return nokRepo.findByStudentId(studentId)
                .map(nok -> new WsResponse<>(
                        new WsHeader("200", "Next of Kin Retrieved Successfully"),
                        mapNextOfKinResponse(nok)
                ))
                .switchIfEmpty(
                        Mono.error(new RuntimeException("Next of Kin not found"))
                );
    }

    // =========================================================
    // 5. UPDATE NEXT OF KIN
    // =========================================================
    @Override
    public Mono<WsResponse<NextOfKinResponse>> updateNextOfKinInformation(UUID studentId, NextOfKinRequest request) {

        return nokRepo.findByStudentId(studentId)
                .switchIfEmpty(Mono.error(new RuntimeException("Next of Kin not found")))
                .flatMap(nok -> {
                    nok.setKinName(request.getName());
                    nok.setKinRelationship(request.getRelationship());
                    nok.setKinContact(request.getPhoneNumber());
                    nok.setKinEmail(request.getEmail());
                    nok.setKinAddress(request.getAddress());

                    return nokRepo.save(nok);
                })
                .map(savedNok -> new WsResponse<>(
                        new WsHeader("200", "Next of Kin Updated Successfully"),
                        mapNextOfKinResponse(savedNok)
                ));
    }

    private NextOfKinResponse mapNextOfKinResponse(NextOfKinEntity nok) {
        return new NextOfKinResponse(
                nok.getKinName(),
                nok.getKinRelationship(),
                nok.getKinContact(),
                nok.getKinEmail(),
                nok.getKinAddress()
        );
    }
}
