package com.techcourse.api.domain.dto;

import com.techcourse.api.domain.code.Status;
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
