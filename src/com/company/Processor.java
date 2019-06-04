package com.company;

public class Processor {
    // Mbps: megabit per second
    public static final int BANDWIDTH_FOG_LAN = 1024;
    public static final int BANDWIDTH_CLOUD_LAN = 256;
    private static final int[] BANDWIDTH_WAN = {10, 100, 512, 1024};
    private static final int BANDWIDTH_WAN_LOWER_BOUND = 10;
    private static final int BANDWIDTH_WAN_UPPER_BOUND = 100;

    // MIPS: million instructions per second
    private static final int PROCESSING_RATE_FOG_LOWER_BOUND = 10;
    private static final int PROCESSING_RATE_FOG_UPPER_BOUND = 500;
    private static final int PROCESSING_RATE_CLOUD_LOWER_BOUND = 250;
    private static final int PROCESSING_RATE_CLOUD_UPPER_BOUND = 1500;

    // MB: megabyte
    private static final int[] RAM_FOG = {512, 1024};
    private static final int[] RAM_CLOUD = {2048, 3072, 4096, 6144, 8192};

    // MB: megabyte
    private static final int STORAGE_FOG_LOWER_BOUND = 100;
    private static final int STORAGE_FOG_UPPER_BOUND = 1024;
    private static final int STORAGE_CLOUD_LOWER_BOUND = 8192;
    private static final int STORAGE_CLOUD_UPPER_BOUND = 102400;

    // PROCESSOR TYPES
    private static final int PROCESSOR_TYPE_FOG_1 = 1;
    private static final int PROCESSOR_TYPE_FOG_2 = 1;
    private static final int PROCESSOR_TYPE_FOG_3 = 1;
    private static final int PROCESSOR_TYPE_FOG_4 = 1;
    private static final int PROCESSOR_TYPE_CLOUD_1 = 5;
    private static final int PROCESSOR_TYPE_CLOUD_2 = 6;
    private static final int PROCESSOR_TYPE_CLOUD_3 = 7;
    private static final int PROCESSOR_TYPE_CLOUD_4 = 8;
    private static final int PROCESSOR_TYPE_CLOUD_5 = 9;

    private static final double[] PROCESSING_RATES = {0, 1.1, 1.6, 2.0, 2.4, 2.2, 2.6, 2.6, 3.0, 3.0};
    private static final int[] NUMBER_OF_CORES = {0, 1, 1, 1, 1, 2, 2, 4, 2, 4};
    private static final double[] COST_PER_TIME_UNIT = {0, 0, 0, 0, 0, 1.0, 1.3, 1.5, 1.7, 2.0};

    // properties
    private int id;
    private boolean isFog;
    private double processingRate;
    private int ram;
    private int storage;
    private int wanUploadBandwidth;
    private int wanDownloadBandwidth;
    private int noOfCores;
    private double costPerTimeUnit;

    public Processor(int id) {
        this.id = id;
        this.isFog = false;
        this.processingRate = 0;
        this.ram = 0;
        this.storage = 0;
        this.wanDownloadBandwidth = 0;
        this.wanUploadBandwidth = 0;
        this.noOfCores = 0;
        this.costPerTimeUnit = 0;
    }

    public Processor(int id, boolean isFog, double processingRate, int noOfCores, int ram,
                     int storage, int wanUploadBandwidth, int wanDownloadBandwidth, double costPerTimeUnit) {
        this.id = id;
        this.isFog = isFog;
        this.processingRate = processingRate;
        this.ram = ram;
        this.storage = storage;
        this.wanDownloadBandwidth = wanDownloadBandwidth;
        this.wanUploadBandwidth = wanUploadBandwidth;
        this.noOfCores = noOfCores;
        this.costPerTimeUnit = costPerTimeUnit;
    }

    public Processor(int id, int processorType, int wanUploadBandwidth, int wanDownloadBandwidth) {
        this.id = id;

        if (processorType <= Processor.PROCESSOR_TYPE_FOG_4) {
            this.isFog = true;
        } else {
            this.isFog = false;
        }

        this.noOfCores = Processor.NUMBER_OF_CORES[processorType];
        this.processingRate = Processor.PROCESSING_RATES[processorType];
        this.costPerTimeUnit = Processor.COST_PER_TIME_UNIT[processorType];

        this.wanUploadBandwidth = wanUploadBandwidth;
        this.wanDownloadBandwidth = wanDownloadBandwidth;
    }

    public double getProcessingRate() {
        return this.processingRate;
    }

    public void setProcessingRate(double processingRate) {
        this.processingRate = processingRate;
    }

    public int getWanUploadBandwidth() {
        return this.wanUploadBandwidth;
    }

    public void setWanUploadBandwidth(int wanUploadBandwidth) {
        this.wanUploadBandwidth = wanUploadBandwidth;
    }

    public int getWanDownloadBandwidth() {
        return this.wanDownloadBandwidth;
    }

    public void setWanDownloadBandwidth(int wanDownloadBandwidth) {
        this.wanDownloadBandwidth = wanDownloadBandwidth;
    }

    public int getRAM() {
        return this.ram;
    }

    public void setRAM(int ram) {
        this.ram = ram;
    }

    public int getStorage() {
        return this.storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public boolean isFog() {
        return this.isFog;
    }

    public void setIsFog(boolean isFog) {
        this.isFog = isFog;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoOfCores() {
        return this.noOfCores;
    }

    public void setNoOfCores(int noOfCores) {
        this.noOfCores = noOfCores;
    }

    public double getCostPerTimeUnit() {
        return this.costPerTimeUnit;
    }

    public void setCostPerTimeUnit(double costPerTimeUnit) {
        this.costPerTimeUnit = costPerTimeUnit;
    }
}
