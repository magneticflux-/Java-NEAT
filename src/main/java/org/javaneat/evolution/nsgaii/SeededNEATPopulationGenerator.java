package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.population.Population;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mitchell on 8/20/2016.
 */
public class SeededNEATPopulationGenerator extends NEATPopulationGenerator {

    private final Population<NEATGenome> seed;

    public SeededNEATPopulationGenerator(NEATInnovationMap neatInnovationMap, Population<NEATGenome> seed) {
        super(neatInnovationMap);
        this.seed = seed;
    }

    @Override
    public Key[] requestProperties() {
        return new Key[0];
    }

    @Override
    public Population<NEATGenome> generatePopulation(int num, Properties properties) {
        List<Individual<NEATGenome>> population = new ArrayList<>(num);

        Iterator<? extends Individual<NEATGenome>> iterator = seed.getPopulation().iterator();
        for (int i = 0; i < num && iterator.hasNext(); i++) {
            Individual<NEATGenome> next = iterator.next();
            population.add(next);
            //population.add(new Individual<>(new NEATGenome(next.getIndividual()), next.aspects,next.id));
        }

        return new Population<>(population, seed.getCurrentIndividualID(), seed.getCurrentSpeciesID());
    }
}
