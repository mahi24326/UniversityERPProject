package edu.univ.erp.domain;

public class StudentAssessmentRow {

    private final String component;
    private final Double score;     // nullable
    private final int weight;
    private final int maxMarks;

    public StudentAssessmentRow(String component, Double score, int weight, int maxMarks) {
        this.component = component;
        this.score = score;
        this.weight = weight;
        this.maxMarks = maxMarks;
    }

    public String getComponent() { return component; }
    public Double getScore()     { return score; }
    public int getWeight()       { return weight; }
    public int getMaxMarks()     { return maxMarks; }

    public int getContribution() {
        if (score == null) return 0;
        return (int) Math.round((score / maxMarks) * weight);
    }
}
