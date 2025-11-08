package com.techcourse.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Registration() {
    }

    public Registration(Student student, Course course, Status status) {
        this.student = student;
        this.course = course;
        this.status = status;
        this.time = LocalDateTime.now();
    }

    public static Registration changeToComplete(Student student, Course course) {
        return new Registration(student, course, Status.COMPLETE);
    }

    public static Registration changeToWait(Student student, Course course) {
        return new Registration(student, course, Status.WAIT);
    }
}
