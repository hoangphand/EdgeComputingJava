package com.company;

import javax.swing.*;

public class MainGanttSingleASFHEFT extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainGanttSingleASFHEFT(String title, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, schedule);
    }

    public static void main(String[] args) {
        ProcessorDAG processorDAG = new ProcessorDAG("dataset-GHz/processors.dag");

        TaskDAG taskDAG = new TaskDAG(1, "dataset-GHz/1.dag");
        Schedule schedule = Heuristics.AStepFurtherHEFTAdjacent(taskDAG, processorDAG);
        double makespan = schedule.getAFT();

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        SwingUtilities.invokeLater(() -> {
            MainGanttSingleASFHEFT example = new MainGanttSingleASFHEFT("Gantt Chart", schedule);
            example.setSize(1200, 700);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}