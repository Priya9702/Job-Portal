package com.jobportal.ai;

import com.jobportal.model.Job;
import com.jobportal.model.MatchResult;

import java.util.*;
import java.util.regex.Pattern;

public class AiMatcher {

    private static final List<String> COMMON_KEYWORDS = Arrays.asList(
        "Java", "Spring Boot", "Spring MVC", "Core Java", "Hibernate", "JPA",
        "React", "Node.js", "Express", "TypeScript", "JavaScript", "HTML5", "CSS3",
        "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "REST API", "Microservices",
        "AWS", "Docker", "Kubernetes", "CI/CD", "Git", "GitHub", "Python", "FastAPI",
        "AI", "Machine Learning", "NLP", "TensorFlow", "PyTorch", "OOPs", "Data Structures"
    );

    public static MatchResult analyze(Job job, String resumeText) {
        String resumeLower = resumeText.toLowerCase();
        String jobText = (job.getTitle() + " " + job.getDescription() + " " + String.join(" ", job.getSkills())).toLowerCase();

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : job.getSkills()) {
            if (resumeLower.contains(skill.toLowerCase())) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        List<String> matchingKeywords = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();

        for (String kw : COMMON_KEYWORDS) {
            if (jobText.contains(kw.toLowerCase())) {
                if (resumeLower.contains(kw.toLowerCase())) {
                    matchingKeywords.add(kw);
                } else {
                    missingKeywords.add(kw);
                }
            }
        }

        // Match Percentage calculation
        double skillRatio = job.getSkills().isEmpty() ? 0.6 : ((double) matchedSkills.size() / job.getSkills().size());
        double kwRatio = matchingKeywords.isEmpty() && missingKeywords.isEmpty() ? 0.7 : 
            ((double) matchingKeywords.size() / Math.max(1, (matchingKeywords.size() + missingKeywords.size())));

        int baseMatch = (int) Math.round((skillRatio * 65.0) + (kwRatio * 35.0));
        
        // Boost for project experience or MCA context
        if (resumeLower.contains("project") || resumeLower.contains("developed") || resumeLower.contains("built")) {
            baseMatch += 5;
        }
        if (resumeLower.contains("mca") || resumeLower.contains("btech") || resumeLower.contains("degree")) {
            baseMatch += 5;
        }

        int matchScore = Math.min(98, Math.max(25, baseMatch));
        String matchGrade = getGrade(matchScore);

        // ATS Score calculation
        int atsScore = 72;
        if (resumeLower.contains("education") || resumeLower.contains("academic")) atsScore += 8;
        if (resumeLower.contains("experience") || resumeLower.contains("projects")) atsScore += 10;
        if (resumeLower.contains("skills") || resumeLower.contains("technical skills")) atsScore += 8;
        if (Pattern.compile("\\d+%").matcher(resumeText).find() || Pattern.compile("\\d+").matcher(resumeText).find()) atsScore += 2;

        atsScore = Math.min(96, Math.max(40, atsScore));
        String atsGrade = getAtsGrade(atsScore);

        // Strengths & Improvements
        List<String> strengths = new ArrayList<>();
        if (!matchedSkills.isEmpty()) {
            strengths.add("Matches essential target skills: " + String.join(", ", matchedSkills.subList(0, Math.min(3, matchedSkills.size()))));
        } else {
            strengths.add("Demonstrates fundamental technical computer application knowledge.");
        }
        if (resumeText.length() > 300) {
            strengths.add("Well-detailed resume text structure with clear technical background.");
        }
        strengths.add("Uses relevant software industry terminology and project references.");

        List<String> improvements = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            improvements.add("Incorporate missing core skills: " + String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))));
        }
        if (!missingKeywords.isEmpty()) {
            improvements.add("Add high-volume job keywords: " + String.join(", ", missingKeywords.subList(0, Math.min(3, missingKeywords.size()))));
        }
        improvements.add("Include quantifiable metrics (e.g. 'Improved database query response by 35%').");
        improvements.add("Ensure key MCA/B.Tech projects are highlighted with technology tags.");

        List<String> suggestedBulletPoints = Arrays.asList(
            "Architected and deployed backend REST APIs using " + (job.getSkills().isEmpty() ? "Java" : job.getSkills().get(0)) + " with optimized MySQL query performance.",
            "Collaborated on full-stack web modules, increasing user platform engagement by 25%."
        );

        String summary;
        if (matchScore >= 80) {
            summary = "Excellent candidate match for " + job.getTitle() + " with strong alignment across required skills.";
        } else if (matchScore >= 60) {
            summary = "Solid candidate match for " + job.getTitle() + ". Adding a few target missing keywords will boost shortlist probability.";
        } else {
            summary = "Moderate profile match for " + job.getTitle() + ". Recommended to tailor project highlights towards job requirements.";
        }

        return new MatchResult(
            matchScore, matchGrade, matchedSkills, missingSkills,
            matchingKeywords, missingKeywords, atsScore, atsGrade,
            summary, strengths, improvements, suggestedBulletPoints
        );
    }

    private static String getGrade(int score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 45) return "C";
        return "D";
    }

    private static String getAtsGrade(int score) {
        if (score >= 88) return "A+";
        if (score >= 75) return "A";
        if (score >= 60) return "B";
        if (score >= 45) return "C";
        return "Needs Improvement";
    }
}
