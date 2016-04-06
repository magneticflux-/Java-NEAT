package org.javaneat.genome;

import org.javaneat.evolution.NEATGenomeManager;
import org.javaneat.evolution.nsgaii.MarioBrosData;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class NEATGenome implements Serializable
// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
{
    private final List<ConnectionGene> connectionGeneList;
    private final List<NeuronGene> neuronGeneList;
    @Nullable
    public MarioBrosData marioBrosData;
    private NEATSpecies species;
    private double score;
    private NEATGenomeManager manager;

    public NEATGenome(final NEATGenome other) {
        this(
                other.connectionGeneList.stream().map(ConnectionGene::new).collect(Collectors.toList()),
                other.neuronGeneList.stream().map(NeuronGene::new).collect(Collectors.toList()),
                other.manager);
    }

    public NEATGenome(final List<ConnectionGene> connections, final List<NeuronGene> neurons, final NEATGenomeManager manager) {
        this.manager = manager;
        this.connectionGeneList = new ArrayList<>(connections);
        this.neuronGeneList = new ArrayList<>(neurons);
    }

    @SuppressWarnings({"unused", "AssignmentToNull"})
    private NEATGenome() // This is to serialize properly
    {
        this.connectionGeneList = new ArrayList<>();
        this.neuronGeneList = new ArrayList<>();
        this.manager = null;
    }

    public NEATGenome(final Random rng, final NEATGenomeManager manager) {
        this.manager = manager;
        this.connectionGeneList = new ArrayList<>(1);
        this.neuronGeneList = new ArrayList<>(this.manager.getNumInputs() + this.manager.getNumOutputs() + 1);

        this.addInitialNodes();
        this.addRandomFirstLink(rng);
        this.sortGenes();
        this.verifyGenome();
    }

    private void addInitialNodes() {
        this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(), NeuronType.BIAS));
        for (int i = 0; i < manager.getNumInputs(); i++)
            this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(), NeuronType.INPUT));
        for (int i = 0; i < manager.getNumOutputs(); i++)
            this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.acquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(), NeuronType.OUTPUT));
    }

    private void addRandomFirstLink(final Random rng) {
        int toNode = rng.nextInt(manager.getNumOutputs()) + manager.getOutputOffset(); // Only Outputs considered
        int fromNode = rng.nextInt(manager.getNumInputs() + 1); // Bias + Inputs considered

        NEATInnovation link = this.manager.acquireLinkInnovation(toNode, fromNode);
        this.connectionGeneList.add(new ConnectionGene(toNode, fromNode, link.getInnovationID(), 1, true));
    }

    public void sortGenes() {
        Collections.sort(this.connectionGeneList);
        Collections.sort(this.neuronGeneList);
    }

    public void verifyGenome() {
        AtomicBoolean error = new AtomicBoolean(false);

        if (connectionGeneList.size() < 1) {
            System.err.println("No connection genes!");
            error.set(true);
        }

        if (neuronGeneList.size() < 1) {
            System.err.println("No neuron genes!");
            error.set(true);
        }

        for (ConnectionGene gene1 : connectionGeneList) {
            connectionGeneList.stream().filter(gene2 -> gene1 != gene2 && gene1.equals(gene2)).forEach(gene2 -> {
                System.err.println("Duplicate gene " + gene1);
                error.set(true);
            });

            boolean toNeuronExists = neuronGeneList.stream().anyMatch(neuronGene -> neuronGene.getNeuronID() == gene1.getToNode());
            boolean fromNeuronExists = neuronGeneList.stream().anyMatch(neuronGene -> neuronGene.getNeuronID() == gene1.getFromNode());
            if (!toNeuronExists) {
                System.err.println("Neuron " + gene1.getToNode() + " does not exist!");
                error.set(true);
            }
            if (!fromNeuronExists) {
                System.err.println("Neuron " + gene1.getFromNode() + " does not exist!");
                error.set(true);
            }
        }

        if (error.get()) {
            System.out.println("Genome connections: " + this.connectionGeneList);
            System.out.println("Genome neurons: " + this.neuronGeneList);
            System.out.println("Genome: " + this);
            throw new Error();
        }
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

    @Deprecated
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

    public NEATGenome copy() {
        return new NEATGenome(this);
    }
}
