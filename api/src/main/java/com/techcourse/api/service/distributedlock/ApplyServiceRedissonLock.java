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
public class ApplyServiceRedissonLock implements ApplyService {

    private final RedissonClient redissonClient;
    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public void apply(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId).orElseThrow();

        // Redis에 임시 대기열 등록
        RQueue<Long> firstQueue = redissonClient.getQueue("firstGrade:" + courseId);
        RQueue<Long> otherQueue = redissonClient.getQueue("otherGrade:" + courseId);

        if (student.getGrade() == 1) {
            firstQueue.add(studentId);
        } else {
            otherQueue.add(studentId);
        }

        processQueue(firstQueue, courseId);

        processQueue(otherQueue, courseId);
    }

    private void processQueue(RQueue<Long> queue, Long courseId) {
        Long studentId;
        while ((studentId = queue.poll()) != null) {
            RLock lock = redissonClient.getLock("applyLock:" + courseId);
            boolean isLocked = false;
            try {
                // 최대 5초 대기, 락 잡으면 10초 유지
                isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
                if (!isLocked) {
                    // 락 못 잡으면 큐 맨 뒤로 다시 넣기
                    queue.add(studentId);
                    continue;
                }
                processRegistration(studentId, courseId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (isLocked && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Transactional
    public void processRegistration(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        if (course.isAvailable()) {
            course.increaseCapacity();
            courseRepository.save(course);
            registrationRepository.save(Registration.changeToComplete(student, course));
        } else {
            registrationRepository.save(Registration.changeToWait(student, course));
        }
    }
}