package com.techcourse.api.service.registration.multi.stripinglock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.service.registration.RegistrationService;
import com.techcourse.api.service.registration.multi.update.RegistrationRepositoryService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationStripingService implements RegistrationService {

    private final RegistrationRepositoryService registrationRepositoryService;

    // 과목별 락, 조건, 카운트를 묶기
    private static class CourseLockBundle {

        final ReentrantLock lock = new ReentrantLock();
        final Condition firstYearCondition = lock.newCondition();
        final Condition otherYearCondition = lock.newCondition();
        int firstYearCount = 0;
        int otherYearCount = 0;
    }

    private final ConcurrentHashMap<Long, CourseLockBundle> bundles = new ConcurrentHashMap<>();

    @Override
    public void courseRegistration(Student student, Course course, int firstYearLimit,
        int otherYearLimit) {

        CourseLockBundle bundle = bundles.computeIfAbsent(course.getId(),
            id -> new CourseLockBundle());
        ReentrantLock lock = bundle.lock;
        lock.lock();

        try {
            int grade = student.getGrade();
            if (grade == 1) {
                firstGrade(student, course, bundle, firstYearLimit);
            }
            if (grade != 1) {
                otherGrade(student, course, bundle, otherYearLimit);
            }
        } finally {
            lock.unlock();
        }
    }

    private void otherGrade(Student student, Course course, CourseLockBundle bundle,
        int otherYearLimit) {
        long nanos = TimeUnit.SECONDS.toNanos(3);
        try {
            if (bundle.otherYearCount >= otherYearLimit) {
                while (nanos > 0) {
                    nanos = bundle.otherYearCondition.awaitNanos(nanos);
                }
                registrationRepositoryService.saveWait(student, course.getId());
            }

            if (bundle.otherYearCount < otherYearLimit) {
                bundle.otherYearCount++;
                registrationRepositoryService.saveComplete(student, course.getId());
                bundle.otherYearCondition.signalAll();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void firstGrade(Student student, Course course,
        RegistrationStripingService.CourseLockBundle bundle,
        int firstYearLimit) {
        long nanos = TimeUnit.SECONDS.toNanos(3);
        try {
            if (bundle.firstYearCount >= firstYearLimit) {
                while (nanos > 0) {
                    nanos = bundle.firstYearCondition.awaitNanos(nanos);
                }
                registrationRepositoryService.saveWait(student, course.getId());
            }
            if (bundle.firstYearCount < firstYearLimit) {
                bundle.firstYearCount++;
                registrationRepositoryService.saveComplete(student, course.getId());
                bundle.firstYearCondition.signalAll();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
