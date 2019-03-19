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
        Processor mostPowerfulProcessor = processorDAG.getTheMostPowerfulProcessor();
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        schedule.addNewSlot(mostPowerfulProcessor, entryTask, 0);

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            Processor selectedProcessor = null;
//            System.out.println("Current task: " + currentTask.getId());
//            schedule.showProcessorSlots();

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < processorDAG.getProcessors().size(); i++) {
                Processor currentProcessor = processorDAG.getProcessors().get(i);
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessor(currentProcessor, currentTask);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessor = currentProcessor;
                }
            }
            schedule.addNewSlot(selectedProcessor, currentTask, selectedSlot.getStartTime());
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
        Processor earliestProcessorForEntryTask = tmpSchedule.getFirstProcessorFreeAt(taskDAG.getArrivalTime());

        tmpSchedule.addNewSlot(earliestProcessorForEntryTask, entryTask, taskDAG.getArrivalTime());

        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

            Slot selectedSlot = null;
            Processor selectedProcessor = null;

            for (int i = 0; i < tmpSchedule.getProcessorDAG().getProcessors().size(); i++) {
                Processor currentProcessor = tmpSchedule.getProcessorDAG().getProcessors().get(i);
                Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessor(currentProcessor, currentTask);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessor = currentProcessor;
                }
            }

            tmpSchedule.addNewSlot(selectedProcessor, currentTask, selectedSlot.getStartTime());
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

        return listOfTasks;
    }
}
