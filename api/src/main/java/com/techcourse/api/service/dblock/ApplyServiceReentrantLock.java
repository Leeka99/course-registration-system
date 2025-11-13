package com.techcourse.api.service.dblock;

import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
import com.techcourse.api.service.RegistrationService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyServiceReentrantLock implements ApplyService {

    private final StudentRepository studentRepository;
    private final RegistrationService registrationService;

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

            registrationService.courseRegistration(student, courseId);

            if (student.getGrade() == 1) {
                firstYearRunning.decrementAndGet();
                otherGradeCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}