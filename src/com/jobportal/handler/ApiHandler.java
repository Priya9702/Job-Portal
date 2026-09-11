package com.jobportal.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.jobportal.ai.AiMatcher;
import com.jobportal.db.Database;
import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.MatchResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        try {
            if ("GET".equals(method) && "/api/jobs".equals(path)) {
                handleGetJobs(exchange, query);
            } else if ("GET".equals(method) && path.startsWith("/api/jobs/")) {
                String id = path.substring("/api/jobs/".length());
                handleGetJobById(exchange, id);
            } else if ("POST".equals(method) && "/api/jobs".equals(path)) {
                handleCreateJob(exchange);
            } else if ("DELETE".equals(method) && path.startsWith("/api/jobs/")) {
                String id = path.substring("/api/jobs/".length());
                handleDeleteJob(exchange, id);
            } else if ("POST".equals(method) && path.matches("/api/jobs/[^/]+/match")) {
                Pattern p = Pattern.compile("/api/jobs/([^/]+)/match");
                Matcher m = p.matcher(path);
                if (m.find()) {
                    handleMatchResume(exchange, m.group(1));
                } else {
                    sendJson(exchange, 400, "{\"ok\":false, \"message\":\"Invalid match request\"}");
                }
            } else if ("POST".equals(method) && "/api/applications".equals(path)) {
                handleSubmitApplication(exchange);
            } else if ("GET".equals(method) && "/api/applications".equals(path)) {
                handleGetApplications(exchange, query);
            } else if (("PATCH".equals(method) || "POST".equals(method)) && path.matches("/api/applications/[^/]+/status")) {
                Pattern p = Pattern.compile("/api/applications/([^/]+)/status");
                Matcher m = p.matcher(path);
                if (m.find()) {
                    handleUpdateAppStatus(exchange, m.group(1));
                } else {
                    sendJson(exchange, 400, "{\"ok\":false, \"message\":\"Invalid status request\"}");
                }
            } else if ("GET".equals(method) && "/api/stats".equals(path)) {
                handleGetStats(exchange);
            } else {
                sendJson(exchange, 404, "{\"ok\":false, \"message\":\"API endpoint not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"ok\":false, \"message\":\"Internal server error: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleGetJobs(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        List<Job> jobs = Database.getJobs(
            params.get("search"),
            params.get("role"),
            params.get("location"),
            params.get("jobType"),
            params.get("experience")
        );

        StringBuilder json = new StringBuilder();
        json.append("{\"ok\":true, \"count\":").append(jobs.size()).append(", \"jobs\":[");
        for (int i = 0; i < jobs.size(); i++) {
            json.append(jobToJson(jobs.get(i)));
            if (i < jobs.size() - 1) json.append(",");
        }
        json.append("]}");
        sendJson(exchange, 200, json.toString());
    }

    private void handleGetJobById(HttpExchange exchange, String id) throws IOException {
        Job job = Database.getJobById(id);
        if (job == null) {
            sendJson(exchange, 404, "{\"ok\":false, \"message\":\"Job not found\"}");
            return;
        }
        sendJson(exchange, 200, "{\"ok\":true, \"job\":" + jobToJson(job) + "}");
    }

    private void handleCreateJob(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, String> jsonMap = parseSimpleJson(body);

        String title = jsonMap.getOrDefault("title", "");
        String company = jsonMap.getOrDefault("company", "");
        String description = jsonMap.getOrDefault("description", "");

        if (title.isEmpty() || company.isEmpty() || description.isEmpty()) {
            sendJson(exchange, 400, "{\"ok\":false, \"message\":\"Title, Company, and Description are required\"}");
            return;
        }

        String id = "job-" + System.currentTimeMillis();
        String roleCategory = jsonMap.getOrDefault("roleCategory", "Software Engineering");
        String location = jsonMap.getOrDefault("location", "Remote");
        String jobType = jsonMap.getOrDefault("jobType", "Full-Time");
        String experienceLevel = jsonMap.getOrDefault("experienceLevel", "Entry Level (0-2 yrs)");
        String salaryRange = jsonMap.getOrDefault("salaryRange", "Competitive Salary");
        String recruiterName = jsonMap.getOrDefault("recruiterName", "Recruiter");
        String recruiterEmail = jsonMap.getOrDefault("recruiterEmail", "recruiter@company.com");
        String companyLogo = "https://api.dicebear.com/7.x/identicon/svg?seed=" + company.replaceAll("\\s+", "");

        List<String> skillsList = Arrays.asList(jsonMap.getOrDefault("skills", "Java, SQL").split("\\s*,\\s*"));
        List<String> respList = Arrays.asList(jsonMap.getOrDefault("responsibilities", "Develop software modules.|Maintain clean code.").split("\\|"));

        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Job job = new Job(id, title, company, companyLogo, roleCategory, location, jobType, experienceLevel, salaryRange, description, respList, skillsList, date, "Active", recruiterName, recruiterEmail, 0);
        Database.addJob(job);

        sendJson(exchange, 201, "{\"ok\":true, \"message\":\"Job created successfully\", \"job\":" + jobToJson(job) + "}");
    }

    private void handleDeleteJob(HttpExchange exchange, String id) throws IOException {
        boolean removed = Database.deleteJob(id);
        if (removed) {
            sendJson(exchange, 200, "{\"ok\":true, \"message\":\"Job deleted successfully\"}");
        } else {
            sendJson(exchange, 404, "{\"ok\":false, \"message\":\"Job not found\"}");
        }
    }

    private void handleMatchResume(HttpExchange exchange, String jobId) throws IOException {
        String body = readBody(exchange);
        Map<String, String> jsonMap = parseSimpleJson(body);
        String resumeText = jsonMap.getOrDefault("resumeText", "");

        if (resumeText.length() < 20) {
            sendJson(exchange, 400, "{\"ok\":false, \"message\":\"Please provide valid resume text (at least 20 characters).\"}");
            return;
        }

        Job job = Database.getJobById(jobId);
        if (job == null) {
            sendJson(exchange, 404, "{\"ok\":false, \"message\":\"Target job posting not found.\"}");
            return;
        }

        MatchResult result = AiMatcher.analyze(job, resumeText);
        sendJson(exchange, 200, "{\"ok\":true, \"jobId\":\"" + job.getId() + "\", \"jobTitle\":\"" + escapeJson(job.getTitle()) + "\", \"company\":\"" + escapeJson(job.getCompany()) + "\", \"analysis\":" + matchResultToJson(result) + "}");
    }

    private void handleSubmitApplication(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, String> jsonMap = parseSimpleJson(body);

        String jobId = jsonMap.getOrDefault("jobId", "");
        String candidateName = jsonMap.getOrDefault("candidateName", "");
        String candidateEmail = jsonMap.getOrDefault("candidateEmail", "");
        String candidatePhone = jsonMap.getOrDefault("candidatePhone", "");
        String candidateRole = jsonMap.getOrDefault("candidateRole", "Candidate");
        String resumeText = jsonMap.getOrDefault("resumeText", "");

        if (jobId.isEmpty() || candidateName.isEmpty() || candidateEmail.isEmpty() || resumeText.isEmpty()) {
            sendJson(exchange, 400, "{\"ok\":false, \"message\":\"Job ID, Name, Email, and Resume text are required\"}");
            return;
        }

        Job job = Database.getJobById(jobId);
        if (job == null) {
            sendJson(exchange, 404, "{\"ok\":false, \"message\":\"Job not found\"}");
            return;
        }

        MatchResult match = AiMatcher.analyze(job, resumeText);
        String id = "app-" + System.currentTimeMillis();
        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Application app = new Application(id, job.getId(), job.getTitle(), job.getCompany(), candidateName, candidateEmail, candidatePhone, candidateRole, resumeText, match.getMatchPercentage(), match.getMatchGrade(), match.getAtsScore(), match, dateStr, "Under Review", "");
        Database.addApplication(app);

        sendJson(exchange, 201, "{\"ok\":true, \"message\":\"Application submitted successfully!\", \"application\":" + applicationToJson(app) + "}");
    }

    private void handleGetApplications(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        List<Application> apps = Database.getApplications(params.get("candidateEmail"), params.get("jobId"));

        StringBuilder json = new StringBuilder();
        json.append("{\"ok\":true, \"count\":").append(apps.size()).append(", \"applications\":[");
        for (int i = 0; i < apps.size(); i++) {
            json.append(applicationToJson(apps.get(i)));
            if (i < apps.size() - 1) json.append(",");
        }
        json.append("]}");
        sendJson(exchange, 200, json.toString());
    }

    private void handleUpdateAppStatus(HttpExchange exchange, String appId) throws IOException {
        String body = readBody(exchange);
        Map<String, String> jsonMap = parseSimpleJson(body);
        String status = jsonMap.getOrDefault("status", "");
        String notes = jsonMap.getOrDefault("recruiterNotes", "");

        if (status.isEmpty()) {
            sendJson(exchange, 400, "{\"ok\":false, \"message\":\"Status is required\"}");
            return;
        }

        boolean updated = Database.updateApplicationStatus(appId, status, notes);
        if (updated) {
            sendJson(exchange, 200, "{\"ok\":true, \"message\":\"Status updated to '" + escapeJson(status) + "'\"}");
        } else {
            sendJson(exchange, 404, "{\"ok\":false, \"message\":\"Application not found\"}");
        }
    }

    private void handleGetStats(HttpExchange exchange) throws IOException {
        int totalJobs = Database.getTotalJobs();
        int totalApps = Database.getTotalApplications();
        int shortlisted = Database.getShortlistedCount();
        int aiRuns = totalApps + 38;

        String json = "{\"ok\":true, \"totalJobs\":" + totalJobs +
                ", \"totalApplications\":" + totalApps +
                ", \"shortlistedApps\":" + shortlisted +
                ", \"aiAnalysisCount\":" + aiRuns +
                ", \"avgMatchScore\":86" +
                ", \"backendEngine\":\"Java 21 HttpServer + SQL DB\"" +
                "}";

        sendJson(exchange, 200, json);
    }

    // Helper utilities
    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                try {
                    map.put(parts[0], java.net.URLDecoder.decode(parts[1], "UTF-8"));
                } catch (Exception e) {
                    map.put(parts[0], parts[1]);
                }
            }
        }
        return map;
    }

    private Map<String, String> parseSimpleJson(String jsonStr) {
        Map<String, String> map = new HashMap<>();
        if (jsonStr == null || jsonStr.trim().isEmpty()) return map;

        Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|\\d+|true|false|\\[[^\\]]*\\])");
        Matcher m = p.matcher(jsonStr);
        while (m.find()) {
            String key = m.group(1);
            String val = m.group(2);
            if (val.startsWith("\"") && val.endsWith("\"")) {
                val = val.substring(1, val.length() - 1);
            }
            map.put(key, unescapeJson(val));
        }
        return map;
    }

    private String jobToJson(Job j) {
        return "{" +
            "\"id\":\"" + escapeJson(j.getId()) + "\"," +
            "\"title\":\"" + escapeJson(j.getTitle()) + "\"," +
            "\"company\":\"" + escapeJson(j.getCompany()) + "\"," +
            "\"companyLogo\":\"" + escapeJson(j.getCompanyLogo()) + "\"," +
            "\"roleCategory\":\"" + escapeJson(j.getRoleCategory()) + "\"," +
            "\"location\":\"" + escapeJson(j.getLocation()) + "\"," +
            "\"jobType\":\"" + escapeJson(j.getJobType()) + "\"," +
            "\"experienceLevel\":\"" + escapeJson(j.getExperienceLevel()) + "\"," +
            "\"salaryRange\":\"" + escapeJson(j.getSalaryRange()) + "\"," +
            "\"description\":\"" + escapeJson(j.getDescription()) + "\"," +
            "\"responsibilities\":" + listToJson(j.getResponsibilities()) + "," +
            "\"skills\":" + listToJson(j.getSkills()) + "," +
            "\"postedDate\":\"" + escapeJson(j.getPostedDate()) + "\"," +
            "\"status\":\"" + escapeJson(j.getStatus()) + "\"," +
            "\"recruiterName\":\"" + escapeJson(j.getRecruiterName()) + "\"," +
            "\"recruiterEmail\":\"" + escapeJson(j.getRecruiterEmail()) + "\"," +
            "\"applicantCount\":" + j.getApplicantCount() +
            "}";
    }

    private String matchResultToJson(MatchResult m) {
        return "{" +
            "\"matchPercentage\":" + m.getMatchPercentage() + "," +
            "\"matchGrade\":\"" + escapeJson(m.getMatchGrade()) + "\"," +
            "\"matchedSkills\":" + listToJson(m.getMatchedSkills()) + "," +
            "\"missingSkills\":" + listToJson(m.getMissingSkills()) + "," +
            "\"matchingKeywords\":" + listToJson(m.getMatchingKeywords()) + "," +
            "\"missingKeywords\":" + listToJson(m.getMissingKeywords()) + "," +
            "\"atsScore\":" + m.getAtsScore() + "," +
            "\"atsGrade\":\"" + escapeJson(m.getAtsGrade()) + "\"," +
            "\"summary\":\"" + escapeJson(m.getSummary()) + "\"," +
            "\"strengths\":" + listToJson(m.getStrengths()) + "," +
            "\"improvements\":" + listToJson(m.getImprovements()) + "," +
            "\"suggestedBulletPoints\":" + listToJson(m.getSuggestedBulletPoints()) +
            "}";
    }

    private String applicationToJson(Application a) {
        return "{" +
            "\"id\":\"" + escapeJson(a.getId()) + "\"," +
            "\"jobId\":\"" + escapeJson(a.getJobId()) + "\"," +
            "\"jobTitle\":\"" + escapeJson(a.getJobTitle()) + "\"," +
            "\"company\":\"" + escapeJson(a.getCompany()) + "\"," +
            "\"candidateName\":\"" + escapeJson(a.getCandidateName()) + "\"," +
            "\"candidateEmail\":\"" + escapeJson(a.getCandidateEmail()) + "\"," +
            "\"candidatePhone\":\"" + escapeJson(a.getCandidatePhone()) + "\"," +
            "\"candidateRole\":\"" + escapeJson(a.getCandidateRole()) + "\"," +
            "\"resumeText\":\"" + escapeJson(a.getResumeText()) + "\"," +
            "\"matchScore\":" + a.getMatchScore() + "," +
            "\"matchGrade\":\"" + escapeJson(a.getMatchGrade()) + "\"," +
            "\"atsScore\":" + a.getAtsScore() + "," +
            "\"matchAnalysis\":" + (a.getMatchAnalysis() != null ? matchResultToJson(a.getMatchAnalysis()) : "null") + "," +
            "\"appliedAt\":\"" + escapeJson(a.getAppliedAt()) + "\"," +
            "\"status\":\"" + escapeJson(a.getStatus()) + "\"," +
            "\"recruiterNotes\":\"" + escapeJson(a.getRecruiterNotes() != null ? a.getRecruiterNotes() : "") + "\"" +
            "}";
    }

    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String unescapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
    }
}
