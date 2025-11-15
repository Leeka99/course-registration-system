package com.techcourse.api.service.registration;

import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Student;

public interface RegistrationService {

    void courseRegistration(Student student, Course course, int firstYearLimit,
        int otherYearLimit);
}
