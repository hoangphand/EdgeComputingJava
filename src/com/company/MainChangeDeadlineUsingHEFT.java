package com.company;

public class MainChangeDeadlineUsingHEFT {
    public static void main(String[] args) {
        int noOfDAGs = 100;
        String oldDirDataSet = "dataset-GHz";
        String newDirDataSet = "new-dataset-GHz";

        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/new-processors.dag");

        for (int index = 1; index < noOfDAGs + 1; index++) {
            System.out.println(index);
            TaskDAG taskDAG = new TaskDAG(1, oldDirDataSet + "/" + index + ".dag");

            Schedule schedule = Heuristics.HEFT(taskDAG, processorDAG);
            double makespan = schedule.getAFT();

            taskDAG.setMakespanHEFT(makespan);
            taskDAG.setDeadline(makespan * 1.5);

            taskDAG.exportDAG(newDirDataSet + "/" + index + ".dag");
        }
    }
}
