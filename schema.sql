-- ============================================================
-- SQL Database Schema for MCA Resume Project: AI Job Portal
-- Target Database: MySQL / MariaDB / PostgreSQL / SQLite compatible
-- ============================================================

CREATE DATABASE IF NOT EXISTS job_portal_db;
USE job_portal_db;

-- 1. Jobs Table
CREATE TABLE IF NOT EXISTS jobs (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    company_logo VARCHAR(500),
    role_category VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    experience_level VARCHAR(100) NOT NULL,
    salary_range VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    responsibilities TEXT NOT NULL,
    skills VARCHAR(500) NOT NULL,
    posted_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Active',
    recruiter_name VARCHAR(100) NOT NULL,
    recruiter_email VARCHAR(150) NOT NULL,
    applicant_count INT DEFAULT 0
);

-- 2. Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id VARCHAR(50) PRIMARY KEY,
    job_id VARCHAR(50) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    candidate_name VARCHAR(150) NOT NULL,
    candidate_email VARCHAR(150) NOT NULL,
    candidate_phone VARCHAR(50),
    candidate_role VARCHAR(100),
    resume_text TEXT NOT NULL,
    match_score INT NOT NULL,
    match_grade VARCHAR(10) NOT NULL,
    ats_score INT NOT NULL,
    matched_skills TEXT,
    missing_skills TEXT,
    summary TEXT,
    improvements TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Under Review',
    recruiter_notes TEXT,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- 3. Initial Sample Data Seed for Demonstration
INSERT INTO jobs (id, title, company, company_logo, role_category, location, job_type, experience_level, salary_range, description, responsibilities, skills, posted_date, status, recruiter_name, recruiter_email, applicant_count)
VALUES 
('job-101', 'Java Full Stack Engineer (Spring Boot & React)', 'TechSphere Solutions', 'https://api.dicebear.com/7.x/identicon/svg?seed=TechSphere', 'Full Stack', 'Bengaluru, India (Hybrid)', 'Hybrid', 'Mid Level (2-5 yrs)', '₹12,00,000 - ₹20,00,000 / yr', 'We are looking for a Java Full Stack Software Engineer to build robust REST APIs using Java Spring Boot and responsive React dashboards.', 'Design and implement microservices in Java 21 and Spring Boot.|Develop front-end components using JavaScript and HTML5/CSS3.|Optimize MySQL database queries, indexing, and transactions.|Integrate CI/CD pipelines and automated JUnit testing.', 'Java, Spring Boot, MySQL, REST API, JavaScript, React, HTML5, CSS3, Git', '2026-09-08', 'Active', 'Priya Sharma', 'priya@techsphere.com', 14),

('job-102', 'Java Backend Developer (Microservices & MySQL)', 'FinTech Global', 'https://api.dicebear.com/7.x/identicon/svg?seed=FinTechGlobal', 'Software Engineering', 'Mumbai, India', 'Full-Time', 'Entry Level (0-2 yrs)', '₹8,50,000 - ₹13,00,000 / yr', 'Ideal role for MCA / B.Tech graduates with strong Java OOPs, Data Structures, and SQL relational database fundamentals.', 'Develop REST APIs and business logic modules in Core Java and Spring MVC.|Write optimized SQL queries and stored procedures in MySQL.|Participate in code reviews and agile software development Sprints.', 'Java, Core Java, SQL, MySQL, Spring Boot, OOPs, REST API, Git', '2026-09-10', 'Active', 'Amit Kumar', 'careers@fintechglobal.com', 22),

('job-103', 'AI & Machine Learning Engineer', 'NeuralWorks AI', 'https://api.dicebear.com/7.x/identicon/svg?seed=NeuralWorks', 'Data Science & AI', 'Remote', 'Remote', 'Mid Level (2-5 yrs)', '₹16,00,000 - ₹26,00,000 / yr', 'Join our AI team building automated document intelligence, NLP resume matching engines, and predictive analytics models.', 'Train and deploy NLP model pipelines for candidate resume scoring.|Build high-speed Java/Python inference APIs.|Manage MySQL vector index stores and document metadata.', 'Java, Python, AI, Machine Learning, NLP, SQL, MySQL, REST API, Docker', '2026-09-09', 'Active', 'Neha Verma', 'neha@neuralworks.ai', 9),

('job-104', 'Cloud & DevOps Automation Specialist', 'CloudPulse Tech', 'https://api.dicebear.com/7.x/identicon/svg?seed=CloudPulse', 'DevOps & Cloud', 'Hyderabad, India', 'Full-Time', 'Senior Level (5+ yrs)', '₹22,00,000 - ₹34,00,000 / yr', 'Lead cloud automation, Docker containerization, Kubernetes cluster management, and CI/CD pipelines.', 'Automate AWS/GCP cloud resources using Terraform.|Maintain CI/CD automation pipelines for Java applications.|Monitor database performance and cloud uptime metrics.', 'Docker, Kubernetes, AWS, Java, CI/CD, Linux, Shell Scripting, MySQL', '2026-09-06', 'Active', 'Rahul Saxena', 'jobs@cloudpulse.io', 11);

INSERT INTO applications (id, job_id, job_title, company, candidate_name, candidate_email, candidate_phone, candidate_role, resume_text, match_score, match_grade, ats_score, matched_skills, missing_skills, summary, improvements, status)
VALUES
('app-201', 'job-101', 'Java Full Stack Engineer (Spring Boot & React)', 'TechSphere Solutions', 'Aarav Sharma (MCA Candidate)', 'aarav.mca@gmail.com', '+91 98765 12345', 'Java Full Stack Developer', 'OBJECTIVE: Master of Computer Applications (MCA) student seeking Java Full Stack Developer position.\nSKILLS: Java, Core Java, Spring Boot, MySQL, SQL, HTML5, CSS3, JavaScript, REST API, Git.\nPROJECTS: MCA Job Portal with AI Resume Matcher using Java backend and MySQL database.\nEDUCATION: MCA - 8.6 CGPA.', 90, 'A+', 88, 'Java, Spring Boot, MySQL, REST API, JavaScript, HTML5, CSS3, Git', 'React', 'Excellent Java & SQL background with solid project architecture suitable for full stack development.', 'Add unit testing framework (JUnit) to resume project details.|Include quantitative achievements in project descriptions.', 'Shortlisted');
