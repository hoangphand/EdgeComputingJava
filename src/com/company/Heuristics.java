package com.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

public class Heuristics {
    private static double BETA = 0.5;

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

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                if (selectedSlot == null || currentSelectedSlot.getEndTime() < selectedSlot.getEndTime()) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                }
            }

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
                        maxAvgCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_FOG_LAN;
                    } else {
                        double avgCommunicationTimeLAN = avgDataConstraint / Processor.BANDWIDTH_FOG_LAN;
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

    public static Schedule AStepFurtherHEFTAdjacentAvg(TaskDAG taskDAG, ProcessorDAG processorDAG) {
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

            double totalAdjacentDataConstraint = 0;
            int noOfSuccessorsAdjacentLayer = 0;

            for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                    totalAdjacentDataConstraint += currentTask.getSuccessors().get(j).getDataConstraint();
                    noOfSuccessorsAdjacentLayer += 1;
                }
            }

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
                        avgCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_FOG_LAN;
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

                if (selectedSlot == null || currentSelectedSlot.getEndTime() + avgCommunicationTime < minAStepFurtherCost) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                    minAStepFurtherCost = currentSelectedSlot.getEndTime() + avgCommunicationTime;
                }
            }

            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }

    public static Schedule DynamicASFHEFTAdjacentAvg(Schedule schedule, TaskDAG taskDAG) {
        Schedule tmpSchedule = new Schedule(schedule, taskDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, tmpSchedule.getProcessorDAG());
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
        ProcessorCore selectedProcessorCoreForEntryTask = schedule.getFirstProcessorCoreFreeAt(taskDAG.getArrivalTime());
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        tmpSchedule.addNewSlot(selectedProcessorCoreForEntryTask, entryTask, taskDAG.getArrivalTime());

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

            double totalAdjacentDataConstraint = 0;
            int noOfSuccessorsAdjacentLayer = 0;

            for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                    totalAdjacentDataConstraint += currentTask.getSuccessors().get(j).getDataConstraint();
                    noOfSuccessorsAdjacentLayer += 1;
                }
            }

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
            for (int i = 0; i < tmpSchedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = tmpSchedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                double avgCommunicationTime = 0;

                if (currentProcessorCore.getProcessor().isFog()) {
                    if (noOfSuccessorsAdjacentLayer <= tmpSchedule.getProcessorDAG().getNoOfFogs()) {
                        avgCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_FOG_LAN;
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

                if (selectedSlot == null || currentSelectedSlot.getEndTime() + avgCommunicationTime < minAStepFurtherCost) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                    minAStepFurtherCost = currentSelectedSlot.getEndTime() + avgCommunicationTime;
                }
            }
            tmpSchedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        tmpSchedule.setAFT(tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());
        tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());

        return tmpSchedule;
    }

    public static Schedule AStepFurtherHEFTAdjacentMax(TaskDAG taskDAG, ProcessorDAG processorDAG) {
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

            double totalAdjacentDataConstraint = 0;
            int noOfSuccessorsAdjacentLayer = 0;

            for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                    totalAdjacentDataConstraint += currentTask.getSuccessors().get(j).getDataConstraint();
                    noOfSuccessorsAdjacentLayer += 1;
                }
            }

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

                double maxCommunicationTime = -1;

                if (currentProcessorCore.getProcessor().isFog()) {
                    if (noOfSuccessorsAdjacentLayer <= processorDAG.getNoOfFogs()) {
                        maxCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_FOG_LAN;
                    } else {
                        maxCommunicationTime = avgDataConstraint / currentProcessorCore.getProcessor().getWanUploadBandwidth();
                    }
                } else {
                    if (noOfSuccessorsAdjacentLayer <= currentProcessorCore.getProcessor().getNoOfCores()) {
                        maxCommunicationTime = 0;
                    } else {
                        maxCommunicationTime = avgDataConstraint / currentProcessorCore.getProcessor().getWanUploadBandwidth();
                    }
                }

                if (selectedSlot == null || currentSelectedSlot.getEndTime() + maxCommunicationTime < minAStepFurtherCost) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                    minAStepFurtherCost = currentSelectedSlot.getEndTime() + maxCommunicationTime;
                }
            }

            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }

    public static Schedule DynamicASFHEFTAdjacentMax(Schedule schedule, TaskDAG taskDAG) {
        Schedule tmpSchedule = new Schedule(schedule, taskDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, tmpSchedule.getProcessorDAG());
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
        ProcessorCore selectedProcessorCoreForEntryTask = tmpSchedule.getFirstProcessorCoreFreeAt(taskDAG.getArrivalTime());
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        tmpSchedule.addNewSlot(selectedProcessorCoreForEntryTask, entryTask, taskDAG.getArrivalTime());

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());

            double totalAdjacentDataConstraint = 0;
            int noOfSuccessorsAdjacentLayer = 0;

            for (int j = 0; j < currentTask.getSuccessors().size(); j++) {
                DataDependency currentSuccessor = currentTask.getSuccessors().get(j);

                if (currentSuccessor.getTask().getLayerId() == currentTask.getLayerId() + 1) {
                    totalAdjacentDataConstraint += currentTask.getSuccessors().get(j).getDataConstraint();
                    noOfSuccessorsAdjacentLayer += 1;
                }
            }

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
            for (int i = 0; i < tmpSchedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = tmpSchedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);

                double maxCommunicationTime = -1;

                if (currentProcessorCore.getProcessor().isFog()) {
                    if (noOfSuccessorsAdjacentLayer <= schedule.getProcessorDAG().getNoOfFogs()) {
                        maxCommunicationTime = avgDataConstraint / Processor.BANDWIDTH_FOG_LAN;
                    } else {
                        maxCommunicationTime = avgDataConstraint / currentProcessorCore.getProcessor().getWanUploadBandwidth();
                    }
                } else {
                    if (noOfSuccessorsAdjacentLayer <= currentProcessorCore.getProcessor().getNoOfCores()) {
                        maxCommunicationTime = 0;
                    } else {
                        maxCommunicationTime = avgDataConstraint / currentProcessorCore.getProcessor().getWanUploadBandwidth();
                    }
                }

                if (selectedSlot == null || currentSelectedSlot.getEndTime() + maxCommunicationTime < minAStepFurtherCost) {
                    selectedSlot = currentSelectedSlot;
                    selectedProcessorCore = currentProcessorCore;
                    minAStepFurtherCost = currentSelectedSlot.getEndTime() + maxCommunicationTime;
                }
            }
            tmpSchedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        tmpSchedule.setAFT(tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());
        tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());

        return tmpSchedule;
    }

    public static Schedule CompromiseMKCR(TaskDAG taskDAG, ProcessorDAG processorDAG) {
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
            System.out.println("Id: " + currentTask.getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;
            double maxMKCR = Double.MIN_VALUE;
            double minMKCR = Double.MAX_VALUE;

            ArrayList<Double> listOfCloudCost = new ArrayList<Double>();
            ArrayList<Double> listOfTaskEF = new ArrayList<Double>();
            ArrayList<Slot> listOfTmpSlots = new ArrayList<Slot>();

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = schedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);
                double cloudCost = 0;
                double taskMakespan = currentSelectedSlot.getEndTime() - currentSelectedSlot.getStartTime();

                if (!currentProcessorCore.getProcessor().isFog()) {
                    cloudCost = taskMakespan * currentProcessorCore.getProcessor().getCostPerTimeUnit();
                }

                listOfCloudCost.add(cloudCost);
                listOfTaskEF.add(currentSelectedSlot.getEndTime());
                listOfTmpSlots.add(currentSelectedSlot);
            }

            double minCloudCost = Double.MAX_VALUE;
            double maxCloudCost = Double.MIN_VALUE;
            double totalCloudCost = 0;
            double minTaskEF = Double.MAX_VALUE;
            double maxTaskEF = Double.MIN_VALUE;
            double totalTaskEF = 0;

            for (int i = 0; i < listOfCloudCost.size(); i++) {
                totalCloudCost += listOfCloudCost.get(i);

                if (listOfCloudCost.get(i) < minCloudCost) {
                    minCloudCost = listOfCloudCost.get(i);
                }

                if (listOfCloudCost.get(i) > maxCloudCost) {
                    maxCloudCost = listOfCloudCost.get(i);
                }
            }

            for (int i = 0; i < listOfTaskEF.size(); i++) {
                totalTaskEF += listOfTaskEF.get(i);

                if (listOfTaskEF.get(i) < minTaskEF) {
                    minTaskEF = listOfTaskEF.get(i);
                }

                if (listOfTaskEF.get(i) > maxTaskEF) {
                    maxTaskEF = listOfTaskEF.get(i);
                }
            }

            double avgCloudCost = totalCloudCost / listOfCloudCost.size();
            double avgTaskEF = totalTaskEF / listOfTaskEF.size();
            double beta = 0.4;

            for (int i = 0; i < listOfCloudCost.size(); i++) {
                double tmpMKCR;

//                Pure Pareto
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - beta) * listOfCloudCost.get(i) + beta * listOfTaskEF.get(i);
//                }
//
//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using sum of percentage
                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
                    tmpMKCR = listOfTaskEF.get(i);
                } else {
                    double partOfCost = (listOfCloudCost.get(i)) / (maxCloudCost);
                    double partOfEF = (listOfTaskEF.get(i) - minTaskEF) / (maxTaskEF - minTaskEF);
//                    double partOfCost = Math.abs(listOfCloudCost.get(i) - avgCloudCost) / (maxCloudCost - minCloudCost);
//                    double partOfEF = Math.abs(listOfTaskEF.get(i) - avgTaskEF) / (maxTaskEF - minTaskEF);

//                    tmpMKCR = partOfCost + partOfEF;
                    tmpMKCR = BETA * partOfCost + (1 - BETA) * partOfEF;

                    if (listOfTmpSlots.get(i).getProcessor().isFog()) {
                        System.out.println("i: " + i + " (Fog) " +
                                ", partOfCost: " + partOfCost + ", partOfEF: " + partOfEF + ", tmpMKCR: " + tmpMKCR);
                    } else {
//                        System.out.println("Current cloud cost: " + listOfCloudCost.get(i));
                        System.out.println("i: " + i + " (Cloud) " +
                                ", partOfCost: " + partOfCost + ", partOfEF: " + partOfEF + ", tmpMKCR: " + tmpMKCR);
                    }
                }

                if (tmpMKCR < minMKCR) {
                    minMKCR = tmpMKCR;
                    selectedSlot = listOfTmpSlots.get(i);
                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
                }

