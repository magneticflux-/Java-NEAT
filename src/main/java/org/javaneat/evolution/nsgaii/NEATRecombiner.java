package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronGene;
import org.javaneat.genome.NeuronType;
import org.jnsgaii.operators.Recombiner;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;
import org.jnsgaii.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATRecombiner extends Recombiner<NEATGenome> {
    private final NEATInnovationMap neatInnovationMap;
    private int numInputs;
    private int numOutputs;

    public NEATRecombiner(NEATInnovationMap neatInnovationMap) {
        this.neatInnovationMap = neatInnovationMap;
    }

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

        System.out.println("Innovations: " + neatInnovationMap.size());
    }

    @Override
    protected NEATGenome crossover(NEATGenome parent1, NEATGenome parent2, double crossoverStrength, double crossoverProbability) {
        Random r = ThreadLocalRandom.current();

        //int maxInnovationNumber = FastMath.max(
        //        parent1.getConnectionGeneList().stream().mapToInt(ConnectionGene::getInnovationID).max().orElseThrow(() -> new Error("Failure to get max!")),
        //        parent2.getConnectionGeneList().stream().mapToInt(ConnectionGene::getInnovationID).max().orElseThrow(() -> new Error("Failure to get max!")));

        ListIterator<ConnectionGene> parent1Iterator = parent1.getConnectionGeneList().listIterator();
        ListIterator<ConnectionGene> parent2Iterator = parent2.getConnectionGeneList().listIterator();

        List<ConnectionGene> newConnectionGenes = new ArrayList<>();

        while (parent1Iterator.hasNext() || parent2Iterator.hasNext()) {
            if (parent1Iterator.hasNext() && !parent2Iterator.hasNext()) {
                newConnectionGenes.add(new ConnectionGene(parent1Iterator.next()));
            } else if (!parent1Iterator.hasNext() && parent2Iterator.hasNext()) {
                newConnectionGenes.add(new ConnectionGene(parent2Iterator.next()));
            } else { // If both lists have genes remaining...
                ConnectionGene parent1CurrentGene = parent1Iterator.next();
                ConnectionGene parent2CurrentGene = parent2Iterator.next();

                if (parent1CurrentGene.getInnovationID() < parent2CurrentGene.getInnovationID()) {
                    newConnectionGenes.add(new ConnectionGene(parent1CurrentGene));
                    parent2Iterator.previous();
                } else if (parent1CurrentGene.getInnovationID() > parent2CurrentGene.getInnovationID()) {
                    newConnectionGenes.add(new ConnectionGene(parent2CurrentGene));
                    parent1Iterator.previous();
                } else { // If they are the same gene
                    boolean enabled = parent1CurrentGene.getEnabled() && parent2CurrentGene.getEnabled(); // Never add a disabled gene, this can cause the gene splitting to fail

                    if (r.nextDouble() < crossoverStrength) { // Add the second one OR...
                        newConnectionGenes.add(new ConnectionGene(parent2CurrentGene, enabled));
                    } else { // Add the first one
                        newConnectionGenes.add(new ConnectionGene(parent1CurrentGene, enabled));
                    }
                }
            }
        }

        Collection<Long> usedHiddenNeurons = new HashSet<>();
        newConnectionGenes.forEach(connectionGene -> {
            if (parent1.getNeuronType(connectionGene.getToNode()) == NeuronType.HIDDEN)
                usedHiddenNeurons.add(connectionGene.getToNode());
            if (parent1.getNeuronType(connectionGene.getFromNode()) == NeuronType.HIDDEN)
                usedHiddenNeurons.add(connectionGene.getFromNode());
        });

        List<NeuronGene> newNeuronGenes = new ArrayList<>();

        for (int i = 0; i < 1 + numInputs + numOutputs; i++) {
            newNeuronGenes.add(new NeuronGene(i, neatInnovationMap.acquireNodeInnovation(i).getInnovationID(), parent1.getNeuronType(i)));
        }
        usedHiddenNeurons.forEach(nodeID -> newNeuronGenes.add(new NeuronGene(nodeID, neatInnovationMap.acquireNodeInnovation(nodeID).getInnovationID(), parent1.getNeuronType(nodeID))));

        NEATGenome genome = new NEATGenome(newConnectionGenes, newNeuronGenes, numInputs, numOutputs);

        genome.sortGenes();
        return genome;
    }

    @Override
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT
        });
    }
}
