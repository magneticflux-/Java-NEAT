package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.Mutator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 *
 * DO NOT USE! Breaks NEAT because splitting nodes relies on connections staying disabled
 */
@Deprecated
public class NEATEnableGeneMutator extends Mutator<NEATGenome> {
    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Enable Gene Mutation Strength", "Enable Gene Mutation Probability"};
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        while (ThreadLocalRandom.current().nextDouble() <= mutationStrength) {
            mutationStrength--; // If strength is 1.5, 100% chance to remove first time, 50% second, 0% final check.
            List<ConnectionGene> validGenes = newObject.getConnectionGeneList().stream().filter(gene -> !gene.getEnabled()).collect(Collectors.toList());
            if (validGenes.size() > 0)
                validGenes.get(ThreadLocalRandom.current().nextInt(validGenes.size())).setEnabled(true);
        }

        newObject.sortGenes();
        newObject.verifyGenome();
        return newObject;
    }
}
