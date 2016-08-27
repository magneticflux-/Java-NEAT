package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mitchell on 8/20/2016.
 */
public class SeededNEATPopulationGenerator extends NEATPopulationGenerator {

    private final Collection<Individual<NEATGenome>> seed;

    public SeededNEATPopulationGenerator(NEATInnovationMap neatInnovationMap, Collection<Individual<NEATGenome>> seed) {
        super(neatInnovationMap);
        this.seed = new ArrayList<>(seed);
    }

    @Override
    public Key[] requestProperties() {
        return new Key[0];
    }

    @Override
    public List<Individual<NEATGenome>> generatePopulation(int num, Properties properties) {
        List<Individual<NEATGenome>> population = new ArrayList<>(num);

        Iterator<Individual<NEATGenome>> iterator = seed.iterator();
        for (int i = 0; i < num; i++) {
            Individual<NEATGenome> next = iterator.next();
            population.add(new Individual<>(new NEATGenome(next.getIndividual()), next.aspects));

            if (!iterator.hasNext()) { // Reset to the beginning
                iterator = seed.iterator();
            }
        }

        return population;
    }
}
