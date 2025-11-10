package com.techcourse.api.repository;

import com.techcourse.api.domain.entity.Course;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Version;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Course l where l.id = :id")
    Optional<Course> findByIdWithPessimisticLock(@Param("id") Long id);

    @Version
    @Query("select l from Course l where l.id = :id")
    Optional<Course> findByIdWithOptimisticLock(@Param("id") Long id);

}
