package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
    	int noOfDAGsToTest = 100;

	    ProcessorDAG processorDAG = new ProcessorDAG("dataset/processors.dag");

	    TaskDAG taskDAG = new TaskDAG(1, "dataset/1.dag");
	    Schedule schedule = Heuristics.HEFT(taskDAG, processorDAG);
	    double makespan = schedule.getAFT();

	    ScheduleResult scheduleResult = new ScheduleResult(schedule);
	    scheduleResult.print();

		int noOfAcceptedRequests = 1;

		for (int id = 2; id < noOfDAGsToTest + 1; id++) {
			taskDAG = new TaskDAG(id, "dataset/" + id + ".dag");

			Schedule tmpSchedule = Heuristics.DynamicHEFT(schedule, taskDAG);
			ScheduleResult tmpScheduleResult = new ScheduleResult(tmpSchedule);

			if (tmpScheduleResult.isAccepted()) {
				schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorExecutionSlots());
				noOfAcceptedRequests += 1;
			}

			tmpScheduleResult.print();
		}

		System.out.println("Accepted " + noOfAcceptedRequests);
    }
}
