package org.javaneat.evolution.nsgaii;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.Mutator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkRemovalMutator extends Mutator<NEATGenome> {
    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        while (ThreadLocalRandom.current().nextDouble(0) <= mutationStrength) {
            mutationStrength--; // If strength is 1.5, 100% chance to remove first time, 50% second, 0% final check.
            if (object.getConnectionGeneList().size() > 0) {
                ConnectionGene removed = object.getConnectionGeneList().remove(ThreadLocalRandom.current().nextInt(object.getConnectionGeneList().size()));

                boolean toNeuronOrphaned = true;
                boolean fromNeuronOrphaned = true;
                for (ConnectionGene gene : object.getConnectionGeneList()) {
                    toNeuronOrphaned &= gene.getToNode() == removed.getToNode() || gene.getFromNode() == removed.getToNode();
                    fromNeuronOrphaned &= gene.getToNode() == removed.getFromNode() || gene.getFromNode() == removed.getFromNode();
                }

                final boolean finalToNeuronOrphaned = toNeuronOrphaned;
                final boolean finalFromNeuronOrphaned = fromNeuronOrphaned;
                object.getNeuronGeneList().removeIf(neuronGene -> (finalToNeuronOrphaned && neuronGene.getNeuronID() == removed.getToNode()) || (finalFromNeuronOrphaned && neuronGene.getNeuronID() == removed.getFromNode()));
            }
        }
        return object;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Removal Mutation Strength", "Link Removal Mutation Probability"};
    }
}
