package com.company;

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

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SchedulingGanttChart {
    public SchedulingGanttChart(JFrame frame, Schedule schedule) {
        JFreeChart chart = ChartFactory.createXYBarChart(
                "Task scheduling for app " + schedule.getTaskDAG().getId(),
                "Resource", false, "Timing", this.getCategoryDataset(schedule),
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
        frame.setContentPane(panel);
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
}
