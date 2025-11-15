package com.techcourse.api.service.lock.dblock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.lock.ApplyService;
import com.techcourse.api.service.registration.single.RegistrationSingleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class ApplyServicePessimisticLock implements ApplyService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationSingleService registrationSingleService;

    @Override
    @Transactional
    public void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findByIdWithPessimisticLock(courseId).orElseThrow();
        int firstYearLimit = (int) (course.getMaxCapacity() * 0.9);
        int otherYearLimit = (int) (course.getMaxCapacity() * 0.1);
        registrationSingleService.courseRegistration(student, course, firstYearLimit,
            otherYearLimit);
    }
}