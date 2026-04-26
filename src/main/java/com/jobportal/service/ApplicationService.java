package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobService jobService;
    private final EmailService emailService;

    @Transactional
    public Application applyForJob(Long jobId, User applicant) {
        Job job = jobService.getJobById(jobId);

        if (!job.isActive()) {
            throw new BadRequestException("This job is no longer active");
        }

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicant.getId())) {
            throw new BadRequestException("You have already applied for this job");
        }

        Application application = Application.builder()
                .job(job)
                .applicant(applicant)
                .status(ApplicationStatus.APPLIED)
                .build();

        Application saved = applicationRepository.save(application);
        log.info("User {} applied for job {}", applicant.getEmail(), jobId);

        try {
            emailService.sendApplicationConfirmationEmail(
                    applicant.getEmail(),
                    applicant.getName(),
                    job.getTitle(),
                    job.getRecruiter().getName()
            );
        } catch (Exception e) {
            log.error("Failed to enqueue application confirmation email", e);
        }

        return saved;
    }

    public List<Application> getMyApplications(Long userId) {
        return applicationRepository.findByApplicantId(userId);
    }

    public List<Application> getApplicationsForJob(Long jobId, Long recruiterId) {
        Job job = jobService.getJobById(jobId);
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new BadRequestException("You can only view applicants for your own jobs");
        }
        return applicationRepository.findByJobId(jobId);
    }

    @Transactional
    public Application updateStatus(Long applicationId, String statusStr, Long recruiterId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        if (!application.getJob().getRecruiter().getId().equals(recruiterId)) {
            throw new BadRequestException("You can only update status for your own job applications");
        }

        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Must be APPLIED, SHORTLISTED, or REJECTED");
        }

        application.setStatus(status);
        Application updated = applicationRepository.save(application);
        log.info("Application {} status updated to {}", applicationId, status);

        try {
            emailService.sendApplicationStatusUpdateEmail(
                    application.getApplicant().getEmail(),
                    application.getApplicant().getName(),
                    application.getJob().getTitle(),
                    application.getJob().getRecruiter().getName(),
                    status.name()
            );
        } catch (Exception e) {
            log.error("Failed to enqueue application status update email", e);
        }

        return updated;
    }
}
