package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronType;
import org.jnsgaii.operators.Mutator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkAdditionMutator extends Mutator<NEATGenome> {
    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();
        newObject.marioBrosData = null;

        List<PossibleLink> possibleLinks = new ArrayList<>();
            // WARNING: ***Lambda singularity approaching***
            newObject.getNeuronGeneList().stream().forEach( // For every neuron
                fromNode -> possibleLinks.addAll( // Add all of the possible links that are...
                        newObject.getNeuronGeneList().stream().filter(
                                toNode -> toNode.getNeuronType() == NeuronType.HIDDEN || toNode.getNeuronType() == NeuronType.OUTPUT).filter( // From a hidden or output neuron
                                toNode -> newObject.getConnectionGeneList().stream().noneMatch(
                                        connectionGene -> connectionGene.getFromNode() == fromNode.getNeuronID() && connectionGene.getToNode() == toNode.getNeuronID())).map( // And no connectionGenes already link
                                toNode -> new PossibleLink(fromNode.getNeuronID(), toNode.getNeuronID())).collect(Collectors.toList()))); // Add them to a small list and add them all to the list

            if (possibleLinks.size() > 0) {
                PossibleLink chosenLink = possibleLinks.get(ThreadLocalRandom.current().nextInt(possibleLinks.size()));

                ConnectionGene gene = new ConnectionGene(chosenLink.fromNode, chosenLink.toNode, newObject.getManager().acquireLinkInnovation(chosenLink.fromNode, chosenLink.toNode).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);

                if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == gene.getInnovationID())) {
                    throw new IllegalStateException("Tried to add duplicate gene! Gene: " + gene + " Genome: " + newObject);
                }

                newObject.getConnectionGeneList().add(gene);
            }

        newObject.sortGenes();
        return newObject;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Addition Mutation Strength", "Link Addition Mutation Probability"};
    }

    private class PossibleLink {
        private final int fromNode;
        private final int toNode;

        public PossibleLink(int fromNode, int toNode) {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PossibleLink that = (PossibleLink) o;

            if (fromNode != that.fromNode) return false;
            return toNode == that.toNode;

        }

        @Override
        public int hashCode() {
            int result = fromNode;
            result = 31 * result + toNode;
            return result;
        }
    }
}
