package com.company;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class MainGanttMultiple extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainGanttMultiple(String title, Schedule schedule) {
        super(title);
        SchedulingGanttChart ganttChart = new SchedulingGanttChart(this, schedule);
    }

    public static void main(String[] args) {
        int noOfDAGsToTest = 2;

        ProcessorDAG processorDAG = new ProcessorDAG("dataset/processors.dag");

        TaskDAG taskDAG = new TaskDAG(1, "dataset/1.dag");
        Schedule schedule = Heuristics.HEFT(taskDAG, processorDAG);
        double makespan = schedule.getAFT();

        ScheduleResult scheduleResult = new ScheduleResult(schedule);
        scheduleResult.print();

        int noOfAcceptedRequests = 1;

        for (int id = 2; id < noOfDAGsToTest + 1; id++) {
            taskDAG = new TaskDAG(id, "dataset/" + id + ".dag");

            Schedule tmpSchedule = Heuristics.DynamicHEFT(schedule, taskDAG);
            ScheduleResult tmpScheduleResult = new ScheduleResult(tmpSchedule);

            if (tmpScheduleResult.isAccepted()) {
                schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorCoreExecutionSlots());
                noOfAcceptedRequests += 1;
            }

            tmpScheduleResult.print();
        }

        System.out.println("Accepted " + noOfAcceptedRequests);

        SwingUtilities.invokeLater(() -> {
            MainGanttMultiple example = new MainGanttMultiple("Gantt Chart", schedule);
            example.setSize(800, 400);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}  