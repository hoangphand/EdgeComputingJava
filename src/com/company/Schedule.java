package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Schedule {
    private TaskDAG taskDAG;
    private ProcessorDAG processorDAG;
    private ArrayList<Slot> taskExecutionSlot;
    private ArrayList<ArrayList<Slot>> processorExecutionSlots;
    private double aft;

    public Schedule(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        this.taskDAG = taskDAG;
        this.processorDAG = processorDAG;

        this.processorExecutionSlots = new ArrayList<ArrayList<Slot>>();
        for (int i = 0; i < processorDAG.getProcessors().size(); i++) {
            Processor currentProcessor = processorDAG.getProcessors().get(i);
            Slot newSlot = new Slot(null, currentProcessor, 0, Integer.MAX_VALUE);
            ArrayList<Slot> newListOfSlots = new ArrayList<Slot>();

            newListOfSlots.add(newSlot);
            this.processorExecutionSlots.add(newListOfSlots);
        }

        this.taskExecutionSlot = new ArrayList<Slot>();
        for (int i = 0; i < this.taskDAG.getTasks().size(); i++) {
            this.taskExecutionSlot.add(null);
        }
    }

    //    add a new slot for a task on a processor at time startTime
    public void addNewSlot(Processor processor, Task task, double startTime) {
//        System.out.println("Task id: " + task.getId());
        ArrayList<Slot> currentProcessorSlots = this.processorExecutionSlots.get(processor.getId());

        double computationTime = task.getComputationRequired() / processor.getProcessingRate();
        double endTime = startTime + computationTime;

        for (int i = 0; i < currentProcessorSlots.size(); i++) {
            Slot currentSlot = currentProcessorSlots.get(i);

//            find the first slot on the processor that fits the startTime and endTime
            if (currentSlot.getTask() == null &&
                    currentSlot.getStartTime() <= startTime &&
                    currentSlot.getEndTime() >= endTime) {
                Slot newSlot = new Slot(task, processor, startTime, endTime);
                currentProcessorSlots.add(newSlot);

                Slot slotBefore = new Slot(null, processor, currentSlot.getStartTime(), startTime);
                Slot slotAfter = new Slot(null, processor, endTime, currentSlot.getEndTime());

                if (startTime != currentSlot.getStartTime() && endTime != currentSlot.getEndTime()) {
                    currentProcessorSlots.add(slotBefore);
                    currentProcessorSlots.add(slotAfter);
                } else if (startTime == currentSlot.getStartTime() && endTime != currentSlot.getEndTime()) {
                    currentProcessorSlots.add(slotAfter);
                } else if (startTime != currentSlot.getStartTime() && endTime == currentSlot.getEndTime()) {
                    currentProcessorSlots.add(slotBefore);
                }

                currentProcessorSlots.remove(i);

                Collections.sort(currentProcessorSlots, Slot.compareByStartTime);

                this.taskExecutionSlot.set(task.getId(), newSlot);
                break;
            }
        }
    }

//    this function calculates the earliest slot that a processor
//    which will be able to execute a specified task
    public Slot getFirstFitSlotForTaskOnProcessor(Processor processor, Task task) {
//        the ready time of the task at which
//        all required input data has arrived at the current processor
        double readyTime = -1;

//        loop through all predecessors of the current task
//        to calculate readyTime
        for (int i = 0; i < task.getPredecessors().size(); i++) {
//            get predecessor task in the tuple of (task, dependency)
            Task predTask = task.getPredecessors().get(i).getTask();
//            get dependency of current predecessor
            double predTaskConstraint = task.getPredecessors().get(i).getDataConstraint();
//            get processor which processes the current predecessor task
            Processor predProcessor = this.taskExecutionSlot.get(predTask.getId()).getProcessor();

//            calculate communication time to transmit data dependency from
//            processor which is assigned to process the predecessor task to
//            the processor which is being considered to use to process the current task
            double communicationTime = this.processorDAG.getCommunicationTime(predProcessor, processor, predTaskConstraint);

            double predSlotEndTime = this.taskExecutionSlot.get(predTask.getId()).getEndTime();
            double currentReadyTime = predSlotEndTime + communicationTime;

            if (currentReadyTime > readyTime) {
                readyTime = currentReadyTime;
            }
        }

        double processingTime = task.getComputationRequired() / processor.getProcessingRate();
        ArrayList<Slot> currentProcessorSlots = this.processorExecutionSlots.get(processor.getId());

//        find the earliest slot
        for (int i = 0; i < currentProcessorSlots.size(); i++) {
            Slot currentSlot = currentProcessorSlots.get(i);

            if (currentSlot.getTask() == null) {
                double actualStart = Math.max(currentSlot.getStartTime(), readyTime);
                double actualEnd = actualStart + processingTime;

                if (actualEnd <= currentSlot.getEndTime()) {
//                    return the first fit slot for the task on the current processor
                    return new Slot(task, processor, actualStart, actualEnd);
                }
            }
        }

        System.out.println("Nothing");
        return new Slot(task, processor, -1, -1);
    }

    public double getTotalComputationCost() {
        double totalComputationCost = 0;

        for (int i = 0; i < this.taskExecutionSlot.size(); i++) {
            totalComputationCost += this.getComputationCostOfTask(i);
        }

        return totalComputationCost;
    }

    public double getTotalCommunicationCost() {
        double totalCommunicationCost = 0;

        for (int layerId = 0; layerId < this.taskDAG.getLayers().size(); layerId++) {
            for (int taskId = 0; taskId < this.taskDAG.getLayers().get(layerId).size(); taskId++) {
                Task currentTask = this.taskDAG.getLayers().get(layerId).get(taskId);

                for (int predId = 0; predId < currentTask.getPredecessors().size(); predId++) {
                    DataDependency currentPred = currentTask.getPredecessors().get(predId);

                    double tmpCost = this.processorDAG.getCommunicationTime(
                            this.taskExecutionSlot.get(currentPred.getTask().getId()).getProcessor(),
                            this.taskExecutionSlot.get(currentTask.getId()).getProcessor(),
                            currentPred.getDataConstraint()
                    );

                    totalCommunicationCost += tmpCost;
                }
            }
        }

        return totalCommunicationCost;
    }

    public double getComputationCostOfTask(Task task) {
        return this.getComputationCostOfTask(task.getId());
    }

    public double getComputationCostOfTask(int taskId) {
        Slot taskSlot = this.taskExecutionSlot.get(taskId);

        return taskSlot.getTask().getComputationRequired() / taskSlot.getProcessor().getProcessingRate();
    }

    public double getMaxPredCommunicationCost(int taskId) {
        double maxCommunicationCost = -1;

        Task currentTask = this.taskDAG.getTasks().get(taskId);

        for (int predIndex = 0; predIndex < currentTask.getPredecessors().size(); predIndex++) {
            DataDependency currentPred = currentTask.getPredecessors().get(predIndex);

            double tmpCost = this.processorDAG.getCommunicationTime(
                    this.taskExecutionSlot.get(currentPred.getTask().getId()).getProcessor(),
                    this.taskExecutionSlot.get(currentTask.getId()).getProcessor(),
                    currentPred.getDataConstraint()
            );

            if (maxCommunicationCost < tmpCost) {
                maxCommunicationCost = tmpCost;
            }
        }

        return maxCommunicationCost;
    }

    public int getNoOfTasksAllocatedToCloudNodes() {
        int noOfTasksAllocatedToCloudNodes = 0;

        for (int i = 1; i < this.taskDAG.getTasks().size() - 1; i++) {
            if (!this.taskExecutionSlot.get(i).getProcessor().isFog()) {
                noOfTasksAllocatedToCloudNodes += 1;
            }
        }

        return noOfTasksAllocatedToCloudNodes;
    }

    public int getNoOfTasksAllocatedToFogNodes() {
        return this.taskDAG.getTasks().size() - this.getNoOfTasksAllocatedToCloudNodes() - 2;
    }

    public Processor getFirstProcessorFreeAt(double time) {
        Processor selectedProcessor = null;
        double earliestStartTime = Double.MAX_VALUE;
        Slot selectedSlot = null;

        for (int processorId = 0; processorId < this.processorDAG.getProcessors().size(); processorId++) {
            for (int slotId = 0; slotId < this.processorExecutionSlots.get(processorId).size(); slotId++) {
                Slot currentSlot = this.processorExecutionSlots.get(processorId).get(slotId);

                if (currentSlot.getTask() == null && currentSlot.getStartTime() <= time && currentSlot.getEndTime() >= time) {
                    if (currentSlot.getStartTime() < earliestStartTime) {
                        selectedSlot = currentSlot;
                        earliestStartTime = currentSlot.getStartTime();
                        selectedProcessor = this.processorDAG.getProcessors().get(processorId);
                    }
                }
            }
        }

//        System.out.println("Found " + selectedProcessor.getId() + " at " + earliestStartTime +
//                " with slot[" + selectedSlot.getStartTime() + "--" + selectedSlot.getEndTime() + "]");
        return selectedProcessor;
    }

    public int countSlotsInNetwork() {
        int count = 0;

        for (int i = 0; i < this.processorDAG.getProcessors().size(); i++) {
            count += this.processorExecutionSlots.get(i).size();
        }

        return count;
    }

    public void setAFT(double aft) {
        this.aft = aft;
    }

    public double getAFT() {
        return this.aft;
    }

    public ProcessorDAG getProcessorDAG() {
        return this.processorDAG;
    }

    public ArrayList<ArrayList<Slot>> getProcessorExecutionSlots() {
        return this.processorExecutionSlots;
    }

    public void setProcessorExecutionSlots(ArrayList<ArrayList<Slot>> processorExecutionSlots) {
        this.processorExecutionSlots = processorExecutionSlots;
    }

    public ArrayList<Slot> getTaskExecutionSlot() {
        return this.taskExecutionSlot;
    }

    public void showProcessorSlots() {
        for (int i = 0; i < this.processorExecutionSlots.size(); i++) {
            ArrayList<Slot> currentProcessorSlots = this.processorExecutionSlots.get(i);

            if (currentProcessorSlots.size() > 1) {
                System.out.print("Processor " + i + ": ");
                for (int j = 0; j < currentProcessorSlots.size(); j++) {
                    Slot currentSlot = currentProcessorSlots.get(j);
                    System.out.print("[");
                    if (currentSlot.getTask() == null) {
                        System.out.print(PrintUtils.ANSI_RED_BACKGROUND + "n" + PrintUtils.ANSI_RESET);
                    } else {
                        System.out.print(PrintUtils.ANSI_GREEN_BACKGROUND + currentSlot.getTask().getId() + PrintUtils.ANSI_RESET);
                    }
                    System.out.print(currentSlot.getStartTime() + "-" + currentSlot.getEndTime() + "]--");
                }
                System.out.println();
            }
        }
    }

    public Schedule(Schedule schedule, TaskDAG taskDAG) {
        this.taskDAG = taskDAG;
        this.processorDAG = schedule.processorDAG;

        this.processorExecutionSlots = new ArrayList<ArrayList<Slot>>();
        for (int i = 0; i < this.processorDAG.getProcessors().size(); i++) {
            Processor currentProcessor = this.processorDAG.getProcessors().get(i);
            ArrayList<Slot> newListOfSlots = new ArrayList<Slot>();

            for (int j = 0; j < schedule.processorExecutionSlots.get(currentProcessor.getId()).size(); j++) {
                Slot currentOldSlot = schedule.processorExecutionSlots.get(currentProcessor.getId()).get(j);
                Slot newSlot = new Slot(currentOldSlot.getTask(), currentProcessor,
                        currentOldSlot.getStartTime(), currentOldSlot.getEndTime());

                newListOfSlots.add(newSlot);
            }

            this.processorExecutionSlots.add(newListOfSlots);
        }

        this.taskExecutionSlot = new ArrayList<Slot>();
        for (int i = 0; i < this.taskDAG.getTasks().size(); i++) {
            this.taskExecutionSlot.add(null);
        }
    }

    public TaskDAG getTaskDAG() {
        return this.taskDAG;
    }
}
