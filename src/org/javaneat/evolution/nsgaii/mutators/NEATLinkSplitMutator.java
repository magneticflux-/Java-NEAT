package org.javaneat.evolution.nsgaii.mutators;

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
        NEATGenome newObject = object.copy();
        newObject.marioBrosData = null;

        Random r = ThreadLocalRandom.current();
        ConnectionGene replaced = newObject.getConnectionGeneList().get(r.nextInt(newObject.getConnectionGeneList().size()));
        // Get a random connection to replace

        replaced.setEnabled(false); // Disable it

        NEATInnovation splitInnovation = newObject.getManager().acquireSplitInnovation(replaced.getFromNode(), replaced.getToNode());
        int neuronID = splitInnovation.getNeuronID();

        NeuronGene insertedNeuron = new NeuronGene(neuronID, splitInnovation.getInnovationID(), NeuronType.HIDDEN);
        ConnectionGene leftConnection = new ConnectionGene(replaced.getFromNode(), neuronID, newObject.getManager().acquireLinkInnovation(replaced.getFromNode(), neuronID).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);
        ConnectionGene rightConnection = new ConnectionGene(neuronID, replaced.getToNode(), newObject.getManager().acquireLinkInnovation(neuronID, replaced.getToNode()).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);

        newObject.getNeuronGeneList().add(insertedNeuron);
        newObject.getConnectionGeneList().add(leftConnection);
        newObject.getConnectionGeneList().add(rightConnection);

        newObject.sortGenes();
        return newObject;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Split Mutation Strength", "Link Split Mutation Probability"};
    }
}
