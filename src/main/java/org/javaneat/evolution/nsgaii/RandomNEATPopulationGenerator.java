package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.speciation.Species;
import org.jnsgaii.population.Population;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/13/2016.
 */
public class RandomNEATPopulationGenerator extends NEATPopulationGenerator {

    public RandomNEATPopulationGenerator(NEATInnovationMap neatInnovationMap) {
        super(neatInnovationMap);
    }

    @Override
    public Population<NEATGenome> generatePopulation(int num, Properties properties) {
        List<Individual<NEATGenome>> population = new ArrayList<>(num);
        final double[] defaultAspects = (double[]) properties.getValue(Key.DoubleKey.DefaultDoubleKey.INITIAL_ASPECT_ARRAY);
        final int numInputs = properties.getInt(NEATIntKey.INPUT_COUNT);
        final int numOutputs = properties.getInt(NEATIntKey.OUTPUT_COUNT);
        long currentIndividualID = 0;
        long currentSpeciesID = 0;

        for (int i = 0; i < num; i++) {
            NEATGenome genome = new NEATGenome(ThreadLocalRandom.current(), numInputs, numOutputs, neatInnovationMap);
            population.add(new Individual<>(genome, defaultAspects, currentIndividualID++));
        }
        Set<Long> usedIDs = population.stream().map(i -> i.id).collect(Collectors.toSet());

        return new Population<>(population, Collections.singleton(new Species(usedIDs, currentSpeciesID++)), currentSpeciesID, currentIndividualID);
    }

    @Override
    public Key[] requestProperties() {
        return new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT, NEATIntKey.INITIAL_LINK_COUNT
        };
    }
}