//                normalizing using sum of max value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - BETA) * listOfCloudCost.get(i) / maxCloudCost + BETA * listOfTaskEF.get(i) / maxTaskEF;
//                }
//
//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using sum of min value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - BETA) * minCloudCost / listOfCloudCost.get(i) + BETA * minTaskEF / listOfTaskEF.get(i);
//                }
//
//                if (tmpMKCR > maxMKCR) {
//                    maxMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using product of min value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = minTaskEF / listOfTaskEF.get(i);
//                } else {
//                    if (listOfTmpSlots.get(i).getProcessor().isFog()) {
//                        tmpMKCR = minTaskEF / listOfTaskEF.get(i);
//                    } else {
//                        tmpMKCR = minCloudCost / listOfCloudCost.get(i) * minTaskEF / listOfTaskEF.get(i);
//                    }
//                }
//
//                if (tmpMKCR > maxMKCR) {
//                    maxMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using product of max value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i) / maxTaskEF;
//                } else {
//                    if (listOfTmpSlots.get(i).getProcessor().isFog()) {
//                        tmpMKCR = listOfTaskEF.get(i) / maxTaskEF;
//                    } else {
//                        tmpMKCR = listOfCloudCost.get(i) / maxCloudCost * listOfTaskEF.get(i) / maxTaskEF;
//                    }
//                }
//
//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }
//
//                if (currentTask.getId() == 101) {
//                    System.out.println(maxTaskEF);
//                }

