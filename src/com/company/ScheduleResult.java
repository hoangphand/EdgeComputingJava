package com.company;

public class ScheduleResult {
    private boolean isAccepted;
    private double ast;
    private double aft;
    private double makespan;
    private int noOfCloudNodesUsed;
    private int noOfFogNodesUsed;
    private int noOfSlots;
    private double earlyTime;
    private double lateTime;
    private double percentageEarly;
    private double percentageLate;
    private TaskDAG taskDAG;

    public ScheduleResult(Schedule schedule) {
        this.taskDAG = schedule.getTaskDAG();
        this.ast = schedule.getAST();
        this.aft = schedule.getAFT();
//        this.makespan = schedule.getAFT() - schedule.getAST();
        this.makespan = schedule.getAFT() - schedule.getTaskDAG().getArrivalTime();
        this.noOfSlots = schedule.countSlotsInNetwork();

        if (this.makespan <= this.taskDAG.getDeadline()) {
            this.isAccepted = true;
            this.earlyTime = this.taskDAG.getDeadline() - this.makespan;
        } else {
            this.isAccepted = false;
            this.lateTime = this.makespan - this.taskDAG.getDeadline();
        }

        this.noOfFogNodesUsed = schedule.getNoOfTasksAllocatedToFogNodes();
        this.noOfCloudNodesUsed = schedule.getNoOfTasksAllocatedToCloudNodes();
    }

    public void print() {
        System.out.println("App id " + this.taskDAG.getId());
        if (isAccepted) {
            System.out.println(PrintUtils.ANSI_GREEN_BACKGROUND + "ACCEPTED!!!" + PrintUtils.ANSI_RESET);
        } else {
            System.out.println(PrintUtils.ANSI_RED_BACKGROUND + "REJECTED!!!" + PrintUtils.ANSI_RESET);
        }

        System.out.println("Makespan: " + this.makespan + ", deadline: " + this.taskDAG.getDeadline());
        System.out.println("MakespanHEFT: " + this.taskDAG.getMakespanHEFT());
        System.out.println("Arrives at " + this.taskDAG.getArrivalTime() + ", AST: " + this.ast + ", AFT: " + this.aft);
        System.out.println("CCR: " + this.taskDAG.getCCR() +
                ", noOfClouds: " + this.noOfCloudNodesUsed + ", noOfFogs: " + this.noOfFogNodesUsed);
        if (this.isAccepted) {
            System.out.println("No of slots: " + this.noOfSlots);
        }
        System.out.println("=========================================================");
    }

    public boolean isAccepted() {
        return this.isAccepted;
    }
}
