package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationStatusDTO {

    @NotBlank(message = "Status is required")
    private String status; // APPLIED, SHORTLISTED, REJECTED
}
