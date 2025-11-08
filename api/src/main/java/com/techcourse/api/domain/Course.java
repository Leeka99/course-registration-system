package com.techcourse.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int maxCapacity;

    private int currentCapacity;

    public Course() {

    }

    public Course(String title, int maxCapacity) {
        this.title = title;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
    }

    public boolean isAvailable() {
        return currentCapacity < maxCapacity;
    }

    public void increaseCapacity() {
        this.currentCapacity++;
    }
}
