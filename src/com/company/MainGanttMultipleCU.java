package com.company;

import javax.swing.*;

public class MainGanttMultipleCU extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainGanttMultipleCU(String title, String chartLabel, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, chartLabel, schedule);
    }

    public static void main(String[] args) {
        int noOfDAGsToTest = GlobalConfig.DATASET_SIZE;

        ProcessorDAG processorDAG = new ProcessorDAG(GlobalConfig.PROCESSORS_PATH);

        TaskDAG taskDAG = new TaskDAG(1, GlobalConfig.DATASET_PATH + "1.dag");
        Schedule schedule = Heuristics.CloudUnaware(taskDAG, processorDAG);
        double makespan = schedule.getAFT();

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        int noOfAcceptedRequests = 1;

        for (int id = 2; id < noOfDAGsToTest + 1; id++) {
            TaskDAG newTaskDAG = new TaskDAG(id, GlobalConfig.DATASET_PATH + id + ".dag");

            Schedule tmpSchedule = Heuristics.DynamicCloudUnaware(schedule, newTaskDAG);

            if (tmpSchedule.getActualStartTimeOfDAG() != taskDAG.getArrivalTime()) {
                System.out.println("Actual start time: " + tmpSchedule.getActualStartTimeOfDAG());
                System.out.println("Actual waiting time: " + tmpSchedule.getActualWaitingTime());
            }

            ScheduleResult tmpScheduleResult = new ScheduleResult(tmpSchedule);
            tmpScheduleResult.print();

            if (tmpScheduleResult.isAccepted()) {
                schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                noOfAcceptedRequests += 1;

                scheduleResult = tmpScheduleResult;
            }
        }

        System.out.println("Accepted " + noOfAcceptedRequests);

        final int GR = noOfAcceptedRequests;
        final double cloudCost = scheduleResult.getCloudCost();

        scheduleResult.printWithResourceUsage();

        System.out.println("Percentage of edge occupancy: " + scheduleResult.getPercentageEdgeOccupancy());

        SwingUtilities.invokeLater(() -> {
            MainGanttMultipleCU example = new MainGanttMultipleCU(
                    "CloudUnaware",
                    "CloudUnaware (Accepted: " + GR + ", Cloud cost: " + cloudCost + ")",
                    schedule);
            example.setSize(800, 400);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
