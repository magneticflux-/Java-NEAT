package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.genome.*;
import org.jnsgaii.operators.Mutator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkSplitMutator extends Mutator<NEATGenome> {

    private final NEATInnovationMap neatInnovationMap;

    public NEATLinkSplitMutator(NEATInnovationMap neatInnovationMap) {
        this.neatInnovationMap = neatInnovationMap;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Split Mutation Strength", "Link Split Mutation Probability"};
    }

    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        //log.info("Mutating " + newObject);

        if (newObject.getConnectionGeneList().size() < 1) {
            throw new IllegalStateException("Empty gene list!\n" + "Genome: " + newObject);
        }

        List<ConnectionGene> possibleGenes = newObject.getConnectionGeneList().stream().filter(ConnectionGene::getEnabled).collect(Collectors.toList());
        if (possibleGenes.size() > 0) {
            ConnectionGene replaced = possibleGenes.get(ThreadLocalRandom.current().nextInt(possibleGenes.size()));
            // Get a random connection to replace

            replaced.setEnabled(false); // Disable it

            NEATInnovation splitInnovation = neatInnovationMap.acquireSplitInnovation(replaced.getFromNode(), replaced.getToNode());
            int neuronID = splitInnovation.getNeuronID();

            NeuronGene insertedNeuron = new NeuronGene(neuronID, splitInnovation.getInnovationID(), NeuronType.HIDDEN);
            ConnectionGene leftConnection = new ConnectionGene(replaced.getFromNode(), neuronID, neatInnovationMap.acquireLinkInnovation(replaced.getFromNode(), neuronID).getInnovationID(), 1, true);
            ConnectionGene rightConnection = new ConnectionGene(neuronID, replaced.getToNode(), neatInnovationMap.acquireLinkInnovation(neuronID, replaced.getToNode()).getInnovationID(), replaced.getWeight(), true);

        /*
        if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == leftConnection.getInnovationID())) {
            throw new IllegalStateException("Tried to add duplicate gene!\nLeft connection Gene: " + leftConnection + "\nGenome: " + newObject);
        }
        if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == rightConnection.getInnovationID())) {
            throw new IllegalStateException("Tried to add duplicate gene!\nRight connection Gene: " + rightConnection + "\nGenome: " + newObject);
        }
        */
        /*
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
        */
            newObject.getNeuronGeneList().add(insertedNeuron);
            newObject.getConnectionGeneList().add(leftConnection);
            newObject.getConnectionGeneList().add(rightConnection);

            newObject.sortGenes();
        }
        return newObject;
    }
}
