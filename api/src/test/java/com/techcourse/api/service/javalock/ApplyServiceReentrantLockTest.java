package com.techcourse.api.service.javalock;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@Slf4j
class ApplyServiceReentrantLockTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ApplyServiceReentrantLock applyServiceReentrantLock;

    @BeforeEach
    void init() {
        for (int i = 1; i <= 70; i++) {
            studentRepository.save(new Student("학생" + i, 3));
        }

        for (int i = 71; i <= 150; i++) {
            studentRepository.save(new Student("학생" + i, 1));
        }
        courseRepository.save(new Course("대학생활 시작하기", 100));
    }

    @Test
    void testFirstYearPriority() throws InterruptedException {
        List<Student> students = studentRepository.findAll();
        Course course = courseRepository.findAll().get(0);
        int threadCount = students.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Student student : students) {
            executorService.submit(() -> {
                applyServiceReentrantLock.apply(student.getId(), course.getId());
                latch.countDown();
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // 스레드 풀 종료
        executorService.shutdown();

        long firstGradeNumber = registrationRepository.findAll().stream()
            .filter(
                register -> register.getStatus().equals(
                    Status.COMPLETE) && register.getStudent().getGrade() == 1).count();

        long thirdGradeNumber = registrationRepository.findAll().stream()
            .filter(register -> register.getStatus().equals(Status.COMPLETE)
                && register.getStudent().getGrade() == 3).count();

        Assertions.assertThat(firstGradeNumber).isEqualTo(80);
        Assertions.assertThat(thirdGradeNumber).isEqualTo(70);

        log.info("firstGradeNumber : {}", firstGradeNumber);
        log.info("thirdGradeNumber : {}", thirdGradeNumber);
    }
}