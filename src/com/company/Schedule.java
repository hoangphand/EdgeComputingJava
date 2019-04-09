package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Schedule {
    private TaskDAG taskDAG;
    private ProcessorDAG processorDAG;
    private ArrayList<Slot> taskExecutionSlot;
    private ArrayList<ArrayList<Slot>> processorCoreExecutionSlots;
    private double aft;
    private double ast;

    public Schedule(TaskDAG taskDAG, ProcessorDAG processorDAG) {
        this.taskDAG = taskDAG;
        this.processorDAG = processorDAG;

//        count variable for indexing processorCores
        int schedulePositionId = 0;
        this.processorCoreExecutionSlots = new ArrayList<ArrayList<Slot>>();
        for (int i = 0; i < processorDAG.getProcessors().size(); i++) {
            Processor currentProcessor = processorDAG.getProcessors().get(i);

            for (int j = 0; j < currentProcessor.getNoOfCores(); j++) {
                ProcessorCore newCore = new ProcessorCore(currentProcessor, j, schedulePositionId);
                Slot newSlot = new Slot(null, newCore, 0, Integer.MAX_VALUE);

                ArrayList<Slot> newListOfSlots = new ArrayList<Slot>();
                newListOfSlots.add(newSlot);

                this.processorCoreExecutionSlots.add(newListOfSlots);

                schedulePositionId += 1;
            }
        }

        this.taskExecutionSlot = new ArrayList<Slot>();
        for (int i = 0; i < this.taskDAG.getTasks().size(); i++) {
            this.taskExecutionSlot.add(null);
        }
    }

    //    add a new slot for a task on a processor at time startTime
    public void addNewSlot(ProcessorCore processorCore, Task task, double startTime) {
        ArrayList<Slot> currentProcessorCoreSlots = this.processorCoreExecutionSlots.get(
                processorCore.getSchedulePositionId()
        );

        double computationTime = task.getComputationRequired() / processorCore.getProcessor().getProcessingRate();

        int count = 0;
        for (int i = 0; i < currentProcessorCoreSlots.size(); i++) {
            Slot currentSlot = currentProcessorCoreSlots.get(i);
            count = i;
            double actualStartTime = Math.max(startTime, currentSlot.getStartTime());
            double endTime = actualStartTime + computationTime;

//            find the first slot on the processor that fits the startTime and endTime
            if (currentSlot.getTask() == null &&
                    currentSlot.getStartTime() <= actualStartTime &&
                    currentSlot.getEndTime() >= endTime) {
                Slot newSlot = new Slot(task, processorCore, startTime, endTime);
                currentProcessorCoreSlots.add(newSlot);

                Slot slotBefore = new Slot(null, processorCore, currentSlot.getStartTime(), startTime);
                Slot slotAfter = new Slot(null, processorCore, endTime, currentSlot.getEndTime());

                if (startTime != currentSlot.getStartTime() && endTime != currentSlot.getEndTime()) {
                    currentProcessorCoreSlots.add(slotBefore);
                    currentProcessorCoreSlots.add(slotAfter);
                } else if (startTime == currentSlot.getStartTime() && endTime != currentSlot.getEndTime()) {
                    currentProcessorCoreSlots.add(slotAfter);
                } else if (startTime != currentSlot.getStartTime() && endTime == currentSlot.getEndTime()) {
                    currentProcessorCoreSlots.add(slotBefore);
                }

                currentProcessorCoreSlots.remove(i);

                Collections.sort(currentProcessorCoreSlots, Slot.compareByStartTime);

                this.taskExecutionSlot.set(task.getId(), newSlot);
                break;
            }
        }

        if (count == currentProcessorCoreSlots.size() - 1) {
            System.out.println("Found no suitable slots on processor " + processorCore.getProcessor().getId() +
                    " core " + processorCore.getCoreId() + "!");
        }
    }

//    this function calculates the earliest slot that a processor
//    which will be able to execute a specified task
    public Slot getFirstFitSlotForTaskOnProcessorCore(ProcessorCore processorCore, Task task) {
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
//            System.out.println("current pred: " + predTask.getId());
            ProcessorCore predProcessorCore = this.taskExecutionSlot.get(predTask.getId()).getProcessorCore();

//            calculate communication time to transmit data dependency from
//            processor which is assigned to process the predecessor task to
//            the processor which is being considered to use to process the current task
            double communicationTime = this.processorDAG.getCommunicationTimeBetweenCores(
                    predProcessorCore, processorCore, predTaskConstraint
            );

            double predSlotEndTime = this.taskExecutionSlot.get(predTask.getId()).getEndTime();
            double currentReadyTime = predSlotEndTime + communicationTime;

            if (currentReadyTime > readyTime) {
                readyTime = currentReadyTime;
            }
        }

        double processingTime = task.getComputationRequired() / processorCore.getProcessor().getProcessingRate();
        ArrayList<Slot> currentProcessorSlots = this.processorCoreExecutionSlots.get(
                processorCore.getSchedulePositionId()
        );

//        find the earliest slot
        for (int i = 0; i < currentProcessorSlots.size(); i++) {
            Slot currentSlot = currentProcessorSlots.get(i);

            if (currentSlot.getTask() == null) {
                double actualStart = Math.max(currentSlot.getStartTime(), readyTime);
                double actualEnd = actualStart + processingTime;

                if (actualEnd <= currentSlot.getEndTime()) {
//                    return the first fit slot for the task on the current processor
                    return new Slot(task, processorCore, actualStart, actualEnd);
                }
            }
        }

        System.out.println("Nothing");
        return new Slot(task, processorCore, -1, -1);
    }

    //    this function calculates the earliest slot that a processor
