package com.techcourse.api.service.dblock;

import com.techcourse.api.domain.code.Status;
import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
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
class ApplyServiceReentrantLockAndDBLockTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ApplyServiceReentrantLockAndDBLock applyServiceReentrantLockAndDBLock;

    @BeforeEach
    void init() {
        for (int i = 1; i <= 159; i += 2) {
            studentRepository.save(new Student("학생" + i, 3));
            studentRepository.save(new Student("학생" + i + 1, 1));
        }
        courseRepository.save(new Course("대학생활 시작하기", 100));
    }

    @DisplayName("1학년 80명, 3학년 80명이 동시에 신청하는 경우 1학년이 먼저 수강신청 되는지 테스트한다 - ReentrantLock And DBLock")
    @Test
    void 동시에_신청하는_경우_ReentrantLock_And_DBLock_적용() throws InterruptedException {
        List<Student> firstGradeStudents = studentRepository.findFirstGradeStudents();
        List<Student> otherGradeStudents = studentRepository.findOtherGradeStudents();

        Course course = courseRepository.findAll().get(0);

        int threadCount = firstGradeStudents.size() + otherGradeStudents.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int index = 0; index < threadCount; index++) {
            final int idx = index;
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 여기서 대기
                    startLatch.await();
                    
                    applyServiceReentrantLockAndDBLock.apply(firstGradeStudents.get(idx).getId(),
                        course.getId());
                    applyServiceReentrantLockAndDBLock.apply(otherGradeStudents.get(idx).getId(),
                        course.getId());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비 후 동시에 시작
        startLatch.countDown();

        // 모든 스레드가 끝날 때까지 대기
        doneLatch.await();

        // 스레드 풀 종료
        executorService.shutdown();

        long firstGradeNumber = registrationRepository.findAll().stream()
            .filter(
                register -> register.getStatus().equals(
                    Status.COMPLETE) && register.getStudent().getGrade() == 1).count();

        long thirdGradeNumber = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE)
                && register.getStudent().getGrade() == 3).count();

        Assertions.assertThat(firstGradeNumber).isGreaterThan(70);
        Assertions.assertThat(thirdGradeNumber).isLessThan(30);

        log.info("firstGradeNumber : {}", firstGradeNumber);
        log.info("thirdGradeNumber : {}", thirdGradeNumber);
    }

}