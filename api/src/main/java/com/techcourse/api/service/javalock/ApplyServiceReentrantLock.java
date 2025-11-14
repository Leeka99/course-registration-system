package com.techcourse.api.service.javalock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
import com.techcourse.api.service.RegistrationReenrantLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyServiceReentrantLock implements ApplyService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationReenrantLockService registrationReenrantLockService;

    @Override
    public void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findAll().get(0);
        int firstYearLimit = (int) (course.getMaxCapacity() * 0.9);
        int otherYearLimit = (int) (course.getMaxCapacity() * 0.1);
        registrationReenrantLockService.courseRegistration(student, course, firstYearLimit,
            otherYearLimit);
    }
}