package com.javaneat.evolution.nsgaii;

import com.javaneat.evolution.NEATGenomeManager;
import com.javaneat.genome.NEATGenome;
import org.skaggs.ec.examples.defaultoperatorframework.RouletteWheelLinearSelection;
import org.skaggs.ec.multiobjective.NSGA_II;
import org.skaggs.ec.operators.DefaultOperator;
import org.skaggs.ec.operators.Mutator;
import org.skaggs.ec.operators.Operator;
import org.skaggs.ec.operators.Selector;
import org.skaggs.ec.properties.Key;
import org.skaggs.ec.properties.Properties;

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
        List<Mutator<NEATGenome>> mutators = Arrays.asList(new NEATWeightMutator());
        Selector<NEATGenome> selector = new RouletteWheelLinearSelection<>();
        Operator<NEATGenome> operator = new DefaultOperator<>(mutators, null, selector, neatSpeciator);

        NSGA_II<NEATGenome> nsga_ii = new NSGA_II<>(properties, operator, null, neatPopulationGenerator);
    }
}
