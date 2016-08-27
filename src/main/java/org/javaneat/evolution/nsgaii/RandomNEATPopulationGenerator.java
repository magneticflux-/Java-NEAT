package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/13/2016.
 */
public class RandomNEATPopulationGenerator extends NEATPopulationGenerator {

    public RandomNEATPopulationGenerator(NEATInnovationMap neatInnovationMap) {
        super(neatInnovationMap);
    }

    @Override
    public List<Individual<NEATGenome>> generatePopulation(int num, Properties properties) {
        List<Individual<NEATGenome>> population = new ArrayList<>(num);
        final double[] defaultAspects = (double[]) properties.getValue(Key.DoubleKey.DefaultDoubleKey.INITIAL_ASPECT_ARRAY);
        final int numInputs = properties.getInt(NEATIntKey.INPUT_COUNT);
        final int numOutputs = properties.getInt(NEATIntKey.OUTPUT_COUNT);

        for (int i = 0; i < num; i++) {
            NEATGenome genome = new NEATGenome(ThreadLocalRandom.current(), numInputs, numOutputs, neatInnovationMap);
            population.add(new Individual<>(genome, defaultAspects));
        }

        return population;
    }

    @Override
    public Key[] requestProperties() {
        return new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT, NEATIntKey.INITIAL_LINK_COUNT
        };
    }
}
