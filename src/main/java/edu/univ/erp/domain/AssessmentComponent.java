package edu.univ.erp.domain;

public class AssessmentComponent {

    private int componentId;
    private int courseId;
    private String name;
    private int weight;
    private int maxMarks;

    public AssessmentComponent(int componentId, int courseId, String name, int weight, int maxMarks) {
        this.componentId = componentId;
        this.courseId = courseId;
        this.name = name;
        this.weight = weight;
        this.maxMarks = maxMarks;
    }

    public int getComponentId() { return componentId; }
    public int getCourseId() { return courseId; }
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public int getMaxMarks() { return maxMarks; }
}
