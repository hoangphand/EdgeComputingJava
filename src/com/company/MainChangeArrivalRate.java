package com.company;

public class MainChangeArrivalRate {
    public static void main(String[] args) {
        int noOfDAGs = 100;
        double arrivalRate = 1.8;
        double arrivalTime = arrivalRate;
        String dirDataSet = "dataset-GHz";

        for (int index = 2; index < noOfDAGs + 1; index++) {
            System.out.println(index);
            TaskDAG taskDAG = new TaskDAG(1, dirDataSet + "/" + index + ".dag");
            taskDAG.setArrivalTime(arrivalTime);
            taskDAG.exportDAG(dirDataSet + "/" + index + ".dag");

            arrivalTime =  arrivalTime + arrivalRate;
        }
    }
}
