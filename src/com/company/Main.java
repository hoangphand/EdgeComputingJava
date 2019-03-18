package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
	    ProcessorDAG processorDAG = new ProcessorDAG("dataset/processors.dag");

	    TaskDAG taskDAG = new TaskDAG(1, "dataset/1.dag");
	    Schedule schedule = Heuristics.HEFT(taskDAG, processorDAG);

		System.out.println("No of slots: " + schedule.countSlotsInNetwork());
		System.out.println("AFT: " + schedule.getAFT());
		System.out.println("Total computation cost: " + schedule.getTotalComputationCost());
		System.out.println("Total communication cost: " + schedule.getTotalCommunicationCost());
    }
}
