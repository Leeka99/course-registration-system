package com.techcourse.api.service.registration.multi.update;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationRepositoryService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void saveComplete(Student student, Long courseId) {
        Course course = courseRepository.findByIdWithPessimisticLock(courseId).orElseThrow();
        course.increaseCapacity();
        courseRepository.save(course);

        registrationRepository.save(Registration.changeToComplete(student, course));
    }

    @Transactional
    public void saveWait(Student student, Long courseId) {
        Course course = courseRepository.findByIdWithPessimisticLock(courseId).orElseThrow();
        registrationRepository.save(Registration.changeToWait(student, course));
    }
}
