package com.javaneat.phenome;

import com.javaneat.evolution.NEATGenomeManager;
import com.javaneat.genome.ConnectionGene;
import com.javaneat.genome.NEATGenome;
import com.javaneat.genome.NeuronGene;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NEATPhenome {
    private NEATGenomeManager manager;
    private List<NEATConnection> connectionList;
    private double[] preActivation;
    private double[] postActivation;

    public NEATPhenome(NEATGenome genome) {
        this.connectionList = new ArrayList<>(genome.getConnectionGeneList().size());
        this.preActivation = new double[genome.getNeuronGeneList().size()];
        this.postActivation = new double[genome.getNeuronGeneList().size()];
        this.manager = genome.getManager();

        genome.sortGenes();

        HashMap<Integer, Integer> neuronIDToArrayIndex = new HashMap<>(genome.getNeuronGeneList().size());
        for (NeuronGene gene : genome.getNeuronGeneList()) {
            neuronIDToArrayIndex.put(gene.getNeuronID(), neuronIDToArrayIndex.size());
        }

        for (ConnectionGene gene : genome.getConnectionGeneList()) {
            if (gene.getEnabled()) {
                NEATConnection connection = new NEATConnection(neuronIDToArrayIndex.get(gene.getToNode()), neuronIDToArrayIndex.get(gene.getFromNode()),
                        gene.getWeight());
                this.connectionList.add(connection);
            }
        }
    }

    public static double activationFunction(double x) {
        return FastMath.tanh(x);
    }

    public void resetInternalState() {
        assert this.preActivation.length == this.postActivation.length : "Irregular neuron arrays. Malformed phenome.";
        for (int i = 0; i < this.preActivation.length; i++) {
            this.preActivation[i] = 0;
            this.postActivation[i] = 0;
        }
    }

    public double[] stepTime(double[] inputs, int numActivations) {
        for (int i = 0; i < numActivations - 1; i++) {
            this.stepTime(inputs);
        }
        return this.stepTime(inputs);
    }

    public double[] stepTime(double[] inputs) {
        if (inputs.length != this.manager.getNumInputs())
            throw new IllegalArgumentException("Input length not correct.");

        this.postActivation[0] = 1;
        System.arraycopy(inputs, 0, this.postActivation, this.manager.getInputOffset(), this.manager.getNumInputs()); // Setting bias and inputs

        for (NEATConnection connection : this.connectionList) // Adding up all connections
        {
            this.preActivation[connection.getToIndex()] += this.postActivation[connection.getFromIndex()] * connection.getWeight();
        }

        for (int i = 0; i < this.preActivation.length; i++) {
            this.postActivation[i] = NEATPhenome.activationFunction(this.preActivation[i]);
            this.preActivation[i] = 0;
        }

        double[] output = new double[this.manager.getNumOutputs()];
        System.arraycopy(this.postActivation, this.manager.getOutputOffset(), output, 0, this.manager.getNumOutputs()); // Getting outputs

        return output;
    }
}
