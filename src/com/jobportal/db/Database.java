package com.jobportal.db;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.MatchResult;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Database {

    private static final List<Job> jobs = new CopyOnWriteArrayList<>();
    private static final List<Application> applications = new CopyOnWriteArrayList<>();

    static {
        seedInitialData();
    }

    private static void seedInitialData() {
        Job job1 = new Job(
            "job-101",
            "Java Full Stack Engineer (Spring Boot & React)",
            "TechSphere Solutions",
            "https://api.dicebear.com/7.x/identicon/svg?seed=TechSphere",
            "Full Stack",
            "Bengaluru, India (Hybrid)",
            "Hybrid",
            "Mid Level (2-5 yrs)",
            "₹12,00,000 - ₹20,00,000 / yr",
            "We are seeking a Java Full Stack Developer to build modern web applications, REST APIs, and responsive enterprise portals using Java Spring Boot, MySQL, and JavaScript/React.",
            Arrays.asList(
                "Architect microservices using Java 21 and Spring Boot.",
                "Develop dynamic frontend components using HTML5, CSS3, and JavaScript/React.",
                "Write optimized SQL queries and manage MySQL database schemas.",
                "Maintain automated unit testing and CI/CD pipelines."
            ),
            Arrays.asList("Java", "Spring Boot", "MySQL", "SQL", "REST API", "JavaScript", "HTML5", "CSS3", "Git"),
            "2026-09-08",
            "Active",
            "Priya Sharma",
            "priya@techsphere.com",
            14
        );

        Job job2 = new Job(
            "job-102",
            "Java Backend Developer (Microservices & MySQL)",
            "FinTech Global",
            "https://api.dicebear.com/7.x/identicon/svg?seed=FinTechGlobal",
            "Software Engineering",
            "Mumbai, India",
            "Full-Time",
            "Entry Level (0-2 yrs)",
            "₹8,50,000 - ₹13,00,000 / yr",
            "Great opportunity for MCA / B.Tech freshers or junior Java developers to work on high-volume transaction processing and relational database systems.",
            Arrays.asList(
                "Develop high-performance REST APIs in Core Java and Spring MVC.",
                "Design MySQL relational database tables, indexes, and stored procedures.",
                "Participate in agile sprint planning and code reviews."
            ),
            Arrays.asList("Java", "Core Java", "SQL", "MySQL", "Spring Boot", "OOPs", "REST API", "Git"),
            "2026-09-10",
            "Active",
            "Amit Kumar",
            "careers@fintechglobal.com",
            22
        );

        Job job3 = new Job(
            "job-103",
            "AI & Machine Learning Engineer",
            "NeuralWorks AI",
            "https://api.dicebear.com/7.x/identicon/svg?seed=NeuralWorks",
            "Data Science & AI",
            "Remote",
            "Remote",
            "Mid Level (2-5 yrs)",
            "₹16,00,000 - ₹26,00,000 / yr",
            "Join our core AI team to train, fine-tune, and deploy NLP models for candidate resume analysis, automated matching, and intelligent search.",
            Arrays.asList(
                "Develop Java/Python NLP algorithms for resume document parsing.",
                "Build scalable AI inference services backed by SQL databases.",
                "Optimize model latency and microservice performance."
            ),
            Arrays.asList("Java", "Python", "AI", "Machine Learning", "NLP", "SQL", "MySQL", "REST API", "Docker"),
            "2026-09-09",
            "Active",
            "Neha Verma",
            "neha@neuralworks.ai",
            9
        );

        Job job4 = new Job(
            "job-104",
            "Cloud & DevOps Specialist",
            "CloudPulse Tech",
            "https://api.dicebear.com/7.x/identicon/svg?seed=CloudPulse",
            "DevOps & Cloud",
            "Hyderabad, India",
            "Full-Time",
            "Senior Level (5+ yrs)",
            "₹22,00,000 - ₹34,00,000 / yr",
            "Lead cloud infrastructure automation, Docker containerization, and Kubernetes deployments.",
            Arrays.asList(
                "Automate AWS cloud infrastructure using Terraform.",
                "Maintain CI/CD deployment pipelines for Java microservices.",
                "Ensure high availability and cloud security."
            ),
            Arrays.asList("Docker", "Kubernetes", "AWS", "Java", "CI/CD", "Linux", "MySQL"),
            "2026-09-06",
            "Active",
            "Rahul Saxena",
            "jobs@cloudpulse.io",
            11
        );

        jobs.addAll(Arrays.asList(job1, job2, job3, job4));

        MatchResult sampleResult = new MatchResult(
            90, "A+",
            Arrays.asList("Java", "Spring Boot", "MySQL", "REST API", "JavaScript", "HTML5", "CSS3", "Git"),
            Arrays.asList("React"),
            Arrays.asList("Java", "Spring Boot", "MySQL", "REST API", "JavaScript", "Git"),
            Arrays.asList("React"),
            88, "A+",
            "Excellent Java & SQL background with solid MCA project architecture.",
            Arrays.asList("Strong Java & SQL fundamentals", "Relevant MCA degree background", "Clear structured resume"),
            Arrays.asList("Add unit testing details (JUnit)", "Quantify project impact metrics"),
            Arrays.asList(
                "Architected REST APIs in Java Spring Boot with MySQL database backing.",
                "Built interactive web frontends using HTML5, CSS3, and JavaScript."
            )
        );

        Application sampleApp = new Application(
            "app-201",
            "job-101",
            "Java Full Stack Engineer (Spring Boot & React)",
            "TechSphere Solutions",
            "Aarav Sharma (MCA Candidate)",
            "aarav.mca@gmail.com",
            "+91 98765 12345",
            "Java Full Stack Developer",
            "OBJECTIVE: MCA Student seeking Java Full Stack Software Developer role.\nTECHNICAL SKILLS: Java, Core Java, Spring Boot, MySQL, SQL, HTML5, CSS3, JavaScript, REST API, Git.\nPROJECTS: Job Portal with AI Resume Matcher using Java backend and MySQL database.\nEDUCATION: Master of Computer Applications (MCA) - 8.6 CGPA.",
            90, "A+", 88,
            sampleResult,
            "2026-09-10 11:30:00",
            "Shortlisted",
            "Strong MCA candidate profile with Java & SQL expertise. Technical interview scheduled."
        );

        applications.add(sampleApp);
    }

    public static List<Job> getJobs(String search, String role, String location, String jobType, String experience) {
        return jobs.stream().filter(j -> {
            if (!"Active".equalsIgnoreCase(j.getStatus())) return false;

            if (search != null && !search.trim().isEmpty()) {
                String q = search.toLowerCase();
                boolean matchTitle = j.getTitle().toLowerCase().contains(q);
                boolean matchCompany = j.getCompany().toLowerCase().contains(q);
                boolean matchDesc = j.getDescription().toLowerCase().contains(q);
                boolean matchSkill = j.getSkills().stream().anyMatch(s -> s.toLowerCase().contains(q));
                if (!matchTitle && !matchCompany && !matchDesc && !matchSkill) return false;
            }

            if (role != null && !"All".equalsIgnoreCase(role)) {
                if (!j.getRoleCategory().equalsIgnoreCase(role)) return false;
            }

            if (location != null && !"All".equalsIgnoreCase(location)) {
                if (!j.getLocation().toLowerCase().contains(location.toLowerCase())) return false;
            }

            if (jobType != null && !"All".equalsIgnoreCase(jobType)) {
                if (!j.getJobType().equalsIgnoreCase(jobType)) return false;
            }

            if (experience != null && !"All".equalsIgnoreCase(experience)) {
                if (!j.getExperienceLevel().equalsIgnoreCase(experience)) return false;
            }

            return true;
        }).collect(Collectors.toList());
    }

    public static Job getJobById(String id) {
        return jobs.stream().filter(j -> j.getId().equals(id)).findFirst().orElse(null);
    }

    public static void addJob(Job job) {
        jobs.add(0, job);
    }

    public static boolean deleteJob(String id) {
        return jobs.removeIf(j -> j.getId().equals(id));
    }

    public static void addApplication(Application app) {
        applications.add(0, app);
        Job job = getJobById(app.getJobId());
        if (job != null) {
            job.setApplicantCount(job.getApplicantCount() + 1);
        }
    }

    public static List<Application> getApplications(String candidateEmail, String jobId) {
        return applications.stream().filter(a -> {
            if (candidateEmail != null && !candidateEmail.trim().isEmpty()) {
                if (!a.getCandidateEmail().equalsIgnoreCase(candidateEmail.trim())) return false;
            }
            if (jobId != null && !jobId.trim().isEmpty()) {
                if (!a.getJobId().equals(jobId.trim())) return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public static boolean updateApplicationStatus(String id, String status, String notes) {
        for (Application app : applications) {
            if (app.getId().equals(id)) {
                app.setStatus(status);
                if (notes != null) app.setRecruiterNotes(notes);
                return true;
            }
        }
        return false;
    }

    public static int getTotalJobs() { return jobs.size(); }
    public static int getTotalApplications() { return applications.size(); }
    public static int getShortlistedCount() {
        return (int) applications.stream().filter(a -> 
            "Shortlisted".equalsIgnoreCase(a.getStatus()) || 
            "Interview Scheduled".equalsIgnoreCase(a.getStatus()) || 
            "Offered".equalsIgnoreCase(a.getStatus())
        ).count();
    }
}
