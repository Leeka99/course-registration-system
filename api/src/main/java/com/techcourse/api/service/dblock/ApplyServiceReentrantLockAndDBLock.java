package com.techcourse.api.service.dblock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyServiceReentrantLockAndDBLock implements ApplyService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition otherGradeCondition = lock.newCondition();
    private final AtomicInteger firstYearRunning = new AtomicInteger(0);

    @Override
    public void apply(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        lock.lock();
        try {
            if (student.getGrade() == 1) {
                firstYearRunning.incrementAndGet();
            }
            while (student.getGrade() != 1 && firstYearRunning.get() > 0) {
                try {
                    otherGradeCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            courseRegistration(student, courseId);

            if (student.getGrade() == 1) {
                firstYearRunning.decrementAndGet();
                otherGradeCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void courseRegistration(Student student, Long courseId) {
        Course course = courseRepository.findByIdWithPessimisticLock(courseId).orElseThrow();

        if (course.isAvailable()) {
            course.increaseCapacity();
            courseRepository.save(course);

            registrationRepository.save(Registration.changeToComplete(student, course));

        } else {
            registrationRepository.save(Registration.changeToWait(student, course));
        }
    }
}