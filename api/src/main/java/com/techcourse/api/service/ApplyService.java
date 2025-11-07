package com.techcourse.api.service;

import com.techcourse.api.domain.Course;
import com.techcourse.api.domain.Registration;
import com.techcourse.api.domain.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
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
            registrationRepository.save(Registration.changeToComplete(student, course));
        }

        // 가득 찼다면 대기순번으로
        registrationRepository.save(Registration.changeToWait(student, course));
    }
}

