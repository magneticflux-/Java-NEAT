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

        Random r = ThreadLocalRandom.current();
        if (newObject.getConnectionGeneList().size() < 1) {
            throw new IllegalStateException("Empty gene list!\n" + "Genome: " + newObject);
        }
        ConnectionGene replaced = newObject.getConnectionGeneList().get(r.nextInt(newObject.getConnectionGeneList().size()));
        // Get a random connection to replace

        replaced.setEnabled(false); // Disable it

        NEATInnovation splitInnovation = newObject.getManager().acquireSplitInnovation(replaced.getFromNode(), replaced.getToNode());
        int neuronID = splitInnovation.getNeuronID();

        NeuronGene insertedNeuron = new NeuronGene(neuronID, splitInnovation.getInnovationID(), NeuronType.HIDDEN);
        ConnectionGene leftConnection = new ConnectionGene(replaced.getFromNode(), neuronID, newObject.getManager().acquireLinkInnovation(replaced.getFromNode(), neuronID).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);
        ConnectionGene rightConnection = new ConnectionGene(neuronID, replaced.getToNode(), newObject.getManager().acquireLinkInnovation(neuronID, replaced.getToNode()).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);


        if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == leftConnection.getInnovationID())) {
            throw new IllegalStateException("Tried to add duplicate gene!\nLeft connection Gene: " + leftConnection + "\nGenome: " + newObject);
        }
        if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == rightConnection.getInnovationID())) {
            throw new IllegalStateException("Tried to add duplicate gene!\nRight connection Gene: " + rightConnection + "\nGenome: " + newObject);
        }

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
