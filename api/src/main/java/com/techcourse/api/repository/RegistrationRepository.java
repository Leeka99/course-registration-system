package com.techcourse.api.repository;

import com.techcourse.api.domain.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

}
