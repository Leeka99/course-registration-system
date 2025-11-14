package com.techcourse.api.service;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationReenrantLockService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;

    private int firstYearCount = 0;
    private int otherYearCount = 0;

    private int firstYearLimit;
    private int otherYearLimit;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition firstYearCondition = lock.newCondition();
    private final Condition otherYearCondition = lock.newCondition();

    @Transactional
    public void courseRegistration(Student student) {
        Course course = courseRepository.findAll().get(0);
        firstYearLimit = (int) (course.getMaxCapacity() * 0.9);
        otherYearLimit = (int) (course.getMaxCapacity() * 0.1);
        lock.lock();
        try {
            int grade = student.getGrade();
            if (grade == 1) {
                firstGrade(student, course, firstYearLimit);
            }
            if (grade != 1) {
                otherGrade(student, course, otherYearLimit);
            }
        } finally {
            lock.unlock();
        }
    }

    private void otherGrade(Student student, Course course, int otherYearLimit) {
        long nanos = TimeUnit.SECONDS.toNanos(3);

        try {
            while (otherYearCount >= otherYearLimit && nanos > 0) {
                nanos = otherYearCondition.awaitNanos(nanos);
            }
            if (otherYearCount < otherYearLimit) {
                registration(student, course);
                otherYearCount++;
                otherYearCondition.signalAll();
                return;
            }
            registrationRepository.save(Registration.changeToWait(student, course));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void firstGrade(Student student, Course course, int firstYearLimit) {
        long nanos = TimeUnit.SECONDS.toNanos(3);
        try {
            while (firstYearCount >= firstYearLimit && nanos > 0) {
                nanos = firstYearCondition.awaitNanos(nanos);
            }
            if (firstYearCount < firstYearLimit) {
                registration(student, course);
                firstYearCount++;
                firstYearCondition.signalAll();
                return;
            }
            registrationRepository.save(Registration.changeToWait(student, course));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void registration(Student student, Course course) {
        course.increaseCapacity();
        courseRepository.save(course);

        registrationRepository.save(Registration.changeToComplete(student, course));
    }
}
