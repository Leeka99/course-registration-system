package com.techcourse.api.service.javalock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Primary
public class ApplyServiceReentrantLock implements ApplyService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition otherGradeQueue = lock.newCondition();
    private boolean firstYearProcessing = false;

    @Override
    @Transactional
    public void apply(Long studentId, Long courseId) {
        lock.lock();
        try {
            Student student = studentRepository.findById(studentId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();

            if (student.getGrade() == 1) {
                firstYearProcessing = true;
            }

            // 1학년 우선 정책 적용: 조건 만족할 때까지 대기
            while (student.getGrade() != 1 && firstYearProcessing) {
                try {
                    otherGradeQueue.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (course.isAvailable()) {
                course.increaseCapacity();
                courseRepository.save(course);
                registrationRepository.save(Registration.changeToComplete(student, course));
                return;
            }

            if (!course.isAvailable()) {
                registrationRepository.save(Registration.changeToWait(student, course));
            }

        } finally {
            firstYearProcessing = false;
            otherGradeQueue.signalAll();
            lock.unlock();
        }
    }
}

