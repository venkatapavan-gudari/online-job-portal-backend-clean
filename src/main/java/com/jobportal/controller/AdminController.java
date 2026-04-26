package com.jobportal.controller;

import com.jobportal.dto.ApiResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok("All users fetched", users));
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse> toggleUserStatus(@PathVariable Long id) {
        User user = adminService.toggleUserStatus(id);
        String status = user.isActive() ? "activated" : "deactivated";
        return ResponseEntity.ok(ApiResponse.ok("User account " + status, user));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse> getAllJobs() {
        List<Job> jobs = adminService.getAllJobs();
        return ResponseEntity.ok(ApiResponse.ok("All jobs fetched", jobs));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse> deleteJob(@PathVariable Long id) {
        adminService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.ok("Job deactivated by admin"));
    }
}
