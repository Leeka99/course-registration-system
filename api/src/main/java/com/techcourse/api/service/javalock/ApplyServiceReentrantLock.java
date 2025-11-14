package com.techcourse.api.service.javalock;

import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
import com.techcourse.api.service.RegistrationReenrantLockService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplyServiceReentrantLock implements ApplyService {

    private final StudentRepository studentRepository;
    private final ReentrantLock lock = new ReentrantLock();
    private final RegistrationReenrantLockService registrationReenrantLockService;

    @Override
    public void apply(Long studentId, Long courseId) {
        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                return;
            }
            Student student = studentRepository.findById(studentId).orElseThrow();

            registrationReenrantLockService.courseRegistration(student);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) { // 현재 스레드가 lock을 가지고 있는지 확인
                lock.unlock();
            }
        }
    }
}