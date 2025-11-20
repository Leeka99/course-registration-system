package com.techcourse.api;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class ApiApplicationTests {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("1학년 학생 100명 3학년 학생 100명 생성, 정원 100명 수강과목 1개 생성")
    void 기본_수강신청_1개일경우_테스트케이스_생성() {
        for (int i = 1; i <= 200; i += 2) {
            studentRepository.save(new Student("학생" + i, 1));
            studentRepository.save(new Student("학생" + (i + 1), 3));
        }
        courseRepository.save(new Course("대학생활 시작하기", 50));
    }

    @Test
    @DisplayName("모든 학년 학생 100명식 생성, 정원 100명 수강과목 1개 생성")
    void 기본_수강신청_1개_모든학년_학생_테스트케이스_생성() {
        for (int i = 1; i <= 400; i += 4) {
            studentRepository.save(new Student("학생" + i, 1));
            studentRepository.save(new Student("학생" + (i + 1), 2));
            studentRepository.save(new Student("학생" + (i + 2), 3));
            studentRepository.save(new Student("학생" + (i + 3), 4));
        }
        courseRepository.save(new Course("대학생활 시작하기", 50));
    }

    @Test
    @DisplayName("1학년 학생 300명 3학년 학생 300명 생성, 정원 100명 수강과목 3개 생성")
    void 수강신청_3개일경우_테스트케이스_생성() {
        for (int i = 1; i <= 600; i += 2) {
            studentRepository.save(new Student("학생" + i, 1));
            studentRepository.save(new Student("학생" + (i + 1), 3));
        }
        for (int i = 1; i <= 3; i++) {
            courseRepository.save(new Course("대학생활 시작하기" + i, 50));
        }
    }

    @Test
    @DisplayName("모든 학년 학생 300명식 생성, 정원 100명 수강과목 3개 생성")
    void 수강신청_3개_모든학년_학생_테스트케이스_생성() {
        for (int i = 1; i <= 1200; i += 4) {
            studentRepository.save(new Student("학생" + i, 1));
            studentRepository.save(new Student("학생" + (i + 1), 2));
            studentRepository.save(new Student("학생" + (i + 2), 3));
            studentRepository.save(new Student("학생" + (i + 3), 4));
        }
        for (int i = 1; i <= 3; i++) {
            courseRepository.save(new Course("대학생활 시작하기" + i, 50));
        }
    }

    @Test
    @DisplayName("1000명식 총 4000명 생성, 정원 30명 수강과목 100개 생성")
    void 과목_100개_모든학년_학생_테스트케이스_생성() {
        for (int i = 1; i <= 4000; i += 4) {
            studentRepository.save(new Student("학생" + i, 1));
            studentRepository.save(new Student("학생" + (i + 1), 2));
            studentRepository.save(new Student("학생" + (i + 2), 3));
            studentRepository.save(new Student("학생" + (i + 3), 4));
        }
        for (int i = 1; i <= 100; i++) {
            courseRepository.save(new Course("대학생활 시작하기" + i, 30));
        }
    }

    @Test
    @DisplayName("15000명식 총 60000명 생성, 정원 30명 수강과목 1500개 생성")
    void 과목_1500개_모든학년_학생_테스트케이스_생성() {
        for (int i = 1; i <= 60000; i += 4) {
            studentRepository.save(new Student("학생" + i, 1));
            studentRepository.save(new Student("학생" + (i + 1), 2));
            studentRepository.save(new Student("학생" + (i + 2), 3));
            studentRepository.save(new Student("학생" + (i + 3), 4));
        }
        for (int i = 1; i <= 1500; i++) {
            courseRepository.save(new Course("대학생활 시작하기" + i, 30));
        }
    }
}