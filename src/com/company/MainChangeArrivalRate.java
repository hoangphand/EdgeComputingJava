package com.company;

public class MainChangeArrivalRate {
    public static void main(String[] args) {
        int noOfDAGs = 100;
        double arrivalRate = 0.4;
        double arrivalTime = arrivalRate;
//        String dirDataSet = "dataset-GHz";
//        String dirDataSet = "dataset-GHz";
        String dirDataSet = "cp-dataset-100";

        for (int index = 2; index < noOfDAGs + 1; index++) {
            System.out.println(index);
            TaskDAG taskDAG = new TaskDAG(1, dirDataSet + "/" + index + ".dag");
            taskDAG.setArrivalTime(arrivalTime);
            taskDAG.exportDAG(dirDataSet + "/" + index + ".dag");

            arrivalTime =  arrivalTime + arrivalRate;
        }
    }
}
