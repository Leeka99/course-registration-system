package com.techcourse.api.service.lock.javalock;

import com.techcourse.api.domain.code.Status;
import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.lock.javalock.ApplyServiceSynchronized;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@Slf4j
class ApplyServiceSynchronizedTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ApplyServiceSynchronized applyServiceSynchronized;

    @BeforeEach
    void init() {
        for (int i = 1; i <= 200; i++) {
            Student student = studentRepository.save(new Student("학생" + i, 4));
        }
        Course course = courseRepository.save(new Course("같이 실행돼도 문제없게 만드는 법", 100));
    }

    @DisplayName("동시에 200명이 신청하는 경우를 테스트한다. - synchronized 키워드 사용")
    @Test
    void 동시에_신청하는_경우_synchronized_키워드사용() throws InterruptedException {
        List<Student> students = studentRepository.findAll();
        Course course = courseRepository.findAll().get(0);
        int threadCount = students.size();
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Student student : students) {
            executorService.submit(() -> {
                try {
                    applyServiceSynchronized.apply(student.getId(), course.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // 스레드 풀 종료
        executorService.shutdown();

        long completeCount = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE))
            .count();

        long waitCount = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.WAIT))
            .count();

        Assertions.assertThat(completeCount).isEqualTo(100);
        Assertions.assertThat(waitCount).isEqualTo(100);

        log.info("COMPLETE : {}", completeCount);
        log.info("WAIT : {}", waitCount);
    }
}