//    which will be able to execute a specified task
    public Slot getBestProcessorCoreForAStepFurtherHEFT(ProcessorCore processorCore, Task task) {
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
//            System.out.println("current pred: " + predTask.getId());
            ProcessorCore predProcessorCore = this.taskExecutionSlot.get(predTask.getId()).getProcessorCore();

//            calculate communication time to transmit data dependency from
//            processor which is assigned to process the predecessor task to
//            the processor which is being considered to use to process the current task
            double communicationTime = this.processorDAG.getCommunicationTimeBetweenCores(
                    predProcessorCore, processorCore, predTaskConstraint
            );

            double predSlotEndTime = this.taskExecutionSlot.get(predTask.getId()).getEndTime();
            double currentReadyTime = predSlotEndTime + communicationTime;

            if (currentReadyTime > readyTime) {
                readyTime = currentReadyTime;
            }
        }

        double processingTime = task.getComputationRequired() / processorCore.getProcessor().getProcessingRate();
        ArrayList<Slot> currentProcessorSlots = this.processorCoreExecutionSlots.get(
                processorCore.getSchedulePositionId()
        );

//        find the earliest slot
        for (int i = 0; i < currentProcessorSlots.size(); i++) {
            Slot currentSlot = currentProcessorSlots.get(i);

            if (currentSlot.getTask() == null) {
                double actualStart = Math.max(currentSlot.getStartTime(), readyTime);
                double actualEnd = actualStart + processingTime;

                if (actualEnd <= currentSlot.getEndTime()) {
//                    return the first fit slot for the task on the current processor
                    return new Slot(task, processorCore, actualStart, actualEnd);
                }
            }
        }

        System.out.println("Nothing");
        return new Slot(task, processorCore, -1, -1);
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

                    double tmpCost = this.processorDAG.getCommunicationTimeBetweenCores(
                            this.taskExecutionSlot.get(currentPred.getTask().getId()).getProcessorCore(),
                            this.taskExecutionSlot.get(currentTask.getId()).getProcessorCore(),
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

            double tmpCost = this.processorDAG.getCommunicationTimeBetweenCores(
                    this.taskExecutionSlot.get(currentPred.getTask().getId()).getProcessorCore(),
                    this.taskExecutionSlot.get(currentTask.getId()).getProcessorCore(),
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

    public ProcessorCore getFirstProcessorCoreFreeAt(double time) {
        ProcessorCore selectedProcessorCore = null;
        double earliestStartTime = Double.MAX_VALUE;

        for (int positionId = 0; positionId < this.processorCoreExecutionSlots.size(); positionId++) {
            for (int slotId = 0; slotId < this.processorCoreExecutionSlots.get(positionId).size(); slotId++) {
                Slot currentSlot = this.processorCoreExecutionSlots.get(positionId).get(slotId);

                if (currentSlot.getTask() == null && currentSlot.getEndTime() >= time) {
                    if (Math.max(currentSlot.getStartTime(), time) <= earliestStartTime) {
                        earliestStartTime = Math.max(currentSlot.getStartTime(), time);
                        selectedProcessorCore = this.processorCoreExecutionSlots.get(positionId).get(0).getProcessorCore();
                    }
                }
            }
        }

        return selectedProcessorCore;
    }

    public int countSlotsInNetwork() {
        int count = 0;

        for (int i = 0; i < this.processorCoreExecutionSlots.size(); i++) {
            count += this.processorCoreExecutionSlots.get(i).size();
        }

        return count;
    }

    public void setAFT(double aft) {
        this.aft = aft;
    }

    public double getAFT() {
        return this.aft;
    }

    public void setAST(double ast) {
        this.ast = ast;
    }

    public double getAST() {
        return this.ast;
    }

    public ProcessorDAG getProcessorDAG() {
        return this.processorDAG;
    }

    public ArrayList<ArrayList<Slot>> getProcessorCoreExecutionSlots() {
        return this.processorCoreExecutionSlots;
    }

    public void setProcessorExecutionSlots(ArrayList<ArrayList<Slot>> processorCoreExecutionSlots) {
        this.processorCoreExecutionSlots = processorCoreExecutionSlots;
    }

    public ArrayList<Slot> getTaskExecutionSlot() {
        return this.taskExecutionSlot;
    }

    public void showProcessorSlots() {
        for (int i = 0; i < this.processorCoreExecutionSlots.size(); i++) {
            ArrayList<Slot> currentProcessorCoreSlots = this.processorCoreExecutionSlots.get(i);

            if (currentProcessorCoreSlots.size() > 1) {
                System.out.print("Processor " + currentProcessorCoreSlots.get(0).getProcessor().getId() +
                        ", core " + currentProcessorCoreSlots.get(0).getProcessorCore().getCoreId() + ": ");
                for (int j = 0; j < currentProcessorCoreSlots.size(); j++) {
                    Slot currentSlot = currentProcessorCoreSlots.get(j);
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

        this.processorCoreExecutionSlots = new ArrayList<ArrayList<Slot>>();
        for (int i = 0; i < schedule.processorCoreExecutionSlots.size(); i++) {
            ProcessorCore currentProcessorCore = schedule.processorCoreExecutionSlots.get(i).get(0).getProcessorCore();
            ArrayList<Slot> newListOfSlots = new ArrayList<Slot>();

            for (int j = 0; j < schedule.processorCoreExecutionSlots.get(i).size(); j++) {
                Slot currentOldSlot = schedule.processorCoreExecutionSlots.get(i).get(j);
                Slot newSlot = new Slot(currentOldSlot.getTask(), currentProcessorCore,
                        currentOldSlot.getStartTime(), currentOldSlot.getEndTime());

                newListOfSlots.add(newSlot);
            }

            this.processorCoreExecutionSlots.add(newListOfSlots);
        }

        this.taskExecutionSlot = new ArrayList<Slot>();
        for (int i = 0; i < this.taskDAG.getTasks().size(); i++) {
            this.taskExecutionSlot.add(null);
        }
    }

    public TaskDAG getTaskDAG() {
        return this.taskDAG;
    }

    public double getActualStartTimeOfDAG() {
        double actualStartTime = Double.MAX_VALUE;

        for (int i = 0; i < this.taskDAG.getLayers().get(1).size(); i++) {
            Task currentTask = this.taskDAG.getLayers().get(1).get(i);
            Slot currentTaskSlot = this.taskExecutionSlot.get(currentTask.getId());

            if (actualStartTime > currentTaskSlot.getStartTime()) {
                actualStartTime = currentTaskSlot.getStartTime();
            }
        }

        return actualStartTime;
    }

    public double getActualWaitingTime() {
        return this.ast - this.taskDAG.getArrivalTime();
    }

    public void deepCopy(Schedule schedule) {
        this.aft = schedule.aft;
        this.ast = schedule.ast;
        this.taskDAG = schedule.taskDAG;
        this.processorCoreExecutionSlots = schedule.processorCoreExecutionSlots;
        this.processorDAG = schedule.processorDAG;
        this.taskExecutionSlot = schedule.taskExecutionSlot;
    }
}
