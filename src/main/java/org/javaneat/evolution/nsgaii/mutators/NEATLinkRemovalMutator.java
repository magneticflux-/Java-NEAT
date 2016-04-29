package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronType;
import org.jnsgaii.operators.Mutator;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkRemovalMutator extends Mutator<NEATGenome> {
    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Removal Mutation Strength", "Link Removal Mutation Probability"};
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        Random r = ThreadLocalRandom.current();

        NEATGenome newObject = object.copy();

        while (r.nextDouble() <= mutationStrength) {
            mutationStrength--; // If strength is 1.5, 100% chance to remove first time, 50% second, 0% final check.
            if (newObject.getConnectionGeneList().size() > 1) { // Prevent the last connection from being removed

                List<ConnectionGene> potentialGenes = newObject.getConnectionGeneList().stream().filter(ConnectionGene::getEnabled).collect(Collectors.toList()); // Only remove enabled genes
                ConnectionGene removed = potentialGenes.get(r.nextInt(potentialGenes.size()));
                newObject.getConnectionGeneList().remove(removed);

                boolean toNeuronOrphaned = true;
                boolean fromNeuronOrphaned = true;
                for (ConnectionGene gene : newObject.getConnectionGeneList()) {
                    toNeuronOrphaned &= gene.getToNode() == removed.getToNode() || gene.getFromNode() == removed.getToNode();
                    fromNeuronOrphaned &= gene.getToNode() == removed.getFromNode() || gene.getFromNode() == removed.getFromNode();
                }

                final boolean finalToNeuronOrphaned = toNeuronOrphaned;
                final boolean finalFromNeuronOrphaned = fromNeuronOrphaned;
                newObject.getNeuronGeneList().removeIf(neuronGene -> neuronGene.getNeuronType() == NeuronType.HIDDEN && ((finalToNeuronOrphaned && neuronGene.getNeuronID() == removed.getToNode()) || (finalFromNeuronOrphaned && neuronGene.getNeuronID() == removed.getFromNode())));
            }
        }

        newObject.sortGenes();
        newObject.verifyGenome();
        return newObject;
    }
}
