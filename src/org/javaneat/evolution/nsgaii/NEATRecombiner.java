package org.javaneat.evolution.nsgaii;

import org.apache.commons.math3.util.FastMath;
import org.javaneat.evolution.NEATGenomeManager;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronGene;
import org.javaneat.genome.NeuronType;
import org.jnsgaii.operators.Recombiner;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;
import org.jnsgaii.util.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATRecombiner extends Recombiner<NEATGenome> {
    private int numInputs;
    private int numOutputs;

    private static NeuronGene getNeuron(int neuronID, NEATGenome best, NEATGenome notBest, Random rng) {
        NeuronGene output;
        output = best.getNeuronGene(neuronID);
        if (output == null) output = notBest.getNeuronGene(neuronID);
        return output;
    }

    @Override
    public void updateProperties(Properties properties) {
        super.updateProperties(properties);
        numInputs = properties.getInt(NEATIntKey.INPUT_COUNT);
        numOutputs = properties.getInt(NEATIntKey.OUTPUT_COUNT);
    }

    @Override
    protected NEATGenome crossover(NEATGenome parent1, NEATGenome parent2, double crossoverStrength, double crossoverProbability) {
        Random r = ThreadLocalRandom.current();
        NEATGenomeManager manager = parent1.getManager();

        // --------------
        // START NEW CODE
        // --------------

        parent1.sortGenes();
        parent2.sortGenes();

        int maxInnovationNumber = FastMath.max(
                parent1.getConnectionGeneList().stream().mapToInt(ConnectionGene::getInnovationID).max().orElseThrow(() -> new Error("Failure to get max!")),
                parent2.getConnectionGeneList().stream().mapToInt(ConnectionGene::getInnovationID).max().orElseThrow(() -> new Error("Failure to get max!")));

        ListIterator<ConnectionGene> parent1Iterator = parent1.getConnectionGeneList().listIterator();
        ListIterator<ConnectionGene> parent2Iterator = parent2.getConnectionGeneList().listIterator();

        List<ConnectionGene> newConnectionGenes = new ArrayList<>();

        while (parent1Iterator.hasNext() || parent2Iterator.hasNext()) {
            if (parent1Iterator.hasNext() && !parent2Iterator.hasNext()) {
                newConnectionGenes.add(parent1Iterator.next());
            } else if (!parent1Iterator.hasNext() && parent2Iterator.hasNext()) {
                newConnectionGenes.add(parent2Iterator.next());
            } else { // If both lists have genes remaining...
                ConnectionGene parent1CurrentGene = parent1Iterator.next();
                ConnectionGene parent2CurrentGene = parent2Iterator.next();

                if (parent1CurrentGene.getInnovationID() < parent2CurrentGene.getInnovationID()) {
                    newConnectionGenes.add(parent1CurrentGene);
                    parent2Iterator.previous();
                } else if (parent1CurrentGene.getInnovationID() > parent2CurrentGene.getInnovationID()) {
                    newConnectionGenes.add(parent2CurrentGene);
                    parent1Iterator.previous();
                } else { // If they are the same gene
                    if (r.nextDouble() < crossoverStrength) { // Add the second one OR...
                        newConnectionGenes.add(parent2CurrentGene);
                    } else { // Add the first one
                        newConnectionGenes.add(parent1CurrentGene);
                    }
                }
            }
        }

        Collection<Integer> usedHiddenNeurons = new HashSet<>();
        newConnectionGenes.forEach(connectionGene -> {
            if (manager.getNeuronType(connectionGene.getToNode()) == NeuronType.HIDDEN)
                usedHiddenNeurons.add(connectionGene.getToNode());
            if (manager.getNeuronType(connectionGene.getFromNode()) == NeuronType.HIDDEN)
                usedHiddenNeurons.add(connectionGene.getFromNode());
        });

        List<NeuronGene> newNeuronGenes = new ArrayList<>();

        for (int i = 0; i < 1 + manager.getNumInputs() + parent2.getManager().getNumOutputs(); i++) {
            newNeuronGenes.add(new NeuronGene(i, manager.acquireNodeInnovation(i).getInnovationID(), manager.getNeuronType(i)));
        }
        usedHiddenNeurons.forEach(integer -> newNeuronGenes.add(new NeuronGene(integer, manager.acquireNodeInnovation(integer).getInnovationID(), manager.getNeuronType(integer))));

        NEATGenome genome = new NEATGenome(newConnectionGenes, newNeuronGenes, manager);

        genome.sortGenes();
        genome.verifyGenome();
        return genome;

        // ------------
        // END NEW CODE
        // ------------
/*
        NEATGenome best, notBest;
        best = parent1;
        notBest = parent2;

        if (parent1.getAdjustedScore() == parent2.getAdjustedScore()) {
            best = r.nextBoolean() ? parent1 : parent2;
            notBest = best == parent1 ? parent2 : parent1;
        } else {
            best = parent1.getScore() > parent2.getScore() ? parent1 : parent2;
            notBest = parent1.getScore() < parent2.getScore() ? parent1 : parent2;
        }

        parent1.sortGenes();
        parent2.sortGenes();

        List<ConnectionGene> parent1Genes = parent1.getConnectionGeneList();
        List<ConnectionGene> parent2Genes = parent2.getConnectionGeneList();

        int parent1GeneIndex = 0;
        int parent2GeneIndex = 0;

        List<ConnectionGene> offspringConnectionGenes = new ArrayList<>();
        List<NeuronGene> offspringNeuronGenes = new ArrayList<>();
        Collection<Integer> addedNeuronIDs = new HashSet<>();

        for (int i = 0; i < 1 + numInputs + numOutputs; i++) // Acquire required neurons
        {
            offspringNeuronGenes.add(parent1.getNeuronGene(i));
            addedNeuronIDs.add(i);
        }

        while (parent1GeneIndex < parent1Genes.size() || parent2GeneIndex < parent2Genes.size()) {
            ConnectionGene selectedGene = null;

            ConnectionGene parent1Gene = null;
            if (parent1GeneIndex < parent1Genes.size())
                parent1Gene = parent1Genes.get(parent1GeneIndex);
            ConnectionGene parent2Gene = null;
            if (parent2GeneIndex < parent2Genes.size())
                parent2Gene = parent2Genes.get(parent2GeneIndex);

            if (parent1Gene == null && parent2Gene != null) {
                if (best == parent2) {
                    selectedGene = parent2Gene;
                }
                parent2GeneIndex++;
            } else if (parent1Gene != null && parent2Gene == null) {
                if (parent1 == best) {
                    selectedGene = parent1Gene;
                }
                parent1GeneIndex++;
            } else {
                assert parent1Gene != null;
                if (parent1Gene.getInnovationID() > parent2Gene.getInnovationID()) {
                    if (best == parent2) {
                        selectedGene = parent2Gene;
                    }
                    parent2GeneIndex++;
                } else if (parent1Gene.getInnovationID() < parent2Gene.getInnovationID()) {
                    if (best == parent1) {
                        selectedGene = parent1Gene;
                    }
                    parent1GeneIndex++;
                } else if (parent1Gene.getInnovationID() == parent2Gene.getInnovationID()) {
                    if (r.nextBoolean()) {
                        selectedGene = parent1Gene;
                    } else {
                        selectedGene = parent2Gene;
                    }
                    parent1GeneIndex++;
                    parent2GeneIndex++;
                }
            }

            if (selectedGene != null) {
                selectedGene = new ConnectionGene(selectedGene);
                if (offspringConnectionGenes.size() == 0) {
                    offspringConnectionGenes.add(selectedGene);
                } else if (offspringConnectionGenes.get(offspringConnectionGenes.size() - 1).getInnovationID() != selectedGene.getInnovationID()) {
                    offspringConnectionGenes.add(selectedGene);
                } else {
                    System.err.println(parent1.hashCode() + " | " + parent2.hashCode());
                    System.err.println("P1: " + parent1.getConnectionGeneList());
                    System.err.println("P2: " + parent2.getConnectionGeneList());
                    throw new IllegalStateException("Previous gene was duplicate, this should not happen. Genes: " + offspringConnectionGenes + " To add: " + selectedGene);
                }

                if (!addedNeuronIDs.contains(selectedGene.getFromNode())) {
                    NeuronGene gene = getNeuron(selectedGene.getFromNode(), best, notBest, r);
                    offspringNeuronGenes.add(new NeuronGene(gene));
                    addedNeuronIDs.add(selectedGene.getFromNode());
                    if (gene == null) {
                        System.err.println("\nAlpha parent: " + parent1 + "\nBeta parent: " + parent2 + "\nSelected gene: " + selectedGene);
                    }
                    assert gene != null : "NeuronID is " + selectedGene.getFromNode();
                }
                if (!addedNeuronIDs.contains(selectedGene.getToNode())) {
                    NeuronGene gene = getNeuron(selectedGene.getToNode(), best, notBest, r);
                    offspringNeuronGenes.add(new NeuronGene(gene));
                    addedNeuronIDs.add(selectedGene.getToNode());
                    if (gene == null) {
                        System.err.println("\nAlpha parent: " + parent1 + "\nBeta parent: " + parent2 + "\nSelected gene: " + selectedGene);
                    }
                    assert gene != null;
                }
            }
        }

        NEATGenome genome = new NEATGenome(offspringConnectionGenes, offspringNeuronGenes, manager);

        genome.sortGenes();
        genome.verifyGenome();
        return genome;*/
    }

    @Override
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT
        });
    }
}
