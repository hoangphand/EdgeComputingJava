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
	    System.out.println(PrintUtils.ANSI_GREEN_BACKGROUND + "app 1 ACCEPTED!!!" + PrintUtils.ANSI_RESET);
		System.out.println("Makespan: " + makespan + ", deadline: " + taskDAG.getDeadline());
		System.out.println("AST: " + schedule.getTaskExecutionSlot().get(0).getStartTime() +
				", AFT: " + schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());
//		schedule.showProcessorSlots();

		int noOfAcceptedRequests = 1;

		for (int id = 2; id < noOfDAGsToTest + 1; id++) {
			taskDAG = new TaskDAG(id, "dataset/" + id + ".dag");

			Schedule tmpSchedule = Heuristics.DynamicHEFT(schedule, taskDAG);
			int noOfClouds = tmpSchedule.getNoOfTasksAllocatedToCloudNodes();
			int noOfFogs = tmpSchedule.getNoOfTasksAllocatedToFogNodes();

			makespan = tmpSchedule.getAFT() - taskDAG.getArrivalTime();

			if (makespan < taskDAG.getDeadline()) {
				noOfAcceptedRequests += 1;
				schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorExecutionSlots());
				System.out.println(PrintUtils.ANSI_GREEN_BACKGROUND + "app " + id + " ACCEPTED!!!" + PrintUtils.ANSI_RESET);
			} else {
				System.out.println(PrintUtils.ANSI_RED_BACKGROUND + "app " + id + " REJECTED!!!" + PrintUtils.ANSI_RESET);
			}

			System.out.println("Makespan: " + makespan + ", deadline: " + taskDAG.getDeadline());
			System.out.println("AST: " + tmpSchedule.getTaskExecutionSlot().get(0).getStartTime() +
					", AFT: " + tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());
			System.out.println("No of slots: " + schedule.countSlotsInNetwork());
			System.out.println("===============================================");
		}

		System.out.println("Accepted: " + noOfAcceptedRequests);

//		System.out.println("No of slots: " + schedule.countSlotsInNetwork());
//		System.out.println("AFT: " + schedule.getAFT());
//		System.out.println("Total computation cost: " + schedule.getTotalComputationCost());
//		System.out.println("Total communication cost: " + schedule.getTotalCommunicationCost());
    }
}
