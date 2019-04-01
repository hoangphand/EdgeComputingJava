package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TaskDAG {
    public static final int MIN_NO_OF_LAYERS = 5;
    public static final int MIN_NO_OF_NODES_PER_LAYER = 1;
    public static final int MIN_OUT_DEGREE = 1;

    // properties
    private int id;
    private double alpha;
    private int height;
    private int width;
    private ArrayList<Task> tasks;
    private ArrayList<ArrayList<Task>> layers;
    private double makespanHEFT;
    private double k;
    private double deadline;
    private double arrivalTime;
    private ProcessorDAG processorDAG;
    private double ccr;

    public TaskDAG(int id, int noOfTasks, double alpha, ProcessorDAG processorDAG, double ccr) {
        this.id = id;
        this.alpha = alpha;

        int minNoOfLayers = MIN_NO_OF_LAYERS;
        int maxNoOfLayers = (int) Math.round(Math.sqrt(noOfTasks) / alpha * 2 - minNoOfLayers);

        this.height = RandomUtils.getRandomIntInRange(minNoOfLayers, maxNoOfLayers);

        int minNoOfNodesPerLayer = MIN_NO_OF_NODES_PER_LAYER;
        int maxNoOfNodesPerLayer = (int) Math.round(Math.sqrt(noOfTasks) * alpha * 2 - minNoOfNodesPerLayer);

        for (int i = 0; i < noOfTasks + 2; i++) {
            this.tasks.add(new Task(i));
//            this.tasks.add(new Task(i, 0, 0, 0, 0));
        }

        this.layers.add(new ArrayList<Task>(Arrays.asList(this.tasks.get(0))));

        int currentNodeId = 1;

        for (int layerId = 1; layerId < this.height; layerId++) {
            int layerWidth = RandomUtils.getRandomIntInRange(minNoOfNodesPerLayer, maxNoOfNodesPerLayer);
            ArrayList<Task> newLayer = new ArrayList<Task>();

            for (int j = 0; j < layerWidth; j++) {
                newLayer.add(this.tasks.get(currentNodeId));

                this.tasks.get(currentNodeId).setLayerId(layerId);

                currentNodeId += 1;
                if (currentNodeId == noOfTasks) {
                    break;
                }
            }

            this.layers.add(newLayer);

            if (currentNodeId == noOfTasks) {
                break;
            }
        }

        ArrayList<Task> newLayer = new ArrayList<Task>();
        for (int i = currentNodeId; i < noOfTasks + 1; i++) {
            newLayer.add(this.tasks.get(currentNodeId));

            this.tasks.get(i).setLayerId(this.height);

            currentNodeId += 1;
        }
        this.layers.add(newLayer);

        this.layers.add(new ArrayList<Task>(Arrays.asList(this.tasks.get(noOfTasks + 1))));
        this.tasks.get(noOfTasks + 1).setLayerId(this.height + 1);

        this.ccr = ccr;
        this.processorDAG = processorDAG;

        for (int layerId = 1; layerId < this.layers.size() - 2; layerId++) {
            ArrayList<Task> possibleDestinationNodes = new ArrayList<Task>();

            for (int j = layerId + 1; j < this.layers.size() - 1; j++) {
                possibleDestinationNodes.addAll(this.layers.get(j));
            }

            for (int layerNodeId = 0; layerNodeId < this.layers.size(); layerNodeId++) {
                int outDegree = RandomUtils.getRandomIntInRange(MIN_OUT_DEGREE, possibleDestinationNodes.size());

                Collections.shuffle(possibleDestinationNodes);

                for (int k = 0; k < outDegree; k++) {
                    this.layers.get(layerNodeId).get(layerNodeId).addEdgeRandomConstraint(possibleDestinationNodes.get(k),
                            this.ccr, this.processorDAG);
                }
            }
        }

        for (int i = 1; i < noOfTasks + 1; i++) {
            if (this.tasks.get(i).getPredecessors().size() == 0) {
                this.tasks.get(0).addEdge(this.tasks.get(i), 0);
            }

            if (this.tasks.get(i).getSuccessors().size() == 0) {
                this.tasks.get(i).addEdge(this.tasks.get(noOfTasks + 1), 0);
            }
        }

        this.height = this.layers.size();
    }

    public TaskDAG(int id, String inputFilePath) {
        this.id = id;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(inputFilePath));

            int noOfTasks = Integer.parseInt(reader.readLine());
            this.tasks = new ArrayList<Task>();
            for (int i = 0; i < noOfTasks + 2; i++) {
                this.tasks.add(new Task(i));
            }

            reader.readLine();
            reader.readLine();

            for (int i = 1; i < noOfTasks + 2; i++) {
                String[] rowDetails = reader.readLine().trim().split("\\s+");

                Task currentTask = this.tasks.get(Integer.parseInt(rowDetails[0]));
                currentTask.setComputationRequired(Double.parseDouble(rowDetails[1]));
                currentTask.setStorageRequired(Double.parseDouble(rowDetails[2]));
                currentTask.setMemoryRequired(Double.parseDouble(rowDetails[3]));

                int noOfPredecessors = Integer.parseInt(rowDetails[4]);

                for (int j = 0; j < noOfPredecessors; j++) {
                    String[] predecessorDetails = reader.readLine().trim().split("\\s+");

                    Task currentPrecedence = this.tasks.get(Integer.parseInt(predecessorDetails[0]));
                    currentPrecedence.addEdge(currentTask, Double.parseDouble(predecessorDetails[1]));
                }
            }

            this.makespanHEFT = Double.parseDouble(reader.readLine().trim().split("\\s+")[1]);
            this.k = Double.parseDouble(reader.readLine().trim().split("\\s+")[1]);
            this.deadline = Double.parseDouble(reader.readLine().trim().split("\\s+")[1]);
            this.arrivalTime = Double.parseDouble(reader.readLine().trim().split("\\s+")[1]);
            this.ccr = Double.parseDouble(reader.readLine().trim().split("\\s+")[1]);
            this.alpha = Double.parseDouble(reader.readLine().trim().split("\\s+")[1]);
            this.height = Integer.parseInt(reader.readLine().trim().split("\\s+")[1]);

            this.layers = new ArrayList<ArrayList<Task>>();
            for (int layerId = 0; layerId < this.height; layerId++) {
                ArrayList<Task> newLayer = new ArrayList<Task>();

                String[] rowDetails = reader.readLine().trim().split("\\s+");

                for (int j = 0; j < rowDetails.length; j++) {
                    newLayer.add(this.tasks.get(Integer.parseInt(rowDetails[j])));
                    this.tasks.get(Integer.parseInt(rowDetails[j])).setLayerId(layerId);
                }

                this.layers.add(newLayer);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public ArrayList<ArrayList<Task>> getLayers() {
        return this.layers;
    }

    public int getId() {
        return this.id;
    }

    public double getDeadline() {
        return this.deadline;
    }

    public double getArrivalTime() {
        return this.arrivalTime;
    }

    public double getMakespanHEFT() { return this.makespanHEFT; }

    public double getCCR() { return this.ccr; }

    public ProcessorDAG getProcessorDAG() {
        return this.processorDAG;
    }

    public int getTotalLinks() {
        int noOfLinks = 0;

        for (int i = 0; i < this.tasks.size(); i++) {
            for (int j = 0; j < this.tasks.get(i).getPredecessors().size(); j++) {
                if (this.tasks.get(i).getPredecessors().get(j).getDataConstraint() != 0) {
                    noOfLinks += 1;
                }
            }
        }

        return noOfLinks;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void exportDAG(String outputFilePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

            int noOfTasks = (this.tasks.size() - 2);

            writer.write(String.valueOf(noOfTasks));
            writer.newLine();

            int maxIdWidth = "id".length();
            int maxComputationWidth = "computation".length();
            int maxStorageWidth = "storage".length();
            int maxMemoryWidth = "memory".length();
            int maxPredWidth = "no_of_preds".length();

            int maxWidthPred = maxIdWidth + maxComputationWidth + maxMemoryWidth + maxPredWidth + 6 * "\t".length();

            String header = String.format("%-" + maxIdWidth + "s\t\t%-" +
                            maxComputationWidth + "s\t\t%-" +
                            maxStorageWidth + "s\t\t%-" +
                            maxMemoryWidth + "s\t\t%-" +
                            maxPredWidth + "s",
                    "id", "computation", "storage", "memory", "no_of_preds");
            writer.write(header);
            writer.newLine();

            for (int index = 0; index < this.tasks.size(); index++) {
                writer.write(String.format("%-" + maxIdWidth + "s", index));
                writer.write("\t\t");
                writer.write(String.format("%-" + maxComputationWidth + "s", this.tasks.get(index).getComputationRequired()));
                writer.write("\t\t");
                writer.write(String.format("%-" + maxStorageWidth + "s", this.tasks.get(index).getStorageRequired()));
                writer.write("\t\t");
                writer.write(String.format("%-" + maxMemoryWidth + "s", this.tasks.get(index).getMemoryRequired()));
                writer.write("\t\t");
                writer.write(String.format("%-" + maxPredWidth + "s", this.tasks.get(index).getPredecessors().size()));
                writer.newLine();

                for (int i = 0; i < this.tasks.get(index).getPredecessors().size(); i++) {
//                    if (this.tasks.get(index).getPredecessors().get(i).)
                    writer.write(String.format("%-" + maxWidthPred + "s\t\t\t\t\t" +
                            this.tasks.get(index).getPredecessors().get(i).getTask().getId() + "\t\t" +
                            this.tasks.get(index).getPredecessors().get(i).getDataConstraint(), " "));
                    writer.newLine();
                }
            }

            writer.write("makespanHEFT: " + this.makespanHEFT);
            writer.newLine();
            writer.write("k: " + this.k);
            writer.newLine();
            writer.write("deadline: " + this.deadline);
            writer.newLine();
            writer.write("arrivalTime: " + this.arrivalTime);
            writer.newLine();
            writer.write("ccr: " + this.ccr);
            writer.newLine();
            writer.write("alpha: " + this.alpha);
            writer.newLine();
            writer.write("height: " + this.height);
            writer.newLine();

            for (int i = 0; i < this.layers.size(); i++) {
                for (int j = 0; j < this.layers.get(i).size(); j++) {
                    writer.write(this.layers.get(i).get(j).getId() + " ");
                }

                writer.newLine();
            }

            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
