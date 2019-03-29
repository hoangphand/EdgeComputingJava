package com.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

public class Heuristics {
    public static Schedule HEFT(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        Schedule schedule = new Schedule(taskDAG, processorDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, processorDAG);
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
//        Processor mostPowerfulProcessor = processorDAG.getTheMostPowerfulProcessor();
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        schedule.addNewSlot(schedule.getFirstProcessorFreeAt(0), entryTask, 0);

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;
//            System.out.println("Current task: " + currentTask.getId());
//            schedule.showProcessorSlots();

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessor(currentProcessorCore, currentTask);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                }
            }
            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
//            System.out.println("Selected slot: [" + selectedSlot.getStartTime() + "--" + selectedSlot.getEndTime() + "], processor: " + selectedProcessor.getId());
//            System.out.println("==========================================================================");
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }

    public static Schedule DynamicHEFT(Schedule schedule, TaskDAG taskDAG) {
        Schedule tmpSchedule = new Schedule(schedule, taskDAG);

//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, tmpSchedule.getProcessorDAG());
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
        ProcessorCore earliestProcessorCoreForEntryTask = tmpSchedule.getFirstProcessorFreeAt(taskDAG.getArrivalTime());

        tmpSchedule.addNewSlot(earliestProcessorCoreForEntryTask, entryTask, taskDAG.getArrivalTime());

        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;

            for (int i = 0; i < tmpSchedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = tmpSchedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
                Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessor(currentProcessorCore, currentTask);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                }
            }

            tmpSchedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        tmpSchedule.setAFT(tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return tmpSchedule;
    }

    public static LinkedList<Task> prioritizeTasks(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        double avgProcessingRate = processorDAG.getAvgProcessingRate();
        double avgBandwidth = processorDAG.getAvgBandwidth();

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

        Task.sortByPriority(listOfTasks);

//        for (int i = 0; i < listOfTasks.size(); i++) {
//            System.out.println(listOfTasks.get(i).getId() + ": " + listOfTasks.get(i).getPriority());
//        }

        return listOfTasks;
    }
}
