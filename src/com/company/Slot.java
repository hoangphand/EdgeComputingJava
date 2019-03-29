package com.company;

import java.util.*;

public class Slot implements Comparable<Slot> {
    private Task task;
    private ProcessorCore processorCore;
    private double startTime;
    private double endTime;

    public Slot(Task task, ProcessorCore processorCore, double startTime, double endTime) {
        this.task = task;
        this.processorCore = processorCore;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task getTask() {
        return this.task;
    }

    public Processor getProcessor() {
        return this.processorCore.getProcessor();
    }

    public ProcessorCore getProcessorCore() {
        return this.processorCore;
    }

    public double getStartTime() {
        return this.startTime;
    }

    public double getEndTime() {
        return this.endTime;
    }

    public int compareTo(Slot other) {
        return (int) ((this.startTime - other.getStartTime()));
    }

    public static Comparator<Slot> compareByStartTime = new Comparator<Slot>() {
        @Override
        public int compare(Slot slot1, Slot slot2) {
            return Double.compare(slot1.getStartTime(), slot2.getStartTime());
        }
    };

    public static Comparator<Slot> compareByEndTime = new Comparator<Slot>() {
        @Override
        public int compare(Slot slot1, Slot slot2) {
            return Double.compare(slot1.getEndTime(), slot2.getEndTime());
        }
    };
}
