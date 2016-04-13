package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATGenomeManager;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.population.PopulationGenerator;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/13/2016.
 */
public class NEATPopulationGenerator implements PopulationGenerator<NEATGenome> {

    private final Source source;
    private final Collection<Individual<NEATGenome>> seed;

    public NEATPopulationGenerator() {
        this.source = Source.RANDOM;
        this.seed = new ArrayList<>();
    }

    public NEATPopulationGenerator(Collection<Individual<NEATGenome>> seed) {
        this.seed = new ArrayList<>(seed);
        this.source = Source.SEEDED;
    }

    @Override
    public List<Individual<NEATGenome>> generatePopulation(int num, Properties properties) {
        List<Individual<NEATGenome>> population = new ArrayList<>(num);
        final double[] defaultAspects = (double[]) properties.getValue(Key.DoubleKey.DefaultDoubleKey.INITIAL_ASPECT_ARRAY);
        final int numInputs = properties.getInt(NEATIntKey.INPUT_COUNT);
        final int numOutputs = properties.getInt(NEATIntKey.OUTPUT_COUNT);

        NEATGenomeManager genomeManager = new NEATGenomeManager(numInputs, numOutputs);

        genomeManager.numInputs = numInputs;
        genomeManager.numOutputs = numOutputs;

        switch (source) {
            case RANDOM:
                for (int i = 0; i < num; i++) {
                    NEATGenome genome = new NEATGenome(ThreadLocalRandom.current(), genomeManager);
                    population.add(new Individual<>(genome, defaultAspects));
                }
                break;
            case SEEDED:
                Iterator<Individual<NEATGenome>> iterator = seed.iterator();
                for (int i = 0; i < num; i++) {
                    Individual<NEATGenome> next = iterator.next();
                    population.add(new Individual<>(new NEATGenome(next.getIndividual()), next.aspects));

                    if (!iterator.hasNext()) { // Reset to the beginning
                        iterator = seed.iterator();
                    }
                }
                break;
        }

        return population;
    }

    @Override
    public Key[] requestProperties() {
        return new Key[]{
                NEATIntKey.INPUT_COUNT, NEATIntKey.OUTPUT_COUNT, NEATIntKey.INITIAL_LINK_COUNT
        };
    }

    private enum Source {
        RANDOM, SEEDED
    }
}
