package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobDTO {

    @NotBlank(message = "Job title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private String description;

    private String location;

    @Size(max = 500, message = "Skills must be at most 500 characters")
    private String skills;
}
