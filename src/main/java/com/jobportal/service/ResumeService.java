package com.jobportal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.ResumeAnalysisDTO;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AiAnalyzerService aiAnalyzerService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Resume uploadAndAnalyzeResume(MultipartFile file, User user) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are supported");
        }

        try {
            // Extract text from PDF
            String extractedText = extractTextFromPdf(file);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new RuntimeException("Could not extract any text from the provided PDF.");
            }

            // Analyze with Groq
            String analysisJson = aiAnalyzerService.analyzeResume(extractedText);

            // Verify it parses correctly into our DTO
            try {
                objectMapper.readValue(analysisJson, ResumeAnalysisDTO.class);
            } catch (Exception e) {
                // If groq returned markdown blocks like ```json ... ```, try to clean it
                analysisJson = analysisJson.replaceAll("```json", "").replaceAll("```", "").trim();
                objectMapper.readValue(analysisJson, ResumeAnalysisDTO.class); // attempt again to ensure it's valid
            }

            // Save to DB
            Optional<Resume> existingResumeOpt = resumeRepository.findByUserId(user.getId());
            Resume resume = existingResumeOpt.orElseGet(Resume::new);

            resume.setUser(user);
            resume.setFileName(file.getOriginalFilename());
            resume.setFileType(file.getContentType());
            resume.setData(file.getBytes());
            resume.setAnalysisResult(analysisJson);

            return resumeRepository.save(resume);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process resume: " + e.getMessage());
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    @Transactional(readOnly = true)
    public Resume getMyResume(Long userId) {
        return resumeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No resume found for user"));
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));
    }
    
    public long getTotalResumes() {
        return resumeRepository.count();
    }
}
