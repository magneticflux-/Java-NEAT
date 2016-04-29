package org.javaneat.phenome;

import org.apache.commons.math3.util.FastMath;
import org.javaneat.evolution.NEATGenomeManager;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronGene;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NEATPhenome {
    private final NEATGenomeManager manager;
    private final List<NEATConnection> connectionList;
    private final double[] preActivation;
    private final double[] postActivation;

    public NEATPhenome(NEATGenome genome) {
        this.preActivation = new double[genome.getNeuronGeneList().size()];
        this.postActivation = new double[genome.getNeuronGeneList().size()];
        this.manager = genome.getManager();

        genome.sortGenes();

        HashMap<Integer, Integer> neuronIDToArrayIndex = new HashMap<>(genome.getNeuronGeneList().size());
        for (NeuronGene gene : genome.getNeuronGeneList()) {
            neuronIDToArrayIndex.put(gene.getNeuronID(), neuronIDToArrayIndex.size());
        }

        connectionList = genome.getConnectionGeneList().stream()
                .filter(ConnectionGene::getEnabled).map(
                        connectionGene -> new NEATConnection(neuronIDToArrayIndex.get(connectionGene.getToNode()), neuronIDToArrayIndex.get(connectionGene.getFromNode()), connectionGene.getWeight()))
                .collect(Collectors.toList());
    }

    public void resetInternalState() {
        assert this.preActivation.length == this.postActivation.length : "Irregular neuron arrays. Malformed phenome.";
        Arrays.fill(this.preActivation, 0);
        Arrays.fill(this.postActivation, 0);
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

        // Adding up all connections
        //noinspection NestedAssignment
        this.connectionList.stream()
                .forEach(neatConnection -> NEATPhenome.this.preActivation[neatConnection.getToIndex()] += NEATPhenome.this.postActivation[neatConnection.getFromIndex()] * neatConnection.getWeight());

        IntStream.range(0, this.preActivation.length)
                .forEach(i ->
                {
                    this.postActivation[i] = NEATPhenome.activationFunction(this.preActivation[i]);
                    this.preActivation[i] = 0;
                });

        double[] output = new double[this.manager.getNumOutputs()];
        System.arraycopy(this.postActivation, this.manager.getOutputOffset(), output, 0, this.manager.getNumOutputs()); // Getting outputs

        return output;
    }

    private static double activationFunction(double x) {
        return FastMath.tanh(x);
    }
}
