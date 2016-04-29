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
                    boolean enabled = parent1CurrentGene.getEnabled() && parent2CurrentGene.getEnabled(); // Never add a disabled gene, this can cause the gene splitting to fail

                    if (r.nextDouble() < crossoverStrength) { // Add the second one OR...
                        newConnectionGenes.add(new ConnectionGene(parent2CurrentGene, enabled));
                    } else { // Add the first one
                        newConnectionGenes.add(new ConnectionGene(parent1CurrentGene, enabled));
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
    }

    @Override
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT
        });
    }
}
