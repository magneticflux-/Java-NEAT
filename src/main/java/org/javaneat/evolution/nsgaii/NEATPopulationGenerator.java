package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.population.Population;
import org.jnsgaii.population.PopulationGenerator;
import org.jnsgaii.properties.Properties;

/**
 * Created by Mitchell Skaggs on 8/20/2016.
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
    public abstract Population<NEATGenome> generatePopulation(int num, Properties properties);
}
