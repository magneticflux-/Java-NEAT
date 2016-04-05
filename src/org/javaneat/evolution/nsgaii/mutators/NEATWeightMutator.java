package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.Mutator;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by Mitchell on 3/23/2016.
 */
public class NEATWeightMutator extends Mutator<NEATGenome> {
    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Weight Mutation Strength", "Weight Mutation Probability"};
    }

    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        newObject.getConnectionGeneList().stream()
                .filter(gene -> ThreadLocalRandom.current().nextDouble() < mutationProbability)
                .forEach(gene -> gene.setWeight(gene.getWeight() + (ThreadLocalRandom.current().nextDouble() * 2 - 1) * mutationStrength));

        newObject.sortGenes();
        newObject.verifyGenome();
        return newObject;
    }
}
