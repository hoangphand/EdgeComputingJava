package com.company;

import javax.swing.*;

public class MainGanttSingleCompromiseMKCR extends JFrame {
    public MainGanttSingleCompromiseMKCR(String title, String chartLabel, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, chartLabel, schedule);
    }

    public static void main(String[] args) {
        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/processors.dag");

        int taskId = 1;
//
//        while (taskId <= 100) {

//            while (beta <= 1) {
                TaskDAG taskDAG = new TaskDAG(taskId, "dataset-GHz/" + taskId + ".dag");
                Schedule schedule = Heuristics.CompromiseMKCR(taskDAG, processorDAG);
                double makespan = schedule.getAFT();

                ScheduleResult scheduleResult = new ScheduleResult(schedule);
//            scheduleResult.print();

                if (scheduleResult.isAccepted()) {
                    System.out.println(PrintUtils.ANSI_GREEN_BACKGROUND + "ACCEPTED!!!" + PrintUtils.ANSI_RESET +
                            " Makespan: " + scheduleResult.getMakespan() + ", deadline: " + taskDAG.getDeadline() +
                            ", cloud cost: " + scheduleResult.getCloudCost());
                } else {
                    System.out.println(PrintUtils.ANSI_RED_BACKGROUND + "REJECTED!!!" + PrintUtils.ANSI_RESET +
                            " Makespan: " + scheduleResult.getMakespan() + ", cloud cost: " + scheduleResult.getCloudCost());
                }
                System.out.println("-------------------");
//            }

//            taskId += 1;
//        }





        SwingUtilities.invokeLater(() -> {
            MainGanttSingleCompromiseMKCR example = new MainGanttSingleCompromiseMKCR(
                    "CompromiseMKCR",
                    "CompromiseMKCR",
                    schedule);
            example.setSize(1200, 700);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
