package com.techcourse.api.service;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void courseRegistration(Student student, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();

        if (course.isAvailable()) {
            course.increaseCapacity();
            courseRepository.save(course);

            registrationRepository.save(
                com.techcourse.api.domain.entity.Registration.changeToComplete(student, course));

        } else {
            registrationRepository.save(
                com.techcourse.api.domain.entity.Registration.changeToWait(student, course));
        }
    }
}
