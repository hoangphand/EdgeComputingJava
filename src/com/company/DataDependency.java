package com.company;

public class DataDependency {
    private Task task;
    private double dataConstraint;

    public DataDependency(Task task, double dataConstraint) {
        this.task = task;
        this.dataConstraint = dataConstraint;
    }

    public Task getTask() {
        return this.task;
    }

    public double getDataConstraint() {
        return this.dataConstraint;
    }

    public void setDataConstraint(double dataConstraint) {
        this.dataConstraint = dataConstraint;
    }
}
