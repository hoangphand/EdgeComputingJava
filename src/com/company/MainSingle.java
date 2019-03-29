package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class MainSingle {

    public static void main(String[] args) {
        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/processors.dag");

//        for (int i = 0; i < processorDAG.getProcessors().size(); i++) {
//            Processor currentProcessor = processorDAG.getProcessors().get(i);
//            System.out.println("Processor " + currentProcessor.getId() + " no of cores: " + currentProcessor.getNoOfCores());
//        }

        TaskDAG taskDAG = new TaskDAG(1, "dataset-GHz/1.dag");
        Schedule schedule = Heuristics.HEFT(taskDAG, processorDAG);
        double makespan = schedule.getAFT();

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        System.out.println(schedule.getProcessorCoreExecutionSlots().size());
    }
}
