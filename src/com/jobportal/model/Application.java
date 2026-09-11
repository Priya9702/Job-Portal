package com.jobportal.model;

public class Application {
    private String id;
    private String jobId;
    private String jobTitle;
    private String company;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String candidateRole;
    private String resumeText;
    private int matchScore;
    private String matchGrade;
    private int atsScore;
    private MatchResult matchAnalysis;
    private String appliedAt;
    private String status;
    private String recruiterNotes;

    public Application() {}

    public Application(String id, String jobId, String jobTitle, String company,
                       String candidateName, String candidateEmail, String candidatePhone,
                       String candidateRole, String resumeText, int matchScore,
                       String matchGrade, int atsScore, MatchResult matchAnalysis,
                       String appliedAt, String status, String recruiterNotes) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.candidatePhone = candidatePhone;
        this.candidateRole = candidateRole;
        this.resumeText = resumeText;
        this.matchScore = matchScore;
        this.matchGrade = matchGrade;
        this.atsScore = atsScore;
        this.matchAnalysis = matchAnalysis;
        this.appliedAt = appliedAt;
        this.status = status;
        this.recruiterNotes = recruiterNotes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getCandidatePhone() { return candidatePhone; }
    public void setCandidatePhone(String candidatePhone) { this.candidatePhone = candidatePhone; }

    public String getCandidateRole() { return candidateRole; }
    public void setCandidateRole(String candidateRole) { this.candidateRole = candidateRole; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }

    public String getMatchGrade() { return matchGrade; }
    public void setMatchGrade(String matchGrade) { this.matchGrade = matchGrade; }

    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }

    public MatchResult getMatchAnalysis() { return matchAnalysis; }
    public void setMatchAnalysis(MatchResult matchAnalysis) { this.matchAnalysis = matchAnalysis; }

    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecruiterNotes() { return recruiterNotes; }
    public void setRecruiterNotes(String recruiterNotes) { this.recruiterNotes = recruiterNotes; }
}
