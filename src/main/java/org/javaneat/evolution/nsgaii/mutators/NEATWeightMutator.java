package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.Mutator;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by Mitchell Skaggs on 3/23/2016.
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
                .filter(ConnectionGene::getEnabled)
                .filter(gene -> ThreadLocalRandom.current().nextDouble() < mutationProbability)
                .forEach(gene -> gene.setWeight(Mutator.mutate(gene.getWeight(), ThreadLocalRandom.current(), mutationStrength)));

        //newObject.sortGenes();
        return newObject;
    }
}
