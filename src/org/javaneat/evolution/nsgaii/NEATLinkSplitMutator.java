package org.javaneat.evolution.nsgaii;

import org.javaneat.genome.*;
import org.jnsgaii.operators.Mutator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkSplitMutator extends Mutator<NEATGenome> {
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        Random rng = ThreadLocalRandom.current();
        ConnectionGene replaced = object.getConnectionGeneList().get(rng.nextInt(object.getConnectionGeneList().size()));
        // Get a random connection to replace

        replaced.setEnabled(false); // Disable it

        NEATInnovation splitInnovation = object.getManager().acquireSplitInnovation(replaced.getFromNode(), replaced.getToNode());
        int neuronID = splitInnovation.getNeuronID();

        NeuronGene insertedNeuron = new NeuronGene(neuronID, splitInnovation.getInnovationID(), NeuronType.HIDDEN);
        ConnectionGene leftConnection = new ConnectionGene(replaced.getFromNode(), neuronID, object.getManager().acquireLinkInnovation(replaced.getFromNode(), neuronID).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);
        ConnectionGene rightConnection = new ConnectionGene(neuronID, replaced.getToNode(), object.getManager().acquireLinkInnovation(neuronID, replaced.getToNode()).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);

        object.getNeuronGeneList().add(insertedNeuron);
        object.getConnectionGeneList().add(leftConnection);
        object.getConnectionGeneList().add(rightConnection);

        return object;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Split Mutation Strength", "Link Split Mutation Probability"};
    }
}
