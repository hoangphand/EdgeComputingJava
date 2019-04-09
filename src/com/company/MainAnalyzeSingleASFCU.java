package com.company;

import java.util.Hashtable;
import java.util.Map;

public class MainAnalyzeSingleASFCU {
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

        int noOfCUBetterThanCU = 0;

        for (int i = 1; i < noOfTaskDAGs + 1; i++) {
            TaskDAG taskDAG = new TaskDAG(i, "dataset-GHz/" + i + ".dag");

            Schedule scheduleCU = Heuristics.CloudUnaware(taskDAG, processorDAG);
            Schedule scheduleASF = Heuristics.AStepFurtherHEFTAdjacent(taskDAG, processorDAG);
//            Schedule scheduleASF = Heuristics.AStepFurtherHEFT(taskDAG, processorDAG);

            if (scheduleCU.getAFT() > scheduleASF.getAFT()) {
                noOfCUBetterThanCU += 1;
                System.out.println("Task " + i + " better than " +
                        Math.round(scheduleCU.getAFT() - scheduleASF.getAFT()) / scheduleCU.getAFT() * 100 +
                        "%, CCR: " + taskDAG.getCCR());

                countCCRBetterCU.put(taskDAG.getCCR(), countCCRBetterCU.get(taskDAG.getCCR()) + 1);
            }
            countCCR.put(taskDAG.getCCR(), countCCR.get(taskDAG.getCCR()) + 1);
        }

        System.out.println("No of ASF schedules better than CU: " + noOfCUBetterThanCU);
        for (Map.Entry<Double, Integer> entry : countCCRBetterCU.entrySet()) {
            System.out.println("CCR " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("No of CCR DAGs in dataset: ");
        for (Map.Entry<Double, Integer> entry : countCCR.entrySet()) {
            System.out.println("CCR " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
