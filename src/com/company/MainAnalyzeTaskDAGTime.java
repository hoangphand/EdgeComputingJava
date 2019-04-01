package com.company;

import java.util.Hashtable;
import java.util.Map;

public class MainAnalyzeTaskDAGTime {
    public static void main(String[] args) {
        int noOfTaskDAGs = 100;
        Map<Double, Double> ccrBasedAvg = new Hashtable<Double, Double>();
        ccrBasedAvg.put(0.2, 0.0);
        ccrBasedAvg.put(0.5, 0.0);
        ccrBasedAvg.put(1.0, 0.0);
        ccrBasedAvg.put(2.0, 0.0);
        ccrBasedAvg.put(5.0, 0.0);

        Map<Double, Integer> countCCR = new Hashtable<Double, Integer>();
        countCCR.put(0.2, 0);
        countCCR.put(0.5, 0);
        countCCR.put(1.0, 0);
        countCCR.put(2.0, 0);
        countCCR.put(5.0, 0);

        for (int i = 1; i < noOfTaskDAGs + 1; i++) {
            TaskDAG taskDAG = new TaskDAG(1, "dataset-GHz/" + i + ".dag");

            ccrBasedAvg.put(taskDAG.getCCR(), ccrBasedAvg.get(taskDAG.getCCR()) + taskDAG.getDeadline());
            countCCR.put(taskDAG.getCCR(), countCCR.get(taskDAG.getCCR()) + 1);
        }

        for (Map.Entry<Double, Double> entry : ccrBasedAvg.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue() +
                    ", avg deadline: " + (entry.getValue() / countCCR.get(entry.getKey())));
        }

        System.out.println("=============================================");

        ccrBasedAvg.put(0.2, 0.0);
        ccrBasedAvg.put(0.5, 0.0);
        ccrBasedAvg.put(1.0, 0.0);
        ccrBasedAvg.put(2.0, 0.0);
        ccrBasedAvg.put(5.0, 0.0);
        countCCR.put(0.2, 0);
        countCCR.put(0.5, 0);
        countCCR.put(1.0, 0);
        countCCR.put(2.0, 0);
        countCCR.put(5.0, 0);

        for (int i = 1; i < noOfTaskDAGs + 1; i++) {
            TaskDAG taskDAG = new TaskDAG(1, "dataset/" + i + ".dag");

            if (!ccrBasedAvg.containsKey(taskDAG.getCCR())) {
                ccrBasedAvg.put(taskDAG.getCCR(), 0.0);
            }

            if (!countCCR.containsKey(taskDAG.getCCR())) {
                countCCR.put(taskDAG.getCCR(), 0);
            }

            ccrBasedAvg.put(taskDAG.getCCR(), ccrBasedAvg.get(taskDAG.getCCR()) + taskDAG.getDeadline());
            countCCR.put(taskDAG.getCCR(), countCCR.get(taskDAG.getCCR()) + 1);
        }

        for (Map.Entry<Double, Double> entry : ccrBasedAvg.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue() +
                    ", avg deadline: " + (entry.getValue() / countCCR.get(entry.getKey())));
        }
    }
}
