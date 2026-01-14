package edu.univ.erp.domain;

public class SectionItem {
    public final int sectionId;
    public final int courseId;
    public final String label;

    public SectionItem(int sectionId, int courseId, String label) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
