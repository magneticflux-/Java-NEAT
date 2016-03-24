package org.javaneat.evolution.nsgaii;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.Mutator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATDisableGeneMutator extends Mutator<NEATGenome> {
    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Disable Gene Mutation Strength", "Enable Gene Mutation Probability"};
    }

    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        List<ConnectionGene> validGenes = object.getConnectionGeneList().stream().filter(gene -> !gene.getEnabled()).collect(Collectors.toList());
        if (validGenes.size() > 0)
            validGenes.get(ThreadLocalRandom.current().nextInt(validGenes.size())).setEnabled(false);

        return object;
    }
}
