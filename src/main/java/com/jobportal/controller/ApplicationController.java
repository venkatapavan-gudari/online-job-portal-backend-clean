package com.jobportal.controller;

import com.jobportal.dto.ApiResponse;
import com.jobportal.dto.ApplicationStatusDTO;
import com.jobportal.entity.Application;
import com.jobportal.entity.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    // ---- JOB SEEKER ----

    @PostMapping("/{jobId}")
    public ResponseEntity<ApiResponse> applyForJob(@PathVariable Long jobId,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        User applicant = userService.findByEmail(userDetails.getUsername());
        Application application = applicationService.applyForJob(jobId, applicant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Application submitted successfully", application));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<ApiResponse> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Application> applications = applicationService.getMyApplications(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Applications fetched", applications));
    }

    // ---- RECRUITER ----

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse> getJobApplicants(@PathVariable Long jobId,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        User recruiter = userService.findByEmail(userDetails.getUsername());
        List<Application> applications = applicationService.getApplicationsForJob(jobId, recruiter.getId());
        return ResponseEntity.ok(ApiResponse.ok("Applicants fetched", applications));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateApplicationStatus(@PathVariable Long id,
                                                                @Valid @RequestBody ApplicationStatusDTO dto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        User recruiter = userService.findByEmail(userDetails.getUsername());
        Application application = applicationService.updateStatus(id, dto.getStatus(), recruiter.getId());
        return ResponseEntity.ok(ApiResponse.ok("Application status updated", application));
    }
}
