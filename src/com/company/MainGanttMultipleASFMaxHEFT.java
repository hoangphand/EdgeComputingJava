package com.company;

import javax.swing.*;

public class MainGanttMultipleASFMaxHEFT extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainGanttMultipleASFMaxHEFT(String title, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, schedule);
    }

    public static void main(String[] args) {
        int noOfDAGsToTest = 100;

        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/processors.dag");

        TaskDAG taskDAG = new TaskDAG(1, "dataset-GHz/1.dag");
        Schedule schedule = Heuristics.AStepFurtherHEFTAdjacentMax(taskDAG, processorDAG);
//        Schedule schedule = Heuristics.AStepFurtherHEFTAdjacentAvg(taskDAG, processorDAG);

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        int noOfAcceptedRequests = 1;

        for (int id = 2; id < noOfDAGsToTest + 1; id++) {
            taskDAG = new TaskDAG(id, "dataset-GHz/" + id + ".dag");

            Schedule tmpSchedule = Heuristics.DynamicASFHEFTAdjacentMax(schedule, taskDAG);
//            Schedule tmpSchedule = Heuristics.DynamicASFHEFTAdjacentAvg(schedule, taskDAG);

            if (tmpSchedule.getActualStartTimeOfDAG() != taskDAG.getArrivalTime()) {
                System.out.println("Actual start time: " + tmpSchedule.getActualStartTimeOfDAG());
                System.out.println("Actual waiting time: " + tmpSchedule.getActualWaitingTime());
            }

            ScheduleResult tmpScheduleResult = new ScheduleResult(tmpSchedule);

            if (tmpScheduleResult.isAccepted()) {
                schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                noOfAcceptedRequests += 1;
            }

            tmpScheduleResult.print();
        }

        System.out.println("Accepted " + noOfAcceptedRequests);

        SwingUtilities.invokeLater(() -> {
            MainGanttMultipleASFMaxHEFT example = new MainGanttMultipleASFMaxHEFT("Gantt Chart", schedule);
            example.setSize(1200, 800);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}