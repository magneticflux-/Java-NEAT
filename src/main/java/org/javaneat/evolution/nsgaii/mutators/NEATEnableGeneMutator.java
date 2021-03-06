package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.Mutator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell Skaggs on 3/24/2016.
 * <p>
 * DO NOT USE! Breaks NEAT because splitting nodes relies on connections staying disabled
 * Nevermind lol
 */
public class NEATEnableGeneMutator extends Mutator<NEATGenome> {
    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Enable Gene Mutation Strength", "Enable Gene Mutation Probability"};
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        List<ConnectionGene> validGenes = newObject.getConnectionGeneList().stream().filter(gene -> !gene.getEnabled()).collect(Collectors.toList());
        if (validGenes.size() > 0)
            validGenes.get(ThreadLocalRandom.current().nextInt(validGenes.size())).setEnabled(true);

        return newObject;
    }
}
