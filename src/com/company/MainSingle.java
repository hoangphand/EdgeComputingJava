package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class MainSingle {

    public static void main(String[] args) {
        ProcessorDAG processorDAG = new ProcessorDAG("dataset/processors.dag");

        TaskDAG taskDAG = new TaskDAG(1, "dataset/12.dag");
        Schedule schedule = Heuristics.HEFT(taskDAG, processorDAG);
        double makespan = schedule.getAFT();

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();
    }
}
