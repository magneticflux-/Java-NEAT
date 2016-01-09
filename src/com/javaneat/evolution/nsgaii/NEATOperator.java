package com.javaneat.evolution.nsgaii;

import com.javaneat.genome.NEATGenome;

import org.skaggs.ec.multiobjective.population.FrontedPopulation;
import org.skaggs.ec.operators.Operator;
import org.skaggs.ec.population.Population;
import org.skaggs.ec.properties.Key;
import org.skaggs.ec.properties.Properties;

/**
 * Created by skaggsm on 12/28/15.
 */
public class NEATOperator implements Operator<NEATGenome> {
    @Override
    public Population<NEATGenome> apply(FrontedPopulation<NEATGenome> population, Properties properties) {
        return null;
    }

    @Override
    public Key[] requestProperties() {
        return new Key[]{Key.DoubleKey.INITIAL_MUTATION_PROBABILITY};
    }
}
