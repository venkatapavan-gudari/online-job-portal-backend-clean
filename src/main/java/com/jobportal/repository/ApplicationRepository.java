package com.jobportal.repository;

import com.jobportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicantId(Long userId);

    List<Application> findByJobId(Long jobId);

    Optional<Application> findByJobIdAndApplicantId(Long jobId, Long userId);

    boolean existsByJobIdAndApplicantId(Long jobId, Long userId);
}
