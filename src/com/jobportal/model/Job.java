package com.jobportal.model;

import java.util.List;

public class Job {
    private String id;
    private String title;
    private String company;
    private String companyLogo;
    private String roleCategory;
    private String location;
    private String jobType;
    private String experienceLevel;
    private String salaryRange;
    private String description;
    private List<String> responsibilities;
    private List<String> skills;
    private String postedDate;
    private String status;
    private String recruiterName;
    private String recruiterEmail;
    private int applicantCount;

    public Job() {}

    public Job(String id, String title, String company, String companyLogo, String roleCategory,
               String location, String jobType, String experienceLevel, String salaryRange,
               String description, List<String> responsibilities, List<String> skills,
               String postedDate, String status, String recruiterName, String recruiterEmail, int applicantCount) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.companyLogo = companyLogo;
        this.roleCategory = roleCategory;
        this.location = location;
        this.jobType = jobType;
        this.experienceLevel = experienceLevel;
        this.salaryRange = salaryRange;
        this.description = description;
        this.responsibilities = responsibilities;
        this.skills = skills;
        this.postedDate = postedDate;
        this.status = status;
        this.recruiterName = recruiterName;
        this.recruiterEmail = recruiterEmail;
        this.applicantCount = applicantCount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public String getRoleCategory() { return roleCategory; }
    public void setRoleCategory(String roleCategory) { this.roleCategory = roleCategory; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecruiterName() { return recruiterName; }
    public void setRecruiterName(String recruiterName) { this.recruiterName = recruiterName; }

    public String getRecruiterEmail() { return recruiterEmail; }
    public void setRecruiterEmail(String recruiterEmail) { this.recruiterEmail = recruiterEmail; }

    public int getApplicantCount() { return applicantCount; }
    public void setApplicantCount(int applicantCount) { this.applicantCount = applicantCount; }
}
