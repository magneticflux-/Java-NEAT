package org.javaneat.evolution.nsgaii;

import org.javaneat.evolution.NEATGenomeManager;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.examples.defaultoperatorframework.RouletteWheelLinearSelection;
import org.jnsgaii.multiobjective.NSGA_II;
import org.jnsgaii.operators.*;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mitchell on 3/13/2016.
 */
public final class Run {
    private Run() {
    }

    public static void main(String[] args) {
        NEATGenomeManager neatGenomeManager = new NEATGenomeManager();

        Properties properties = new Properties()
                .setValue(Key.DoubleKey.DefaultDoubleKey.INITIAL_ASPECT_ARRAY, new double[]{0, 0, 0, 0});

        NEATPopulationGenerator neatPopulationGenerator = new NEATPopulationGenerator(neatGenomeManager);

        NEATSpeciator neatSpeciator = new NEATSpeciator();
        List<Mutator<NEATGenome>> mutators = Arrays.asList(new NEATWeightMutator(), new NEATLinkAdditionMutator(), new NEATLinkRemovalMutator(), new NEATLinkSplitMutator(), new NEATEnableGeneMutator(), new NEATDisableGeneMutator());
        Recombiner<NEATGenome> recombiner = null;
        Selector<NEATGenome> selector = new RouletteWheelLinearSelection<>();
        Operator<NEATGenome> operator = new DefaultOperator<>(mutators, recombiner, selector, neatSpeciator);

        NSGA_II<NEATGenome> nsga_ii = new NSGA_II<>(properties, operator, null, neatPopulationGenerator);
    }
}
