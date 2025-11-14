package com.techcourse.api.service.lock.original;

import com.techcourse.api.domain.code.Status;
import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import com.techcourse.api.service.lock.original.ApplyServiceOriginal;
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
class ApplyServiceOriginalTest {

    @Autowired
    private ApplyServiceOriginal applyServiceOriginal;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void init() {
        for (int i = 1; i <= 200; i++) {
            Student student = studentRepository.save(new Student("학생" + i, 4));
        }
        Course course = courseRepository.save(new Course("같이 실행돼도 문제없게 만드는 법", 100));
    }

    @DisplayName("한번만 신청하는 경우를 테스트한다.")
    @Test
    void 한번만신청() {
        applyServiceOriginal.apply(1L, 1L);

        long count = registrationRepository.count();

        Assertions.assertThat(count).isEqualTo(1);
    }

    @DisplayName("순차적으로 200명이 신청하는 경우를 테스트한다.")
    @Test
    void 순차적으로_신청하는_경우를_테스트한다() {
        List<Student> students = studentRepository.findAll();
        Course course = courseRepository.findAll().get(0);

        for (Student student : students) {
            applyServiceOriginal.apply(student.getId(), course.getId());
        }

        long completeCount = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE))
            .count();

        long waitCount = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.WAIT))
            .count();

        Assertions.assertThat(completeCount).isEqualTo(100);

        log.info("COMPLETE : {}", completeCount);
        log.info("WAIT : {}", waitCount);
    }

    @DisplayName("동시에 200명이 신청하는 경우를 테스트한다. - 락 제어 X")
    @Test
    void 동시에_신청하는_경우_락_제어_X() throws InterruptedException {
        List<Student> students = studentRepository.findAll();
        Course course = courseRepository.findAll().get(0);
        int threadCount = students.size();
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Student student : students) {
            executorService.submit(() -> {
                try {
                    applyServiceOriginal.apply(student.getId(), course.getId());
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

        Assertions.assertThat(completeCount).isEqualTo(200);
        Assertions.assertThat(waitCount).isEqualTo(0);

        log.info("COMPLETE : {}", completeCount);
        log.info("WAIT : {}", waitCount);

    }
}