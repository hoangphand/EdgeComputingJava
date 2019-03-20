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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class MainGanttChart extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainGanttChart(String title, Schedule schedule) {
        super(title);
        JFreeChart chart = ChartFactory.createXYBarChart(
                "Task scheduling for app " + schedule.getTaskDAG().getId(),
                "Resource", false, "Timing", getCategoryDataset(schedule),
                PlotOrientation.HORIZONTAL,
                true, false, false);

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangePannable(true);

        String[] resourceNames = new String[schedule.getProcessorDAG().getProcessors().size()];

        for (int i = 0; i < schedule.getProcessorDAG().getProcessors().size(); i++) {
            Processor currentProcessor = schedule.getProcessorDAG().getProcessors().get(i);
            String processorName = "";

            if (currentProcessor.isFog()) {
                processorName = "Fog " + currentProcessor.getId();
            } else {
                processorName = "Cloud " + currentProcessor.getId();
            }

            resourceNames[i] = processorName;
        }
        SymbolAxis xAxis = new SymbolAxis("Resources", resourceNames);
        xAxis.setGridBandsVisible(false);
        plot.setDomainAxis(xAxis);

        DateAxis range = new DateAxis("Time");
        range.setDateFormatOverride(new SimpleDateFormat("ss.SSS"));
        plot.setRangeAxis(range);
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setUseYInterval(true);
        ChartUtilities.applyCurrentTheme(chart);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private IntervalXYDataset getCategoryDataset(Schedule schedule) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();

        for (int i = 0; i < schedule.getProcessorDAG().getProcessors().size(); i++) {
            Processor currentProcessor = schedule.getProcessorDAG().getProcessors().get(i);
            String processorName = "";

            if (currentProcessor.isFog()) {
                processorName = "Fog " + currentProcessor.getId();
            } else {
                processorName = "Cloud " + currentProcessor.getId();
            }

            TaskSeries taskSeries = new TaskSeries(processorName);

            for (int j = 0; j < schedule.getProcessorExecutionSlots().get(i).size(); j++) {
                Slot currentSlot = schedule.getProcessorExecutionSlots().get(i).get(j);

                if (currentSlot.getTask() != null) {
                    long startTime = (new Double(currentSlot.getStartTime() * 1000)).longValue();
                    long endTime = (new Double(currentSlot.getEndTime() * 1000)).longValue();

                    taskSeries.add(new Task("Task " + currentSlot.getTask().getId(), new Date(startTime), new Date(endTime)));
                }
            }

            dataset.add(taskSeries);
        }

        return new XYTaskDataset(dataset);
    }

    public static void main(String[] args) {
        int noOfDAGsToTest = 20;

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
                schedule.setProcessorExecutionSlots(tmpSchedule.getProcessorExecutionSlots());
                noOfAcceptedRequests += 1;
            }

            tmpScheduleResult.print();
        }

        System.out.println("Accepted " + noOfAcceptedRequests);

        SwingUtilities.invokeLater(() -> {
            MainGanttChart example = new MainGanttChart("Gantt Chart", schedule);
            example.setSize(800, 400);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}  