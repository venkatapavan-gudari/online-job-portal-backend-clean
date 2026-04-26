package com.jobportal.controller;

import com.jobportal.dto.ApiResponse;
import com.jobportal.dto.JobDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.service.JobService;
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
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    // ---- PUBLIC ----

    @GetMapping
    public ResponseEntity<ApiResponse> getAllJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills) {

        List<Job> jobs;
        if (title != null || location != null || skills != null) {
            jobs = jobService.searchJobs(title, location, skills);
        } else {
            jobs = jobService.getAllActiveJobs();
        }
        return ResponseEntity.ok(ApiResponse.ok("Jobs fetched", jobs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.ok("Job fetched", job));
    }

    // ---- RECRUITER ----

    @PostMapping
    public ResponseEntity<ApiResponse> createJob(@Valid @RequestBody JobDTO dto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User recruiter = userService.findByEmail(userDetails.getUsername());
        Job job = jobService.createJob(dto, recruiter);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Job created successfully", job));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateJob(@PathVariable Long id,
                                                  @Valid @RequestBody JobDTO dto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User recruiter = userService.findByEmail(userDetails.getUsername());
        Job job = jobService.updateJob(id, dto, recruiter.getId());
        return ResponseEntity.ok(ApiResponse.ok("Job updated successfully", job));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteJob(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = user.getRole().name().equals("ADMIN");
        jobService.deleteJob(id, user.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Job deactivated successfully"));
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<ApiResponse> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
        User recruiter = userService.findByEmail(userDetails.getUsername());
        List<Job> jobs = jobService.getRecruiterJobs(recruiter.getId());
        return ResponseEntity.ok(ApiResponse.ok("Recruiter jobs fetched", jobs));
    }
}
