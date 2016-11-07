package org.javaneat.evolution.nsgaii;

import org.apache.commons.collections4.set.UnmodifiableSet;
import org.jnsgaii.operators.SpeciatorEx;
import org.jnsgaii.population.individual.Individual;

import java.util.Random;

/**
 * Created by skaggsm on 10/27/16.
 */

public class NEATSpeciatorEx<NEATGenome> extends SpeciatorEx<NEATGenome> {
    @Override
    public UnmodifiableSet<Species<NEATGenome>> getSpecies() {
        return null;
    }

    @Override
    public double getDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        return 0;
    }

    @Override
    protected double getMaxDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        return 0;
    }

    @Override
    public void modifyAspects(double[] aspects, Random r) {
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[0];
    }
}
