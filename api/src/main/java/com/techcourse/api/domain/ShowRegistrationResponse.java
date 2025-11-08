package com.techcourse.api.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowRegistrationResponse {

    private String courseName;
    private Status status;
    private String studentName;
    private int grade;
    private LocalDateTime time;
}
