package com.company;

public class Processor {
    // Mbps: megabit per second
    public static final int BANDWIDTH_LAN = 1024;
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

    // properties
    private int id;
    private boolean isFog;
    private int processingRate;
    private int ram;
    private int storage;
    private int wanUploadBandwidth;
    private int wanDownloadBandwidth;

    public Processor(int id, boolean isFog, int processingRate, int ram, int storage, int wanUploadBandwidth, int wanDownloadBandwidth) {
        this.id = id;
        this.isFog = isFog;
        this.processingRate = processingRate;
        this.ram = ram;
        this.storage = storage;
        this.wanDownloadBandwidth = wanDownloadBandwidth;
        this.wanUploadBandwidth = wanUploadBandwidth;
    }

    public Processor(int id, boolean isFog) {
        this.id = id;
        this.isFog = isFog;

        if (this.isFog) {
            this.processingRate = RandomUtils.getRandomIntInRange(PROCESSING_RATE_FOG_LOWER_BOUND, PROCESSING_RATE_FOG_UPPER_BOUND);
            this.ram = RandomUtils.getRandomElement(RAM_FOG);
            this.storage = RandomUtils.getRandomIntInRange(STORAGE_FOG_LOWER_BOUND, STORAGE_FOG_UPPER_BOUND);
        } else {
            this.processingRate = RandomUtils.getRandomIntInRange(PROCESSING_RATE_CLOUD_LOWER_BOUND, PROCESSING_RATE_CLOUD_UPPER_BOUND);
            this.ram = RandomUtils.getRandomElement(RAM_CLOUD);
            this.storage = RandomUtils.getRandomIntInRange(STORAGE_CLOUD_LOWER_BOUND, STORAGE_CLOUD_UPPER_BOUND);
        }

        this.wanUploadBandwidth = RandomUtils.getRandomIntInRange(BANDWIDTH_WAN_LOWER_BOUND, BANDWIDTH_WAN_UPPER_BOUND);
        this.wanDownloadBandwidth = RandomUtils.getRandomIntInRange(BANDWIDTH_WAN_LOWER_BOUND, BANDWIDTH_WAN_UPPER_BOUND);
    }

    public int getProcessingRate() {
        return this.processingRate;
    }

    public void setProcessingRate(int processingRate) {
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
