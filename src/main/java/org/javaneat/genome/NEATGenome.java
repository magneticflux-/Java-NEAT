package org.javaneat.genome;

import org.javaneat.evolution.NEATInnovationMap;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NEATGenome implements Serializable
// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
{
    private final List<ConnectionGene> connectionGeneList;
    private final List<NeuronGene> neuronGeneList;
    private final int numInputs;
    private final int numOutputs;

    public NEATGenome(final NEATGenome other) {
        this.connectionGeneList = other.connectionGeneList.stream().map(ConnectionGene::new).collect(Collectors.toList());
        this.neuronGeneList = other.neuronGeneList.stream().map(NeuronGene::new).collect(Collectors.toList());
        this.numInputs = other.numInputs;
        this.numOutputs = other.numOutputs;
    }

    public NEATGenome(final List<ConnectionGene> connections, final List<NeuronGene> neurons, int numInputs, int numOutputs, NEATInnovationMap neatInnovationMap) {
        this.connectionGeneList = new LinkedList<>(connections);
        this.neuronGeneList = new LinkedList<>(neurons);
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
    }

    @SuppressWarnings("unused")
    private NEATGenome() // This is to serialize properly
    {
        this.connectionGeneList = null;
        this.neuronGeneList = null;
        this.numInputs = -1;
        this.numOutputs = -1;
    }

    public NEATGenome(Random rng, int numInputs, int numOutputs, NEATInnovationMap neatInnovationMap) {
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;

        this.connectionGeneList = new LinkedList<>(); //new ArrayList<>(1);
        this.neuronGeneList = new LinkedList<>(); //new ArrayList<>(numInputs + numOutputs + 1);

        this.addInitialNodes(neatInnovationMap);
        this.addRandomFirstLink(rng, neatInnovationMap);
        this.sortGenes();
    }

    private void addInitialNodes(NEATInnovationMap neatInnovationMap) {
        this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), neatInnovationMap.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(), NeuronType.BIAS));
        for (int i = 0; i < numInputs; i++)
            this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), neatInnovationMap.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(), NeuronType.INPUT));
        for (int i = 0; i < numOutputs; i++)
            this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), neatInnovationMap.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(), NeuronType.OUTPUT));
    }

    private void addRandomFirstLink(Random rng, NEATInnovationMap neatInnovationMap) {
        int toNode = rng.nextInt(numOutputs) + numInputs + 1; // Only Outputs considered
        int fromNode = rng.nextInt(numInputs + 1); // Bias + Inputs considered

        NEATInnovation link = neatInnovationMap.acquireLinkInnovation(toNode, fromNode);
        this.connectionGeneList.add(new ConnectionGene(toNode, fromNode, link.getInnovationID(), 1, true));
    }

    public void sortGenes() {
        Collections.sort(this.connectionGeneList);
        Collections.sort(this.neuronGeneList);
    }

    public NeuronGene getNeuronGene(int neuronID) {
        for (NeuronGene gene : this.neuronGeneList) {
            if (gene.getNeuronID() == neuronID) return gene;
        }
        // throw new NullPointerException("Neuron not found! NeuronID: " + neuronID + "\n Neurons: " + this.neuronGeneList);
        return null;
    }

    public int getNumOutputs() {
        return numOutputs;
    }

    public int getInputOffset() {
        return 1;
    }

    public int getOutputOffset() {
        return 1 + this.getNumInputs();
    }

    public int getNumInputs() {
        return numInputs;
    }

    public NeuronType getNeuronType(long neuronID) {
        if (neuronID == 0)
            return NeuronType.BIAS;
        else if (neuronID < 1 + numInputs)
            return NeuronType.INPUT;
        else if (neuronID < 1 + numInputs + numOutputs)
            return NeuronType.OUTPUT;
        else
            return NeuronType.HIDDEN;
    }

    public List<ConnectionGene> getConnectionGeneList() {
        return this.connectionGeneList;
    }

    public List<NeuronGene> getNeuronGeneList() {
        return this.neuronGeneList;
    }

    @Override
    public String toString() {
        return "NEATGenome=[ConnectionGenes:" + this.connectionGeneList + ",NodeGenes:" + this.neuronGeneList + "]";
    }

    public NEATGenome copy() {
        return new NEATGenome(this);
    }
}
