package com.company;

public class ProcessorCore {
    private Processor processor;
    private int coreId;
    private int schedulePositionId;

    public ProcessorCore(Processor processor, int coreId, int schedulePositionId) {
        this.processor = processor;
        this.coreId = coreId;
        this.schedulePositionId = schedulePositionId;
    }

    public Processor getProcessor() {
        return this.processor;
    }

    public int getCoreId() {
        return this.coreId;
    }

    public int getSchedulePositionId() {
        return this.schedulePositionId;
    }
}
