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
import com.agapehill.agape_hill_backend.dto.request.StudentRequest;
import com.agapehill.agape_hill_backend.dto.response.StudentDashboardResponse;
import com.agapehill.agape_hill_backend.dto.response.StudentResponse;
import com.agapehill.agape_hill_backend.dto.response.WsHeader;
import com.agapehill.agape_hill_backend.dto.response.WsResponse;
import com.agapehill.agape_hill_backend.repository.FeesStatusRepository;
import com.agapehill.agape_hill_backend.repository.NextOfKinRepository;
import com.agapehill.agape_hill_backend.repository.StudentRepository;
import com.agapehill.agape_hill_backend.service.StudentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{

  //1. Method to create new Students
    private final StudentRepository studentRepo;
    private final NextOfKinRepository nokRepo;
    private final FeesStatusRepository feeStatusRepo;


@Override
@Transactional
public Mono<WsResponse<StudentResponse>> createStudent(StudentRequest request) {

    UUID studentId = UUID.randomUUID();

    // 1. Safety Check for fee values
    // If frontend sends null, we default to 0.00 to prevent the crash
    BigDecimal billed = (request.getTotalBilled() != null) ? request.getTotalBilled() : BigDecimal.ZERO;
    BigDecimal paid = (request.getTotalPaid() != null) ? request.getTotalPaid() : BigDecimal.ZERO;
    BigDecimal balance = billed.subtract(paid);

    StudentEntity student = new StudentEntity(
        studentId,
        "ADM" + System.currentTimeMillis(), 
        request.getFullName(),
        request.getGender(),
        request.getDateOfBirth(),
        request.getStudentClass(),
        LocalDate.now(),
        true
    );

    NextOfKinEntity nextOfKin = new NextOfKinEntity(
        studentId,
        request.getKinName(),
        request.getKinRelationship(),
        request.getKinContact(),
        request.getKinAdress(),
        true
    );

    // 2. Use the pre-calculated safe variables here
    FeeStatusEntity feeStatus = new FeeStatusEntity(
        studentId,
        billed,
        paid,
        balance,
        true
    );

    return studentRepo.save(student)
        .flatMap(savedStudent -> feeStatusRepo.save(feeStatus)
            .flatMap(savedFeeStatus -> nokRepo.save(nextOfKin)
                .map(savedNextOfKin -> new WsResponse<>(
                    new WsHeader("200", "Student Registered Successfully"),
                    new StudentResponse(
                        savedStudent.getId(),
                        savedStudent.getAdmissionNumber(),
                        savedStudent.getFullName(),
                        savedStudent.getStudentClass(),
                        savedFeeStatus.getBalance(),
                        savedNextOfKin.getKinName(),
                        savedNextOfKin.getKinRelationship(),
                        savedNextOfKin.getKinContact()
                    )
                ))
            )
        );
}

    // 2.  Method to get the statisttics dashboard

    public Mono<WsResponse <StudentDashboardResponse>> getStudentDashboardStats(){

    //get the first day of the month
    LocalDate firstdayOfMonth = LocalDate.now().withDayOfMonth(1);

    return Mono.zip(
     studentRepo.count()
    ,studentRepo.countByGenderIgnoreCase("Male")
    ,studentRepo.countByGenderIgnoreCase("Female")
    , studentRepo.countByRegisteredDateAfter(firstdayOfMonth.minusDays(1))
   ).map(tuple ->{
     long total = tuple.getT1();
     long male = tuple.getT2();
     long female = tuple.getT3();
     long newThisMonth = tuple.getT4();

     // Calculate percentages
        double malePerc = total > 0 ? (double) male / total * 100 : 0;
        double femalePerc = total > 0 ? (double) female / total * 100 : 0;

        StudentDashboardResponse stats = new StudentDashboardResponse(
            total, male, female, newThisMonth, malePerc, femalePerc
        );

        return new WsResponse<>(new WsHeader("200", "Dashboard data Retrieved"),stats);

   });

    }

    //3.  Search for students by name nemis or admission number

  public Mono<WsResponse<List<StudentResponse>>> getAllStudentsWithBalance(String search) {
    LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1); // For "This Month" logic

    Flux<StudentEntity> studentFlux = (search == null || search.isBlank()) 
        ? studentRepo.findAll() 
        : studentRepo.searchStudents(search);

    return studentFlux.flatMap(student -> 
    Mono.zip(
        feeStatusRepo.findById(student.getId()),
        nokRepo.findById(student.getId())
    )
    .map(tuple -> {
        FeeStatusEntity fee = tuple.getT1();
        NextOfKinEntity nok = tuple.getT2();
        return new StudentResponse(
            student.getId(),
            student.getAdmissionNumber(),
            student.getFullName(),
            student.getStudentClass(),
            fee.getBalance(),
            nok.getKinName(),
            nok.getKinRelationship(),
            nok.getKinContact()
        );
    })
)
    .collectList() // Converts Flux<StudentResponse> to Mono<List<StudentResponse>>
    .map(studentList -> new WsResponse<>(
        new WsHeader("200", "Students retrieved successfully"),
        studentList
    ));
}
}
