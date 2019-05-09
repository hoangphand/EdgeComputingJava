package com.company;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SchedulingGanttChart {
    public SchedulingGanttChart(JFrame frame, Schedule schedule) {
        TaskSeriesCollection dataset = this.getCategoryDataset(schedule);
        JFreeChart chart = ChartFactory.createXYBarChart(
                "Task scheduling for app " + schedule.getTaskDAG().getId(),
                "Resource", false, "Timing", new XYTaskDataset(dataset),
                PlotOrientation.HORIZONTAL,
                true, false, false);

        chart.setBackgroundPaint(Color.white);

        ChartUtilities.applyCurrentTheme(chart);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangePannable(true);

        String[] resourceNames = new String[schedule.getProcessorCoreExecutionSlots().size()];

        for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
            ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
            String processorName = "";

            if (currentProcessorCore.getProcessor().isFog()) {
                processorName = "Fog " + currentProcessorCore.getProcessor().getId();
            } else {
                processorName = "Cloud " + currentProcessorCore.getProcessor().getId() + " core " + currentProcessorCore.getCoreId();
            }

            resourceNames[i] = processorName;
        }
        SymbolAxis xAxis = new SymbolAxis("Resources", resourceNames);
        xAxis.setGridBandsVisible(false);
        plot.setDomainAxis(xAxis);

        DateAxis range = new DateAxis("Time");
        range.setDateFormatOverride(new SimpleDateFormat("m.ss.SSS"));
        plot.setRangeAxis(range);
        MyRenderer renderer = new MyRenderer(dataset);
        renderer.setUseYInterval(true);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        plot.setRenderer(renderer);
//        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());

        ChartPanel panel = new ChartPanel(chart);
        frame.setContentPane(panel);
    }

    private TaskSeriesCollection getCategoryDataset(Schedule schedule) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();

        for (int i = 0; i < schedule.getProcessorCoreExecutionSlots().size(); i++) {
            ProcessorCore currentProcessorCore = schedule.getProcessorCoreExecutionSlots().get(i).get(0).getProcessorCore();
            String processorName = "";

            if (currentProcessorCore.getProcessor().isFog()) {
                processorName = "Fog " + currentProcessorCore.getProcessor().getId();
            } else {
                processorName = "Cloud " + currentProcessorCore.getProcessor().getId() + " core " + currentProcessorCore.getCoreId();
            }

            TaskSeries taskSeries = new TaskSeries(processorName);

            for (int j = 0; j < schedule.getProcessorCoreExecutionSlots().get(i).size(); j++) {
                Slot currentSlot = schedule.getProcessorCoreExecutionSlots().get(i).get(j);

                if (currentSlot.getTask() != null) {
                    long startTime = (new Double(currentSlot.getStartTime() * 1000)).longValue();
                    long endTime = (new Double(currentSlot.getEndTime() * 1000)).longValue();

                    taskSeries.add(new Task(String.valueOf(currentSlot.getTask().getTaskDAGId()),
                            new Date(startTime), new Date(endTime)));
                }
            }

            dataset.add(taskSeries);
        }

        return dataset;
    }
}

class MyRenderer extends XYBarRenderer {
    private final TaskSeriesCollection model;

    private List<Color> listOfColors = new ArrayList<Color>(List.of(
            Color.BLACK,
            Color.BLUE,
            Color.CYAN,
//            Color.DARK_GRAY,
            Color.GREEN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.WHITE,
            Color.RED,
            Color.YELLOW,
            Color.PINK
    ));

    public MyRenderer(TaskSeriesCollection model) {
        this.model = model;
    }

    @Override
    public Paint getItemPaint(int row, int col) {
        TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
        Task task = series.get(col);

        int taskDAGId = Integer.parseInt(task.getDescription());

        return listOfColors.get(taskDAGId % (listOfColors.size()));
    }
}
