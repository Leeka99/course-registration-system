package com.techcourse.api.service.lock.striping;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.lock.ApplyService;
import com.techcourse.api.service.registration.multi.guava.RegistrationGuavaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class ApplyServiceGuavaLock implements ApplyService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationGuavaService registrationGuavaService;

    @Override
    public void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        int firstYearLimit = (int) (course.getMaxCapacity() * 0.9);
        int otherYearLimit = (int) (course.getMaxCapacity() * 0.1);
        registrationGuavaService.courseRegistration(student, course, firstYearLimit,
            otherYearLimit);
    }
}