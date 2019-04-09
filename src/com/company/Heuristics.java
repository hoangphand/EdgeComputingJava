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
        schedule.addNewSlot(schedule.getFirstProcessorCoreFreeAt(0), entryTask, 0);

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());
            System.out.println("Current task: " + currentTask.getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);
                System.out.println("Current processor " + currentProcessorCore.getProcessor().getId() +
                        " current core " + currentProcessorCore.getCoreId() + " ends at " + currentSelectedSlot.getEndTime());

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                }
            }

            System.out.println("Select processor " + selectedProcessorCore.getProcessor().getId() +
                    " core " + selectedProcessorCore.getCoreId() + " ends at " + selectedSlot.getEndTime());
            System.out.println("============================================");
            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
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
        ProcessorCore selectedProcessorCoreForEntryTask = schedule.getFirstProcessorCoreFreeAt(taskDAG.getArrivalTime());

        tmpSchedule.addNewSlot(selectedProcessorCoreForEntryTask, entryTask, taskDAG.getArrivalTime());

        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;

            for (int i = 0; i < tmpSchedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = tmpSchedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
                Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                }
            }

            tmpSchedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        tmpSchedule.setAFT(tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());
        tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());

        return tmpSchedule;
    }

    public static Schedule CloudUnaware(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        Schedule schedule = new Schedule(taskDAG, processorDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, processorDAG);
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
//        Processor mostPowerfulProcessor = processorDAG.getTheMostPowerfulProcessor();
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        schedule.addNewSlot(schedule.getFirstProcessorCoreFreeAt(0), entryTask, 0);

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();

                if (currentProcessorCore.getProcessor().isFog()) {
//                find the first-fit slot on the current processor for the current task
                    Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                    if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                        selectedSlot = currentSelectedSlot;
                        selectedProcessorCore = currentProcessorCore;
                    }
                }
            }
            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }

    public static Schedule DynamicCloudUnaware(Schedule schedule, TaskDAG taskDAG) {
        Schedule tmpSchedule = new Schedule(schedule, taskDAG);

//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, tmpSchedule.getProcessorDAG());
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
        ProcessorCore selectedProcessorCoreForEntryTask = schedule.getFirstProcessorCoreFreeAt(taskDAG.getArrivalTime());

        tmpSchedule.addNewSlot(selectedProcessorCoreForEntryTask, entryTask, taskDAG.getArrivalTime());

        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;

            for (int i = 0; i < tmpSchedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = tmpSchedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();

                if (currentProcessorCore.getProcessor().isFog()) {
                    Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                    if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                        selectedSlot = currentSelectedSlot;
                        selectedProcessorCore = currentProcessorCore;
                    }
                }
            }

            tmpSchedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        tmpSchedule.setAFT(tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());
        tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());

        return tmpSchedule;
    }
    public static Schedule AStepFurtherHEFT(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        Schedule schedule = new Schedule(taskDAG, processorDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, processorDAG);
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
//        Processor mostPowerfulProcessor = processorDAG.getTheMostPowerfulProcessor();
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        schedule.addNewSlot(schedule.getFirstProcessorCoreFreeAt(0), entryTask, 0);

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());
//            System.out.println("Current task: " + currentTask.getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            double minAStepFurtherCost = Double.MAX_VALUE;
            ProcessorCore selectedProcessorCore = null;

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);
                double totalDataConstraint = 0;

                for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                    totalDataConstraint += currentTask.getSuccessors().get(j).getDataConstraint();
                }

                double avgDataConstraint = totalDataConstraint / currentTask.getSuccessors().size();
                double maxAvgCommunicationTime = Double.MIN_VALUE;

                if (currentProcessorCore.getProcessor().isFog()) {
                    if (currentTask.getSuccessors().size() <= processorDAG.getNoOfFogs()) {
                        maxAvgCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_LAN;
                    } else {
                        double avgCommunicationTimeLAN = avgDataConstraint / Processor.BANDWIDTH_LAN;
                        double avgCommunicationTimeWAN = avgDataConstraint / currentProcessorCore.getProcessor().getWanUploadBandwidth();

                        maxAvgCommunicationTime = Math.max(avgCommunicationTimeLAN, avgCommunicationTimeWAN);
                    }
                } else {
                    if (currentTask.getSuccessors().size() <= currentProcessorCore.getProcessor().getNoOfCores()) {
                        maxAvgCommunicationTime = 0;
                    } else {
                        maxAvgCommunicationTime = avgDataConstraint / currentProcessorCore.getProcessor().getWanUploadBandwidth();
                    }
                }
