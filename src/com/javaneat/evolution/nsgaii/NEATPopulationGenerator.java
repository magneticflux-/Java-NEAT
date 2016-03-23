package com.javaneat.evolution.nsgaii;

import com.javaneat.evolution.NEATGenomeManager;
import com.javaneat.evolution.nsgaii.keys.NEATIntKey;
import com.javaneat.genome.NEATGenome;
import org.skaggs.ec.population.PopulationGenerator;
import org.skaggs.ec.population.individual.Individual;
import org.skaggs.ec.properties.Key;
import org.skaggs.ec.properties.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/13/2016.
 */
public class NEATPopulationGenerator implements PopulationGenerator<NEATGenome> {

    private final Source source;
    private final NEATGenomeManager genomeManager;
    private final Collection<NEATGenome> seed;

    public NEATPopulationGenerator(NEATGenomeManager genomeManager) {
        this.source = Source.RANDOM;
        this.genomeManager = genomeManager;
        this.seed = new ArrayList<>();
    }

    public NEATPopulationGenerator(NEATGenomeManager genomeManager, Collection<NEATGenome> seed) {
        this.seed = new ArrayList<>(seed);
        this.source = Source.SEEDED;
        this.genomeManager = genomeManager;
    }

    @Override
    public List<Individual<NEATGenome>> generatePopulation(int num, Properties properties) {
        List<Individual<NEATGenome>> population = new ArrayList<>(num);
        final double[] defaultAspects = (double[]) properties.getValue(Key.DoubleKey.DefaultDoubleKey.INITIAL_ASPECT_ARRAY);

        switch (source) {
            case RANDOM:
                for (int i = 0; i < population.size(); i++) {
                    NEATGenome genome = new NEATGenome(ThreadLocalRandom.current(), this.genomeManager);
                    population.add(new Individual<>(genome, defaultAspects));
                }
                break;
            case SEEDED:
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
