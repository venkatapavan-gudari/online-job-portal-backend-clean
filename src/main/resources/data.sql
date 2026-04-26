-- =====================================================
-- Online Job Portal - Database Setup Script
-- Run this manually to seed an Admin user
-- =====================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS job_portal_db;
USE job_portal_db;

-- =====================================================
-- Insert default Admin user
-- Password: admin123 (BCrypt encoded)
-- =====================================================
INSERT INTO users (name, email, password, role, active, created_at)
VALUES (
    'System Admin',
    'admin@jobportal.com',
    'admin123',
    'ADMIN',
    true,
    NOW()
) ON DUPLICATE KEY UPDATE id = id;

-- =====================================================
-- Sample Recruiter (Password: recruiter123)
-- =====================================================
INSERT INTO users (name, email, password, role, active, created_at)
VALUES (
    'Test Recruiter',
    'recruiter@jobportal.com',
    'recruiter123',
    'RECRUITER',
    true,
    NOW()
) ON DUPLICATE KEY UPDATE id = id;

-- =====================================================
-- Sample Job Seeker (Password: seeker123)
-- =====================================================
INSERT INTO users (name, email, password, role, active, created_at)
VALUES (
    'Test Seeker',
    'seeker@jobportal.com',
    'seeker123',
    'JOB_SEEKER',
    true,
    NOW()
) ON DUPLICATE KEY UPDATE id = id;
