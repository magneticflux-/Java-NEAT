package com.javaneat.evolution.nsgaii;

import com.javaneat.genome.NEATGenome;
import org.skaggs.ec.multiobjective.NSGA_II;
import org.skaggs.ec.operators.DefaultOperator;
import org.skaggs.ec.operators.Operator;
import org.skaggs.ec.properties.Key;
import org.skaggs.ec.properties.Properties;

/**
 * Created by Mitchell on 3/13/2016.
 */
public final class Run {
    private Run() {
    }

    public static void main(String[] args) {
        Properties properties = new Properties()
                .setValue(Key.DoubleKey.DefaultDoubleKey.INITIAL_ASPECT_ARRAY, new double[]{0, 0, 0, 0});

        Operator<NEATGenome> operator = new DefaultOperator<>(null, null, null, null);

        NSGA_II<NEATGenome> nsga_ii = new NSGA_II<>(properties, null, null, null);
    }
}
