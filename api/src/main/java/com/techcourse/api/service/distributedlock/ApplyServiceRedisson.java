package com.techcourse.api.service.distributedlock;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.ApplyService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Primary
public class ApplyServiceRedisson implements ApplyService {

    private final RedissonClient redissonClient;
    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void apply(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        RLock lock = redissonClient.getLock("applyLock:" + courseId);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return;
            }

            // Redis에 임시 대기열 등록
            RQueue<Long> firstQueue = redissonClient.getQueue("firstGrade:" + courseId);
            RQueue<Long> otherQueue = redissonClient.getQueue("otherGrade:" + courseId);

            if (student.getGrade() == 1) {
                firstQueue.add(studentId);
            } else {
                otherQueue.add(studentId);
            }

            // 1학년 우선 처리
            while (!firstQueue.isEmpty()) {
                Long firstId = firstQueue.poll();
                processRegistration(firstId, course);
            }

            // 1학년이 모두 끝난 후, 나머지 학년 처리
            while (!otherQueue.isEmpty()) {
                Long otherId = otherQueue.poll();
                processRegistration(otherId, course);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void processRegistration(Long studentId, Course course) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        if (course.isAvailable()) {
            course.increaseCapacity();
            courseRepository.save(course);
            registrationRepository.save(Registration.changeToComplete(student, course));
        } else {
            registrationRepository.save(Registration.changeToWait(student, course));
        }
    }
}