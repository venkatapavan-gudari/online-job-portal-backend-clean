package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeAnalysisDTO {
    private List<String> skills;
    private List<String> strengths;
    private List<String> weakAreas;
    private List<String> suggestedJobs;
}
