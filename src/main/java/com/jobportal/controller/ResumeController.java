package com.jobportal.controller;

import com.jobportal.dto.ApiResponse;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.service.ResumeService;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadResume(@RequestParam("file") MultipartFile file,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Resume resume = resumeService.uploadAndAnalyzeResume(file, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Resume uploaded and analyzed successfully", resume));
    }

    @GetMapping("/my-resume")
    public ResponseEntity<ApiResponse> getMyResume(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        try {
            Resume resume = resumeService.getMyResume(user.getId());
            return ResponseEntity.ok(ApiResponse.ok("Resume fetched successfully", resume));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("No resume found", null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserResume(@PathVariable Long userId) {
        try {
            Resume resume = resumeService.getMyResume(userId);
            return ResponseEntity.ok(ApiResponse.ok("Resume fetched successfully", resume));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("No resume found", null));
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(resume.getFileType()));
        headers.setContentDispositionFormData("attachment", resume.getFileName());
        
        return new ResponseEntity<>(resume.getData(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/admin/stats")
    public ResponseEntity<ApiResponse> getAdminStats() {
        long total = resumeService.getTotalResumes();
        return ResponseEntity.ok(ApiResponse.ok("Stats fetched", Map.of("totalResumesAnalyzed", total)));
    }
}
