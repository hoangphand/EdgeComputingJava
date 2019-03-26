package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ProcessorDAG {
    private static final int NO_OF_FOGS_LOWER_BOUND = 15;
    private static final int NO_OF_FOGS_UPPER_BOUND = 15;
    private static final int NO_OF_CLOUDS_LOWER_BOUND = 15;
    private static final int NO_OF_CLOUDS_UPPER_BOUND = 15;

    private int noOfFogs;
    private int noOfClouds;
    private ArrayList<Processor> processors;

    public ProcessorDAG(int noOfFogs, int noOfClouds) {
        this.noOfFogs = noOfFogs;
        this.noOfClouds = noOfClouds;
        this.processors = new ArrayList<Processor>();
    }

    public ProcessorDAG(String inputFilePath) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(inputFilePath));

            this.noOfFogs = Integer.parseInt(reader.readLine());
            this.noOfClouds = Integer.parseInt(reader.readLine());

            this.processors = new ArrayList<Processor>();

            reader.readLine();

            for (int i = 0; i < this.noOfFogs + this.noOfClouds; i++) {
                String[] rowDetails = reader.readLine().trim().split("\\s+");

                boolean isFog = Boolean.parseBoolean(rowDetails[1]);

                Processor newProcessor = new Processor(i, isFog);
                newProcessor.setProcessingRate(Integer.parseInt(rowDetails[2]));
                newProcessor.setRAM(Integer.parseInt(rowDetails[3]));
                newProcessor.setStorage(Integer.parseInt(rowDetails[4]));
                newProcessor.setWanUploadBandwidth(Integer.parseInt(rowDetails[5]));
                newProcessor.setWanDownloadBandwidth(Integer.parseInt(rowDetails[6]));

                this.processors.add(newProcessor);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getNoOfFogs() {
        return this.noOfFogs;
    }

    public int getNoOfClouds() {
        return this.noOfClouds;
    }

    public ArrayList<Processor> getProcessors() {
        return this.processors;
    }

    public int getTotalProcessingRate() {
        int totalProcessingRate = 0;

        for (int i = 0; i < this.processors.size(); i++) {
            totalProcessingRate += this.processors.get(i).getProcessingRate();
        }

        return totalProcessingRate;
    }

    public double getAvgProcessingRate() {
        return this.getTotalProcessingRate() / (this.noOfClouds + this.noOfFogs);
    }

    public int getTotalUploadBandwidth() {
        int totalUploadBandwidth = 0;

        for (int i = 0; i < this.processors.size(); i++) {
            totalUploadBandwidth += this.processors.get(i).getWanUploadBandwidth();
        }

        return totalUploadBandwidth;
    }

    public int getTotalDownloadBandwidth() {
        int totalDownloadBandwidth = 0;

        for (int i = 0; i < this.processors.size(); i++) {
            totalDownloadBandwidth += this.processors.get(i).getWanDownloadBandwidth();
        }

        return totalDownloadBandwidth;
    }

    public double getAvgUploadBandwidth() {
        return this.getTotalUploadBandwidth() / (this.noOfFogs + this.noOfClouds);
    }

    public double getAvgDownloadBandwidth() {
        return this.getTotalDownloadBandwidth() / (this.noOfFogs + this.noOfClouds);
    }

    public double getAvgBandwidth() {
        return (this.getTotalUploadBandwidth() + this.getTotalDownloadBandwidth()) / (2 * (this.noOfFogs + this.noOfClouds));
    }

    public int getBandwidthToUse(Processor fromProcessor, Processor toProcessor) {
        int bandwidthToUse;

        if (fromProcessor.isFog() && toProcessor.isFog()) {
            bandwidthToUse = Processor.BANDWIDTH_LAN;
        } else {
            bandwidthToUse = Math.min(fromProcessor.getWanUploadBandwidth(), toProcessor.getWanDownloadBandwidth());
        }

        return bandwidthToUse;
    }

    public Processor getTheMostPowerfulProcessor() {
        Processor theMostPowerfulProcesor = this.processors.get(0);

        for (int i = 0; i < this.processors.size(); i++) {
            if (this.processors.get(i).getProcessingRate() > theMostPowerfulProcesor.getProcessingRate()) {
                theMostPowerfulProcesor = this.processors.get(i);
            }
        }

        return theMostPowerfulProcesor;
    }

    public double getCommunicationTime(Processor fromProcessor, Processor toProcessor, double amountOfData) {
        if (fromProcessor.getId() == toProcessor.getId()) {
            return 0;
        } else {
            return amountOfData / this.getBandwidthToUse(fromProcessor, toProcessor);
        }
    }
}
