package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NEATInnovation;
import org.javaneat.genome.NeuronGene;
import org.javaneat.genome.NeuronType;
import org.jnsgaii.operators.Mutator;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkSplitMutator extends Mutator<NEATGenome> {

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Split Mutation Strength", "Link Split Mutation Probability"};
    }

    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        //log.info("Mutating " + newObject);

        Random r = ThreadLocalRandom.current();
        if (newObject.getConnectionGeneList().size() < 1) {
            throw new IllegalStateException("Empty gene list!\n" + "Genome: " + newObject);
        }

        List<ConnectionGene> possibleGenes = newObject.getConnectionGeneList().stream().filter(ConnectionGene::getEnabled).collect(Collectors.toList());
        ConnectionGene replaced = possibleGenes.get(r.nextInt(possibleGenes.size()));
        // Get a random connection to replace

        replaced.setEnabled(false); // Disable it

        NEATInnovation splitInnovation = newObject.getManager().acquireSplitInnovation(replaced.getFromNode(), replaced.getToNode());
        int neuronID = splitInnovation.getNeuronID();

        NeuronGene insertedNeuron = new NeuronGene(neuronID, splitInnovation.getInnovationID(), NeuronType.HIDDEN);
        ConnectionGene leftConnection = new ConnectionGene(replaced.getFromNode(), neuronID, newObject.getManager().acquireLinkInnovation(replaced.getFromNode(), neuronID).getInnovationID(), 1, true);
        ConnectionGene rightConnection = new ConnectionGene(neuronID, replaced.getToNode(), newObject.getManager().acquireLinkInnovation(neuronID, replaced.getToNode()).getInnovationID(), replaced.getWeight(), true);

/*
        if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == leftConnection.getInnovationID())) {
            throw new IllegalStateException("Tried to add duplicate gene!\nLeft connection Gene: " + leftConnection + "\nGenome: " + newObject);
        }
        if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == rightConnection.getInnovationID())) {
            throw new IllegalStateException("Tried to add duplicate gene!\nRight connection Gene: " + rightConnection + "\nGenome: " + newObject);
        }
        */

        if (newObject.getNeuronGeneList().contains(insertedNeuron)
                || newObject.getConnectionGeneList().contains(leftConnection)
                || newObject.getConnectionGeneList().contains(rightConnection)) {
            System.err.println("Split of\n\t"
                    + replaced
                    + "\nduplicated a gene\n"
                    + insertedNeuron + leftConnection + rightConnection
                    + "\non "
                    + newObject);
        } else {
            newObject.getNeuronGeneList().add(insertedNeuron);
            newObject.getConnectionGeneList().add(leftConnection);
            newObject.getConnectionGeneList().add(rightConnection);
        }

        newObject.sortGenes();
        newObject.verifyGenome();
        return newObject;
    }
}
