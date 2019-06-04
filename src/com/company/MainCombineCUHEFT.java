package com.company;

import javax.swing.*;

public class MainCombineCUHEFT extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainCombineCUHEFT(String title, String chartLabel, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, chartLabel, schedule);
    }

    public static void main(String[] args) {
        int noOfDAGsToTest = 100;

        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/processors.dag");

        TaskDAG taskDAG = new TaskDAG(1, "dataset-GHz/1.dag");
        Schedule schedule = new Schedule(taskDAG, processorDAG);
        Schedule scheduleHEFT = Heuristics.HEFT(taskDAG, processorDAG);
        Schedule scheduleCU = Heuristics.CloudUnaware(taskDAG, processorDAG);

        if (scheduleCU.getAFT() > scheduleHEFT.getAFT()) {
            schedule.deepCopy(scheduleHEFT);
        } else {
            schedule.deepCopy(scheduleCU);
        }

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        int noOfAcceptedRequests = 1;

        for (int id = 2; id < noOfDAGsToTest + 1; id++) {
            taskDAG = new TaskDAG(id, "dataset-GHz/" + id + ".dag");
            Schedule tmpScheduleCU = Heuristics.DynamicCloudUnaware(schedule, taskDAG);
            Schedule tmpScheduleHEFT = Heuristics.DynamicHEFT(schedule, taskDAG);

            Schedule tmpSchedule = new Schedule(taskDAG, processorDAG);

            if (tmpScheduleCU.getAFT() > tmpScheduleHEFT.getAFT()) {
                tmpSchedule.deepCopy(tmpScheduleHEFT);
            } else {
                tmpSchedule.deepCopy(tmpScheduleCU);
            }

            ScheduleResult tmpScheduleResult = new ScheduleResult(tmpSchedule);

            if (tmpSchedule.getActualStartTimeOfDAG() != taskDAG.getArrivalTime()) {
                System.out.println("Actual start time: " + tmpSchedule.getActualStartTimeOfDAG());
                System.out.println("Actual waiting time: " + tmpSchedule.getActualWaitingTime());
            }

            if (tmpScheduleResult.isAccepted()) {
                schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                noOfAcceptedRequests += 1;

                scheduleResult = tmpScheduleResult;
            }

            tmpScheduleResult.print();
        }

        System.out.println("Accepted " + noOfAcceptedRequests);

        final int GR = noOfAcceptedRequests;
        final double cloudCost = scheduleResult.getCloudCost();

        final Schedule finalSchedule = new Schedule(taskDAG, processorDAG);
        finalSchedule.setProcessorExecutionSlots(schedule.getProcessorCoreExecutionSlots());

        SwingUtilities.invokeLater(() -> {
            MainCombineCUHEFT example = new MainCombineCUHEFT(
                    "CombineCUHEFT",
                    "CombineCUHEFT (Accepted: " + GR + ", Cloud cost: " + cloudCost + ")",
                    finalSchedule);
            example.setSize(800, 400);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
