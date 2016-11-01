package org.javaneat.evolution.nsgaii;

import org.apache.commons.lang3.tuple.Pair;
import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.population.Population;
import org.jnsgaii.population.PopulationGenerator;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.Properties;

import java.util.List;

/**
 * Created by Mitchell on 8/20/2016.
 */
public abstract class NEATPopulationGenerator implements PopulationGenerator<NEATGenome> {
    protected final NEATInnovationMap neatInnovationMap;

    public NEATPopulationGenerator(NEATInnovationMap neatInnovationMap) {
        this.neatInnovationMap = neatInnovationMap;
    }

    public static NEATPopulationGenerator createNEATPopulationGenerator(NEATInnovationMap neatInnovationMap) {
        return new RandomNEATPopulationGenerator(neatInnovationMap);
    }

    public static NEATPopulationGenerator createNEATPopulationGenerator(NEATInnovationMap neatInnovationMap, Population<NEATGenome> seed) {
        return new SeededNEATPopulationGenerator(neatInnovationMap, seed);
    }

    @Override
    public abstract Pair<List<Individual<NEATGenome>>, Long> generatePopulation(int num, Properties properties);
}
