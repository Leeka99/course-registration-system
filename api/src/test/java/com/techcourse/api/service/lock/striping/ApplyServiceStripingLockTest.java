package com.techcourse.api.service.lock.striping;

import com.techcourse.api.domain.code.Status;
import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import java.util.List;
import java.util.Random;
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
class ApplyServiceStripingLockTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ApplyServiceStripingLock applyServiceStripingLock;

    @BeforeEach
    void init() {
        for (int i = 1; i <= 200; i += 2) {
            studentRepository.save(new Student("학생" + i, 3));
            studentRepository.save(new Student("학생" + i + 1, 1));
        }
        for (int i = 0; i < 3; i++) {
            courseRepository.save(new Course("대학생활 시작하기" + i, 100));
        }
    }

    @DisplayName("1학년 200명, 3학년 200명이 동시에 신청하는 경우 1학년 90% 이외학년 10% 비율로 정상적으로 저장되는지 테스트한다. - ReentrantLock")
    @Test
    void 동시에_신청하는_경우_ReentrantLock_적용() throws InterruptedException {
        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();

        int threadCount = students.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

//        Random random = new Random();

        for (int index = 0; index < threadCount; index++) {
            final int idx = index;
//            Long randomNumber = Long.valueOf(random.nextInt(3) + 1);
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 여기서 대기
                    startLatch.await();
                    applyServiceStripingLock.apply(students.get(idx).getId(),
                        courses.get(0).getId());

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

        Assertions.assertThat(firstGradeNumber).isEqualTo(90);
        Assertions.assertThat(thirdGradeNumber).isEqualTo(10);

        log.info("firstGradeNumber : {}", firstGradeNumber);
        log.info("thirdGradeNumber : {}", thirdGradeNumber);
    }

    @DisplayName("모든 학년이 신청하는 경우에도 1학년 90% 이외학년 10% 비율로 정상적으로 저장되는지 테스트한다. - ReentrantLock")
    @Test
    void 모든학년이_동시에_신청하는_경우_ReentrantLock_적용() throws InterruptedException {

        for (int i = 201; i <= 400; i += 2) {
            studentRepository.save(new Student("학생" + i, 2));
            studentRepository.save(new Student("학생" + i + 1, 4));
        }

        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();

        int threadCount = students.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        Random random = new Random();

        for (int index = 0; index < threadCount; index++) {
            final int idx = index;
            Long randomNumber = Long.valueOf(random.nextInt(3) + 1);
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 여기서 대기
                    startLatch.await();

                    applyServiceStripingLock.apply(students.get(idx).getId(),
                        courses.get(0).getId());

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
            .filter(register -> register.getStatus().equals(Status.COMPLETE)
                && register.getStudent().getGrade() == 1).count();

        long secondGradeNumber = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE)
                && register.getStudent().getGrade() == 2).count();

        long thirdGradeNumber = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE)
                && register.getStudent().getGrade() == 3).count();

        long fourthGradeNumber = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE)
                && register.getStudent().getGrade() == 4).count();

        Assertions.assertThat(firstGradeNumber).isEqualTo(90);
        Assertions.assertThat(secondGradeNumber).isLessThan(11);
        Assertions.assertThat(thirdGradeNumber).isLessThan(11);
        Assertions.assertThat(fourthGradeNumber).isLessThan(11);

        log.info("firstGradeNumber : {}", firstGradeNumber);
        log.info("secondGradeNumber : {}", secondGradeNumber);
        log.info("thirdGradeNumber : {}", thirdGradeNumber);
        log.info("fourthGradeNumber : {}", fourthGradeNumber);
    }
}