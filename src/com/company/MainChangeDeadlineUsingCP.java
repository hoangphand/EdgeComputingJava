package com.company;

import java.util.LinkedList;
import java.util.Random;

public class MainChangeDeadlineUsingCP {
    public static void main(String[] args) {
        int noOfDAGs = 100;
        String oldDirDataSet = "dataset-GHz";
        String newDirDataSet = "cp-dataset-100";

        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/new-processors.dag");

        double avgProcessingRate = processorDAG.getAvgProcessingRate();
        double avgBandwidth = processorDAG.getAvgBandwidthWithLAN();


        for (int index = 1; index < noOfDAGs + 1; index++) {
            System.out.println(index);
            TaskDAG taskDAG = new TaskDAG(1, oldDirDataSet + "/" + index + ".dag");

            LinkedList<Task> listOfTasks = new LinkedList<Task>();

            for (int i = 0; i < taskDAG.getTasks().size(); i++) {
                listOfTasks.add(taskDAG.getTasks().get(i));
            }

            for (int i = 0; i < taskDAG.getLayers().size(); i++) {
                int layerIndex = taskDAG.getLayers().size() - i - 1;

                for (int j = 0; j < taskDAG.getLayers().get(layerIndex).size(); j++) {
                    int taskIndex = taskDAG.getLayers().get(layerIndex).size() - j - 1;
                    Task currentTask = taskDAG.getLayers().get(layerIndex).get(taskIndex);

                    double avgComputationCost = currentTask.getComputationRequired() / avgProcessingRate;

                    if (currentTask.getSuccessors().size() == 0) {
                        currentTask.setPriority(avgComputationCost);
                    } else {
                        double tmpMaxSuccessorCost = Double.MIN_VALUE;

                        for (int k = 0; k < currentTask.getSuccessors().size(); k++) {
                            DataDependency currentSuccessor = currentTask.getSuccessors().get(k);
                            Task successorTask = currentSuccessor.getTask();

                            double communicationCost = currentSuccessor.getDataConstraint() / avgBandwidth;
                            double currentSuccessorCost = communicationCost + successorTask.getPriority();

                            if (currentSuccessorCost > tmpMaxSuccessorCost) {
                                tmpMaxSuccessorCost = currentSuccessorCost;
                            }
                        }

                        currentTask.setPriority(avgComputationCost + tmpMaxSuccessorCost);
                    }
                }
            }

            listOfTasks.get(0).setPriority(listOfTasks.get(0).getPriority() + 1);

            LinkedList<Task> criticalPath = new LinkedList<Task>();
            criticalPath.add(listOfTasks.get(0));
            double criticalPathLength = 0;
            Task currentTask = listOfTasks.get(0);

            while (currentTask.getId() != taskDAG.getTasks().size() - 1) {
                Task topPrioritySuccessorTask = null;
                DataDependency topSuccessorDependency = null;

                criticalPathLength += currentTask.getComputationRequired() / avgProcessingRate;

                for (int i = 0; i < currentTask.getSuccessors().size(); i++) {
                    DataDependency currentSuccessorDependency = currentTask.getSuccessors().get(i);
                    Task successorTask = currentSuccessorDependency.getTask();

                    if (topPrioritySuccessorTask == null ||
                            successorTask.getPriority() > topPrioritySuccessorTask.getPriority()) {
                        topSuccessorDependency = currentSuccessorDependency;
                        topPrioritySuccessorTask = successorTask;
                    }
                }

                criticalPathLength += topSuccessorDependency.getDataConstraint() / avgBandwidth;
                currentTask = topPrioritySuccessorTask;
                criticalPath.add(listOfTasks.get(currentTask.getId()));
            }

            Random ran = new Random();
            double randomDeadline = criticalPathLength + ran.nextDouble() * criticalPathLength;

            System.out.println("Random deadline: " + randomDeadline);
            System.out.println("Deadline: " + taskDAG.getDeadline());
            taskDAG.setDeadline(randomDeadline);

            taskDAG.exportDAG(newDirDataSet + "/" + index + ".dag");
        }
    }
}
