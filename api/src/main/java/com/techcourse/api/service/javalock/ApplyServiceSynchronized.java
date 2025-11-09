package com.techcourse.api.service.javalock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
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
}

