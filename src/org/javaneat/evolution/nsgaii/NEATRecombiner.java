package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronGene;
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
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT
        });
    }

    @Override
    protected NEATGenome crossover(NEATGenome parent1, NEATGenome parent2, double crossoverStrength, double crossoverProbability) {
        Random r = ThreadLocalRandom.current();

        NEATGenome best, notBest;
        best = parent1;
        notBest = parent2;
        /*
        if (parent1.getAdjustedScore() == parent2.getAdjustedScore()) {
            best = r.nextBoolean() ? parent1 : parent2;
            notBest = best == parent1 ? parent2 : parent1;
        } else {
            best = parent1.getScore() > parent2.getScore() ? parent1 : parent2;
            notBest = parent1.getScore() < parent2.getScore() ? parent1 : parent2;
        }*/

        List<ConnectionGene> parent1Genes = parent1.getConnectionGeneList();
        List<ConnectionGene> parent2Genes = parent2.getConnectionGeneList();
        parent1.sortGenes();
        parent2.sortGenes();

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
            if (parent1GeneIndex < parent1Genes.size()) parent1Gene = parent1Genes.get(parent1GeneIndex);
            ConnectionGene parent2Gene = null;
            if (parent2GeneIndex < parent2Genes.size()) parent2Gene = parent2Genes.get(parent2GeneIndex);

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
                selectedGene = new ConnectionGene(selectedGene.getFromNode(), selectedGene.getToNode(), selectedGene.getInnovationID(),
                        selectedGene.getWeight(), selectedGene.getEnabled() || r.nextInt(4) == 0);
                // 75% chance to be disabled if the parent's gene was disabled
                if (offspringConnectionGenes.size() == 0) {
                    offspringConnectionGenes.add(selectedGene);
                } else {
                    if (offspringConnectionGenes.get(offspringConnectionGenes.size() - 1).getInnovationID() != selectedGene.getInnovationID()) {
                        offspringConnectionGenes.add(selectedGene);
                    } else {
                        System.err.println(parent1.hashCode() + " | " + parent2.hashCode());
                        throw new IllegalStateException("Previous gene was duplicate, this should not happen. Genes: " + offspringConnectionGenes + " To add: " + selectedGene);
                    }
                }

                if (!addedNeuronIDs.contains(selectedGene.getFromNode())) {
                    NeuronGene gene = getNeuron(selectedGene.getFromNode(), best, notBest, r);
                    offspringNeuronGenes.add(gene);
                    addedNeuronIDs.add(selectedGene.getFromNode());
                    if (gene == null) {
                        System.err.println("\nAlpha parent: " + parent1 + "\nBeta parent: " + parent2 + "\nSelected gene: " + selectedGene);
                    }
                    assert gene != null : "NeuronID is " + selectedGene.getFromNode();
                }
                if (!addedNeuronIDs.contains(selectedGene.getToNode())) {
                    NeuronGene gene = getNeuron(selectedGene.getToNode(), best, notBest, r);
                    offspringNeuronGenes.add(gene);
                    addedNeuronIDs.add(selectedGene.getToNode());
                    if (gene == null) {
                        System.err.println("\nAlpha parent: " + parent1 + "\nBeta parent: " + parent2 + "\nSelected gene: " + selectedGene);
                    }
                    assert gene != null;
                }
            }
        }

        return new NEATGenome(offspringConnectionGenes, offspringNeuronGenes, parent1.getManager());
    }
}
