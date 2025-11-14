package com.techcourse.api.service.showregistration;

import com.techcourse.api.domain.dto.ShowRegistrationResponse;
import com.techcourse.api.domain.entity.Course;
import com.techcourse.api.domain.entity.Registration;
import com.techcourse.api.domain.entity.Student;
import com.techcourse.api.repository.CourseRepository;
import com.techcourse.api.repository.RegistrationRepository;
import com.techcourse.api.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowRegistrationService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<ShowRegistrationResponse> showRegistration() {
        List<Registration> registrations = registrationRepository.findAll();

        List<ShowRegistrationResponse> responses = new ArrayList<>();

        for (Registration registration : registrations) {
            Student student = studentRepository.findById(registration.getStudent().getId())
                .orElseThrow();
            Course course = courseRepository.findById(registration.getCourse().getId())
                .orElseThrow();

            ShowRegistrationResponse dto = new ShowRegistrationResponse(
                registration.getId(),
                course.getTitle(),
                registration.getStatus(),
                student.getName(),
                student.getGrade(),
                registration.getTime()
            );
            responses.add(dto);
        }

        return responses;
    }
}
