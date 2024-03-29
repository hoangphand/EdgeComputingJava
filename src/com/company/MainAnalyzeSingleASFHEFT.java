package com.company;

import java.util.Hashtable;
import java.util.Map;

public class MainAnalyzeSingleASFHEFT {
    public static void main(String[] args) {
        int noOfTaskDAGs = 100;
        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/processors.dag");

        Map<Double, Integer> countCCR = new Hashtable<Double, Integer>();
        countCCR.put(0.2, 0);
        countCCR.put(0.5, 0);
        countCCR.put(1.0, 0);
        countCCR.put(2.0, 0);
        countCCR.put(5.0, 0);

        Map<Double, Integer> countCCRBetterCU = new Hashtable<Double, Integer>();
        countCCRBetterCU.put(0.2, 0);
        countCCRBetterCU.put(0.5, 0);
        countCCRBetterCU.put(1.0, 0);
        countCCRBetterCU.put(2.0, 0);
        countCCRBetterCU.put(5.0, 0);

        int noOfCUBetterThanHEFT = 0;

        for (int i = 1; i < noOfTaskDAGs + 1; i++) {
            TaskDAG taskDAG = new TaskDAG(i, "dataset-GHz/" + i + ".dag");

//            Schedule schedule = Heuristics.AStepFurtherHEFT(taskDAG, processorDAG);
//            Schedule schedule = Heuristics.AStepFurtherHEFTAdjacentAvg(taskDAG, processorDAG);
            Schedule schedule = Heuristics.AStepFurtherHEFTAdjacentMax(taskDAG, processorDAG);

            if (schedule.getAFT() < taskDAG.getMakespanHEFT()) {
                noOfCUBetterThanHEFT += 1;
                System.out.println("Task " + i + " better than " +
                        Math.round(taskDAG.getMakespanHEFT() - schedule.getAFT()) / taskDAG.getMakespanHEFT() * 100 +
                        "%, CCR: " + taskDAG.getCCR());

                countCCRBetterCU.put(taskDAG.getCCR(), countCCRBetterCU.get(taskDAG.getCCR()) + 1);
            }
            countCCR.put(taskDAG.getCCR(), countCCR.get(taskDAG.getCCR()) + 1);
        }

        System.out.println("No of ASF schedules better than HEFT: " + noOfCUBetterThanHEFT);
        for (Map.Entry<Double, Integer> entry : countCCRBetterCU.entrySet()) {
            System.out.println("CCR " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("No of CCR DAGs in dataset: ");
        for (Map.Entry<Double, Integer> entry : countCCR.entrySet()) {
            System.out.println("CCR " + entry.getKey() + ": " + entry.getValue());
        }
    }
}