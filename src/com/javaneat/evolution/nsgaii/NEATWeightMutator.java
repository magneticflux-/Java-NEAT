package com.javaneat.evolution.nsgaii;

import com.javaneat.genome.NEATGenome;
import org.skaggs.ec.operators.Mutator;

import java.util.concurrent.ThreadLocalRandom;

import static org.jzy3d.colors.Color.rng;

/**
 * Created by Mitchell on 3/23/2016.
 */
public class NEATWeightMutator extends Mutator<NEATGenome> {
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {

        object.getConnectionGeneList().parallelStream()
                .filter(gene -> ThreadLocalRandom.current().nextDouble() < mutationProbability)
                .forEach(gene -> gene.setWeight(gene.getWeight() + (rng.nextDouble() * 2 - 1) * mutationStrength));

        return object;
    }
}
