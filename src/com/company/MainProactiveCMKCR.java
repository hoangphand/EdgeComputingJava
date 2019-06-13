package com.company;

import javax.swing.*;
import java.util.ArrayList;

public class MainProactiveCMKCR extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainProactiveCMKCR(String title, String chartLabel, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, chartLabel, schedule);
    }

    public static void main(String[] args) {
        int noOfDAGsToTest = 100;

        ProcessorDAG processorDAG = new ProcessorDAG(GlobalConfig.PROCESSORS_PATH);

        TaskDAG taskDAG = new TaskDAG(1, GlobalConfig.DATASET_PATH + "1.dag");
        Schedule schedule = Heuristics.CompromiseMKCR(taskDAG, processorDAG);
        double makespan = schedule.getAFT();

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        ArrayList<TaskDAG> listOfTaskDAGs = new ArrayList<TaskDAG>();
        listOfTaskDAGs.add(taskDAG);

        int noOfAcceptedRequests = 1;

        for (int id = 2; id < noOfDAGsToTest + 1; id++) {
            TaskDAG newTaskDAG = new TaskDAG(id, GlobalConfig.DATASET_PATH + id + ".dag");

            Schedule tmpSchedule = Heuristics.DynamicCompromiseMKCR(schedule, newTaskDAG, 0.5);
            tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());

            ScheduleResult tmpScheduleResult = new ScheduleResult(tmpSchedule);
            tmpScheduleResult.print();

            if (tmpScheduleResult.isAccepted()) {
                schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                noOfAcceptedRequests += 1;
                newTaskDAG.setId(noOfAcceptedRequests);

                scheduleResult = tmpScheduleResult;
            } else {
                System.out.println("-------Try with lower beta--------------");
                tmpSchedule = Heuristics.DynamicCompromiseMKCR(schedule, newTaskDAG, 0.2);
                tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());
                tmpScheduleResult = new ScheduleResult(tmpSchedule);
                tmpScheduleResult.print();

                if (tmpScheduleResult.isAccepted()) {
                    schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                    noOfAcceptedRequests += 1;
                    newTaskDAG.setId(noOfAcceptedRequests);

                    scheduleResult = tmpScheduleResult;
                } else {
                    System.out.println("-------Try with HEFT--------------");
                    tmpSchedule = Heuristics.DynamicHEFT(schedule, newTaskDAG);
                    tmpSchedule.setAST(tmpSchedule.getActualStartTimeOfDAG());
                    tmpScheduleResult = new ScheduleResult(tmpSchedule);
                    tmpScheduleResult.print();

                    if (tmpScheduleResult.isAccepted()) {
                        schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                        noOfAcceptedRequests += 1;
                        newTaskDAG.setId(noOfAcceptedRequests);

                        scheduleResult = tmpScheduleResult;
                    }
                }
            }

            listOfTaskDAGs.add(newTaskDAG);
        }

        System.out.println("Accepted " + noOfAcceptedRequests);

        final int GR = noOfAcceptedRequests;
        final double cloudCost = scheduleResult.getCloudCost();

        System.out.println("Percentage of edge occupancy: " + scheduleResult.getPercentageEdgeOccupancy());

        SwingUtilities.invokeLater(() -> {
            MainProactiveCMKCR example = new MainProactiveCMKCR(
                    "ProactiveCMKCR2HEFT",
                    "ProactiveCMKCR2HEFT (Accepted: " + GR + ", Cloud cost: " + cloudCost + ")",
                    schedule);
            example.setSize(800, 400);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}