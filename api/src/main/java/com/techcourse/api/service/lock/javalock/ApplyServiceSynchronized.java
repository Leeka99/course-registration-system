package com.techcourse.api.service.lock.javalock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.lock.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyServiceSynchronized implements ApplyService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public synchronized void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        if (course.isAvailable()) {
            course.increaseCapacity();
            courseRepository.save(course);
            registrationRepository.save(Registration.changeToComplete(student, course));
            return;
        }
        if (!course.isAvailable()) {
            registrationRepository.save(Registration.changeToWait(student, course));
        }
    }
}

