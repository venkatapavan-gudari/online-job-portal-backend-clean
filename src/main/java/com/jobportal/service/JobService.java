package com.jobportal.service;

import com.jobportal.dto.JobDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public Job createJob(JobDTO dto, User recruiter) {
        Job job = Job.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .skills(dto.getSkills())
                .recruiter(recruiter)
                .active(true)
                .build();
        Job saved = jobRepository.save(job);
        log.info("Job created: {} by recruiter {}", saved.getTitle(), recruiter.getEmail());

        try {
            List<User> jobSeekers = userRepository.findByRole(Role.JOB_SEEKER);
            for (User seeker : jobSeekers) {
                emailService.sendNewJobPostedEmail(
                        seeker.getEmail(),
                        seeker.getName(),
                        saved.getTitle(),
                        recruiter.getName()
                );
            }
        } catch (Exception e) {
            log.error("Failed to enqueue new job posting emails", e);
        }

        return saved;
    }

    @Transactional
    public Job updateJob(Long jobId, JobDTO dto, Long recruiterId) {
        Job job = getJobById(jobId);
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new BadRequestException("You can only update your own job postings");
        }
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSkills(dto.getSkills());
        return jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long jobId, Long userId, boolean isAdmin) {
        Job job = getJobById(jobId);
        if (!isAdmin && !job.getRecruiter().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own job postings");
        }
        job.setActive(false);
        jobRepository.save(job);
        log.info("Job deactivated: {}", jobId);
    }

    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
    }

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrue();
    }

    public List<Job> searchJobs(String title, String location, String skills) {
        return jobRepository.searchJobs(
                (title != null && title.isBlank()) ? null : title,
                (location != null && location.isBlank()) ? null : location,
                (skills != null && skills.isBlank()) ? null : skills
        );
    }

    public List<Job> getRecruiterJobs(Long recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
}
