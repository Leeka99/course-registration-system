package com.techcourse.api.service.lock.striping;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.lock.ApplyService;
import com.techcourse.api.service.registration.multi.RegistrationStripingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyServiceStripingLock implements ApplyService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationStripingService registrationStripingService;

    @Override
    @Transactional
    public void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findByIdWithOptimisticLock(courseId).orElseThrow();
        int firstYearLimit = (int) (course.getMaxCapacity() * 0.9);
        int otherYearLimit = (int) (course.getMaxCapacity() * 0.1);
        registrationStripingService.courseRegistration(student, course, firstYearLimit, otherYearLimit);
    }
}