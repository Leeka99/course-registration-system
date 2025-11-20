package com.techcourse.api.service.registration.multi.guava;

import com.google.common.util.concurrent.Striped;
import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.service.registration.RegistrationService;
import com.techcourse.api.service.registration.multi.update.RegistrationRepositoryService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationGuavaService implements RegistrationService {

    private final RegistrationRepositoryService registrationRepositoryService;

    private static class CourseCountBundle {

        int firstYearCount = 0;
        int otherYearCount = 0;
        final Condition firstYearCondition;
        final Condition otherYearCondition;

        CourseCountBundle(Lock lock) {
            this.firstYearCondition = lock.newCondition();
            this.otherYearCondition = lock.newCondition();
        }
    }

    private final ConcurrentHashMap<Long, CourseCountBundle> bundles = new ConcurrentHashMap<>();

    private final Striped<Lock> stripedLock = Striped.lock(50);

    @Override
    public void courseRegistration(Student student, Course course, int firstYearLimit,
        int otherYearLimit) {

        Lock lock = stripedLock.get(course.getId());
        lock.lock();

        try {
            CourseCountBundle bundle = bundles.computeIfAbsent(course.getId(),
                id -> new CourseCountBundle(lock));

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

    private void otherGrade(Student student, Course course, CourseCountBundle bundle,
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
        CourseCountBundle bundle,
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
