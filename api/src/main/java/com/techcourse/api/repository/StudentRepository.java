package com.techcourse.api.repository;

import com.techcourse.api.domain.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s from Student s where s.grade = 1 ")
    List<Student> findFirstGradeStudents();

    @Query("select s from Student s where s.grade != 1 ")
    List<Student> findOtherGradeStudents();
}
