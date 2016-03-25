package org.javaneat.genome;

import org.javaneat.evolution.NEATGenomeManager;
import org.javaneat.evolution.nsgaii.MarioBrosData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NEATGenome implements Cloneable
// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
{
    private final List<ConnectionGene> connectionGeneList;
    private final NEATGenomeManager manager;
    private final List<NeuronGene> neuronGeneList;
    public MarioBrosData marioBrosData;
    private NEATSpecies species;
    private double score;

    @SuppressWarnings({"unused", "AssignmentToNull"})
    private NEATGenome() // This is to serialize properly
    {
        this.connectionGeneList = new ArrayList<>();
        this.neuronGeneList = new ArrayList<>();
        this.manager = null;
    }

    public NEATGenome(final NEATGenome other) {
        this(other.connectionGeneList, other.neuronGeneList, other.manager);
    }

    public NEATGenome(final List<ConnectionGene> connections, final List<NeuronGene> neurons, final NEATGenomeManager manager) {
        this.manager = manager;
        this.connectionGeneList = new ArrayList<>(connections.size());
        this.neuronGeneList = new ArrayList<>(neurons.size());
        for (final ConnectionGene gene : connections)
            this.connectionGeneList.add(new ConnectionGene(gene));
        for (final NeuronGene gene : neurons) {
            try {
                this.neuronGeneList.add(new NeuronGene(gene));
            } catch (NullPointerException e) {
                System.err.println("NeuronGene being added: " + gene);
                e.printStackTrace();
            }
        }
    }

    public NEATGenome(final Random rng, final NEATGenomeManager manager) {
        this.manager = manager;
        this.connectionGeneList = new ArrayList<>(1);
        this.neuronGeneList = new ArrayList<>(this.manager.getNumInputs() + this.manager.getNumOutputs() + 1);

        this.addInitialNodes();
        this.addRandomFirstLink(rng);
        this.sortGenes();
    }

    private void addInitialNodes() {
        this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
                NeuronType.BIAS));
        for (int i = 0; i < this.manager.getNumInputs(); i++)
            this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
                    NeuronType.INPUT));
        for (int i = 0; i < this.manager.getNumOutputs(); i++)
            this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
                    NeuronType.OUTPUT));
    }

    private void addRandomFirstLink(final Random rng) {
        final int inputIndex = rng.nextInt(this.manager.getNumInputs() + 1); // Bias + Inputs considered
        final int outputIndex = rng.nextInt(this.manager.getNumOutputs()) + this.manager.getOutputOffset(); // Only Outputs considered
        final NEATInnovation link = this.manager.acquireLinkInnovation(inputIndex, outputIndex);
        this.connectionGeneList.add(new ConnectionGene(inputIndex, outputIndex, link.getInnovationID(), 1, true));
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

    public List<ConnectionGene> getConnectionGeneList() {
        return this.connectionGeneList;
    }

    public NEATGenomeManager getManager() {
        return this.manager;
    }

    public List<NeuronGene> getNeuronGeneList() {
        return this.neuronGeneList;
    }

    @Override
    public String toString() {
        return "NEATGenome=[ConnectionGenes:" + this.connectionGeneList + ",NodeGenes:" + this.neuronGeneList + ",Manager:" + this.manager + "]";
    }

    /**
     * @return the species
     */
    public NEATSpecies getSpecies() {
        return species;
    }

    /**
     * @param species the species to set
     */
    public void setSpecies(NEATSpecies species) {
        this.species = species;
    }

    public double getAdjustedScore() {
        if (this.species != null && this.species.getMembers().size() != 0)
            return this.getScore() / this.species.getMembers().size();
        else
            return this.getScore();
    }

    /**
     * @return the score
     */
    public double getScore() {
        return score >= 0 ? score : 0;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    public NEATGenome clone() throws CloneNotSupportedException {
        return (NEATGenome) super.clone();
    }
}
