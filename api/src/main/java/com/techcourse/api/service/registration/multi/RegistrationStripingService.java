package com.techcourse.api.service.registration.multi;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.service.registration.RegistrationService;
import java.util.HashMap;
import java.util.Map;
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

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition firstYearCondition = lock.newCondition();
    private final Condition otherYearCondition = lock.newCondition();

    long nanos = TimeUnit.SECONDS.toNanos(3);

    Map<Long, Integer> firstYearCount = new HashMap<>();
    Map<Long, Integer> otherYearCount = new HashMap<>();

    @Override
    public void courseRegistration(Student student, Course course, int firstYearLimit,
        int otherYearLimit) {
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
        try {
            int number = otherYearCount.getOrDefault(course.getId(), 0);
            while (number >= otherYearLimit && nanos > 0) {
                nanos = otherYearCondition.awaitNanos(nanos);
            }
            if (number < otherYearLimit) {
                otherYearCount.put(course.getId(), number + 1);
                registration(student, course);
                otherYearCondition.signalAll();
                return;
            }
            registrationRepository.save(Registration.changeToWait(student, course));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void firstGrade(Student student, Course course, int firstYearLimit) {
        try {
            int number = firstYearCount.getOrDefault(course.getId(), 0);
            while (number >= firstYearLimit && nanos > 0) {
                nanos = firstYearCondition.awaitNanos(nanos);
            }
            if (number < firstYearLimit) {
                firstYearCount.put(course.getId(), number + 1);
                registration(student, course);
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