//                System.out.println("Current processor " + currentProcessorCore.getProcessor().getId() +
//                        " current core " + currentProcessorCore.getCoreId() +
//                        " ends at " + currentSelectedSlot.getEndTime() +
//                        ", communication time: " + maxAvgCommunicationTime);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() + maxAvgCommunicationTime < minAStepFurtherCost) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                    minAStepFurtherCost = currentSelectedSlot.getEndTime() + maxAvgCommunicationTime;
                }
            }

//            System.out.println("Select processor " + selectedProcessorCore.getProcessor().getId() +
//                    " core " + selectedProcessorCore.getCoreId() +
//                    ", asf HEFT time: " + minAStepFurtherCost);
//            System.out.println("============================================");
            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }
    public static Schedule AStepFurtherHEFTAdjacent(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        Schedule schedule = new Schedule(taskDAG, processorDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, processorDAG);
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
//        Processor mostPowerfulProcessor = processorDAG.getTheMostPowerfulProcessor();
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        schedule.addNewSlot(schedule.getFirstProcessorCoreFreeAt(0), entryTask, 0);

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());
            System.out.println("Current task: " + currentTask.getId());

            System.out.println("No of successor: " + currentTask.getSuccessors().size());

            double totalAdjacentDataConstraint = 0;
            int noOfSuccessorsAdjacentLayer = 0;

            for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                    totalAdjacentDataConstraint += currentTask.getSuccessors().get(j).getDataConstraint();
                    noOfSuccessorsAdjacentLayer += 1;
                }
            }
            System.out.println("No of successor in adjacent layer: " + noOfSuccessorsAdjacentLayer);

            double avgDataConstraint;

            if (noOfSuccessorsAdjacentLayer == 0) {
                avgDataConstraint = 0;
            } else {
                avgDataConstraint = totalAdjacentDataConstraint / noOfSuccessorsAdjacentLayer;
            }

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            double minAStepFurtherCost = Double.MAX_VALUE;
            ProcessorCore selectedProcessorCore = null;

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                double avgCommunicationTime = 0;

                if (currentProcessorCore.getProcessor().isFog()) {
                    if (noOfSuccessorsAdjacentLayer <= processorDAG.getNoOfFogs()) {
                        avgCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_LAN;
                    } else {
                        for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                            DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                            if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                                avgCommunicationTime += currentTask.getSuccessors().get(j).getDataConstraint() /
                                        currentProcessorCore.getProcessor().getWanUploadBandwidth();
                            }
                        }
                        avgCommunicationTime = avgCommunicationTime / totalAdjacentDataConstraint;
                    }
                } else {
                    if (noOfSuccessorsAdjacentLayer <= currentProcessorCore.getProcessor().getNoOfCores()) {
                        avgCommunicationTime = 0;
                    } else {
                        for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                            DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                            if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                                avgCommunicationTime += currentTask.getSuccessors().get(j).getDataConstraint() /
                                        currentProcessorCore.getProcessor().getWanUploadBandwidth();
                            }
                        }
                        avgCommunicationTime = avgCommunicationTime / totalAdjacentDataConstraint;
                    }
                }

                System.out.println("Current processor " + currentProcessorCore.getProcessor().getId() +
                        " current core " + currentProcessorCore.getCoreId() +
                        " ends at " + currentSelectedSlot.getEndTime() +
                        ", communication time: " + avgCommunicationTime + " equals " +
                        (currentSelectedSlot.getEndTime() + avgCommunicationTime));

                if (selectedSlot == null || currentSelectedSlot.getEndTime() + avgCommunicationTime < minAStepFurtherCost) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                    minAStepFurtherCost = currentSelectedSlot.getEndTime() + avgCommunicationTime;
                }
            }

            System.out.println("Select processor " + selectedProcessorCore.getProcessor().getId() +
                    " core " + selectedProcessorCore.getCoreId() +
                    ", ASF HEFT time: " + minAStepFurtherCost);
            System.out.println("============================================");
            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }

    public static LinkedList<Task> prioritizeTasks(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        double avgProcessingRate = processorDAG.getAvgProcessingRate();
        double avgBandwidth = processorDAG.getAvgBandwidthWithLAN();
//        double avgBandwidth = processorDAG.getAvgBandwidth();

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
