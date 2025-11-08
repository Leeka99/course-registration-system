package com.techcourse.api.service;

import com.techcourse.api.domain.Course;
import com.techcourse.api.domain.Registration;
import com.techcourse.api.domain.ShowRegistrationResponse;
import com.techcourse.api.domain.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public ApplyService(RegistrationRepository registrationRepository,
        StudentRepository studentRepository, CourseRepository courseRepository) {
        this.registrationRepository = registrationRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        // 수강 가능 인원이 남아있다면
        if (course.isAvailable()) {
            course.increaseCapacity();
            courseRepository.save(course);
            registrationRepository.save(Registration.changeToComplete(student, course));
            return;
        }
        if (!course.isAvailable()) {
            // 가득 찼다면 대기순번으로
            registrationRepository.save(Registration.changeToWait(student, course));
        }
    }

    public List<ShowRegistrationResponse> showRegistration() {
        List<Registration> registrations = registrationRepository.findAll();

        List<ShowRegistrationResponse> responses = new ArrayList<>();

        for (Registration registration : registrations) {
            Student student = studentRepository.findById(registration.getStudent().getId())
                .orElseThrow();
            Course course = courseRepository.findById(registration.getCourse().getId())
                .orElseThrow();

            ShowRegistrationResponse dto = new ShowRegistrationResponse(
                course.getTitle(),
                registration.getStatus(),
                student.getName(),
                student.getGrade(),
                registration.getTime()
            );
            responses.add(dto);
        }

        return responses;
    }

}

