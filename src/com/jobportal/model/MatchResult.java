package com.jobportal.model;

import java.util.List;

public class MatchResult {
    private int matchPercentage;
    private String matchGrade;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> matchingKeywords;
    private List<String> missingKeywords;
    private int atsScore;
    private String atsGrade;
    private String summary;
    private List<String> strengths;
    private List<String> improvements;
    private List<String> suggestedBulletPoints;

    public MatchResult() {}

    public MatchResult(int matchPercentage, String matchGrade, List<String> matchedSkills,
                       List<String> missingSkills, List<String> matchingKeywords,
                       List<String> missingKeywords, int atsScore, String atsGrade,
                       String summary, List<String> strengths, List<String> improvements,
                       List<String> suggestedBulletPoints) {
        this.matchPercentage = matchPercentage;
        this.matchGrade = matchGrade;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.matchingKeywords = matchingKeywords;
        this.missingKeywords = missingKeywords;
        this.atsScore = atsScore;
        this.atsGrade = atsGrade;
        this.summary = summary;
        this.strengths = strengths;
        this.improvements = improvements;
        this.suggestedBulletPoints = suggestedBulletPoints;
    }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public String getMatchGrade() { return matchGrade; }
    public void setMatchGrade(String matchGrade) { this.matchGrade = matchGrade; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public List<String> getMatchingKeywords() { return matchingKeywords; }
    public void setMatchingKeywords(List<String> matchingKeywords) { this.matchingKeywords = matchingKeywords; }

    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }

    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }

    public String getAtsGrade() { return atsGrade; }
    public void setAtsGrade(String atsGrade) { this.atsGrade = atsGrade; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }

    public List<String> getSuggestedBulletPoints() { return suggestedBulletPoints; }
    public void setSuggestedBulletPoints(List<String> suggestedBulletPoints) { this.suggestedBulletPoints = suggestedBulletPoints; }
}