//                normalizing using sum of average value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - BETA) * listOfCloudCost.get(i) / avgCloudCost + BETA * listOfTaskEF.get(i) / avgTaskEF;
//                }
//
//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }
            }

            System.out.println("selected: " + selectedProcessorCore.getSchedulePositionId());
            schedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        schedule.setAFT(schedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return schedule;
    }

    public static Schedule DynamicCompromiseMKCR(Schedule schedule, TaskDAG taskDAG, double beta) {
        Schedule tmpSchedule = new Schedule(schedule, taskDAG);
//        prioritize tasks
//        init list of unscheduled tasks
        LinkedList<Task> unscheduledTasks = Heuristics.prioritizeTasks(taskDAG, tmpSchedule.getProcessorDAG());
//        get the entry task by popping the first task from the unscheduled list
        Task entryTask = unscheduledTasks.removeLast();
//        get the most powerful processor in the network
//        Processor mostPowerfulProcessor = processorDAG.getTheMostPowerfulProcessor();
//        allocate dummy entry task on the most powerful processing node at timestamp 0
        ProcessorCore selectedProcessorCoreForEntryTask = schedule.getFirstProcessorCoreFreeAt(taskDAG.getArrivalTime());

        tmpSchedule.addNewSlot(selectedProcessorCoreForEntryTask, entryTask, taskDAG.getArrivalTime());

//        loop through all unscheduled tasks
        while (unscheduledTasks.size() > 0) {
            Task currentTask = taskDAG.getTasks().get(unscheduledTasks.removeLast().getId());
//            System.out.println("Id: " + currentTask.getId());

//            calculate ready time to calculate current task on all the processors in the network
            Slot selectedSlot = null;
            ProcessorCore selectedProcessorCore = null;
            double maxMKCR = Double.MIN_VALUE;
            double minMKCR = Double.MAX_VALUE;

            ArrayList<Double> listOfCloudCost = new ArrayList<Double>();
            ArrayList<Double> listOfTaskEF = new ArrayList<Double>();
            ArrayList<Slot> listOfTmpSlots = new ArrayList<Slot>();

//            loop through all processors to find the best processing execution location
            for (int i = 0; i < tmpSchedule.getProcessorCoreExecutionSlots().size(); i++) {
                ProcessorCore currentProcessorCore = tmpSchedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
//                find the first-fit slot on the current processor for the current task
                Slot currentSelectedSlot = tmpSchedule.getFirstFitSlotForTaskOnProcessorCore(currentProcessorCore, currentTask);
                double cloudCost = 0;
                double taskMakespan = currentSelectedSlot.getEndTime() - currentSelectedSlot.getStartTime();

                if (!currentProcessorCore.getProcessor().isFog()) {
                    cloudCost = taskMakespan * currentProcessorCore.getProcessor().getCostPerTimeUnit();
                }

                listOfCloudCost.add(cloudCost);
                listOfTaskEF.add(currentSelectedSlot.getEndTime());
                listOfTmpSlots.add(currentSelectedSlot);
            }

            double minCloudCost = Double.MAX_VALUE;
            double maxCloudCost = Double.MIN_VALUE;
            double totalCloudCost = 0;
            double minTaskEF = Double.MAX_VALUE;
            double maxTaskEF = Double.MIN_VALUE;
            double totalTaskEF = 0;

            for (int i = 0; i < listOfCloudCost.size(); i++) {
                totalCloudCost += listOfCloudCost.get(i);

                if (listOfCloudCost.get(i) < minCloudCost) {
                    minCloudCost = listOfCloudCost.get(i);
                }

                if (listOfCloudCost.get(i) > maxCloudCost) {
                    maxCloudCost = listOfCloudCost.get(i);
                }
            }

            for (int i = 0; i < listOfTaskEF.size(); i++) {
                totalTaskEF += listOfTaskEF.get(i);

                if (listOfTaskEF.get(i) < minTaskEF) {
                    minTaskEF = listOfTaskEF.get(i);
                }

                if (listOfTaskEF.get(i) > maxTaskEF) {
                    maxTaskEF = listOfTaskEF.get(i);
                }
            }

            double avgCloudCost = totalCloudCost / listOfCloudCost.size();
            double avgTaskEF = totalTaskEF / listOfTaskEF.size();
//            double beta = 0.5;

            for (int i = 0; i < listOfCloudCost.size(); i++) {
                double tmpMKCR;

//                normalizing using sum of percentage
                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
                    tmpMKCR = listOfTaskEF.get(i);
                } else {
                    double partOfCost = (listOfCloudCost.get(i) - minCloudCost) / (maxCloudCost - minCloudCost);
                    double partOfEF = (listOfTaskEF.get(i) - minTaskEF) / (maxTaskEF - minTaskEF);

//                    tmpMKCR =  partOfCost + partOfEF;
//                    tmpMKCR = BETA * partOfCost + (1 - BETA) * partOfEF;
                    tmpMKCR = beta * partOfCost + (1 - beta) * partOfEF;

//                    System.out.println("i: " + i + ", partOfCost: " + partOfCost + ", partOfEF: " + partOfEF + ", tmpMKCR: " + tmpMKCR);
                }

                if (tmpMKCR < minMKCR) {
                    minMKCR = tmpMKCR;
                    selectedSlot = listOfTmpSlots.get(i);
                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
                }

//                normalizing using sum of max value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - BETA) * listOfCloudCost.get(i) / maxCloudCost + BETA * listOfTaskEF.get(i) / maxTaskEF;
//                }
//
//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using sum of min value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - BETA) * minCloudCost / listOfCloudCost.get(i) + BETA * minTaskEF / listOfTaskEF.get(i);
//                }
//
//                if (tmpMKCR > maxMKCR) {
//                    maxMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using product of min value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = minTaskEF / listOfTaskEF.get(i);
//                } else {
//                    if (listOfTmpSlots.get(i).getProcessor().isFog()) {
//                        tmpMKCR = minTaskEF / listOfTaskEF.get(i);
//                    } else {
//                        tmpMKCR = minCloudCost / listOfCloudCost.get(i) * minTaskEF / listOfTaskEF.get(i);
//                    }
//                }
//
//                if (tmpMKCR > maxMKCR) {
//                    maxMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using product of max value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i) / maxTaskEF;
//                } else {
//                    if (listOfTmpSlots.get(i).getProcessor().isFog()) {
//                        tmpMKCR = listOfTaskEF.get(i) / maxTaskEF;
//                    } else {
//                        tmpMKCR = listOfCloudCost.get(i) / maxCloudCost * listOfTaskEF.get(i) / maxTaskEF;
//                    }
//                }
//
//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }

//                normalizing using sum of average value
//                if (currentTask.getId() == taskDAG.getTasks().size() - 1) {
//                    tmpMKCR = listOfTaskEF.get(i);
//                } else {
//                    tmpMKCR = (1 - BETA) * listOfCloudCost.get(i) / avgCloudCost + BETA * listOfTaskEF.get(i) / avgTaskEF;
//                }

//                if (tmpMKCR < minMKCR) {
//                    minMKCR = tmpMKCR;
//                    selectedSlot = listOfTmpSlots.get(i);
//                    selectedProcessorCore = listOfTmpSlots.get(i).getProcessorCore();
//                }
            }

            tmpSchedule.addNewSlot(selectedProcessorCore, currentTask, selectedSlot.getStartTime());
        }

        tmpSchedule.setAFT(tmpSchedule.getTaskExecutionSlot().get(taskDAG.getTasks().size() - 1).getEndTime());

        return tmpSchedule;
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
