package org.javaneat.evolution.nsgaii;

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
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        List<PossibleLink> possibleLinks = new ArrayList<>();

        object.getNeuronGeneList().stream().forEach( // For every neuron
                fromNode -> possibleLinks.addAll(
                        object.getNeuronGeneList().parallelStream().filter(
                                toNode -> toNode.getNeuronType() == NeuronType.HIDDEN || toNode.getNeuronType() == NeuronType.OUTPUT).filter( // For each hidden or output neuron
                                toNode -> object.getConnectionGeneList().parallelStream().noneMatch(
                                        connectionGene -> connectionGene.getFromNode() == fromNode.getNeuronID() && connectionGene.getToNode() == toNode.getNeuronID())).map( // That no connectionGenes already inhabit
                                toNode -> new PossibleLink(fromNode.getNeuronID(), toNode.getNeuronID())).collect(Collectors.toList()))); // Add collect them into a set and add them all

        PossibleLink chosenLink = possibleLinks.get(ThreadLocalRandom.current().nextInt(possibleLinks.size()));

        ConnectionGene gene = new ConnectionGene(chosenLink.fromNode, chosenLink.toNode, object.getManager().acquireLinkInnovation(chosenLink.fromNode, chosenLink.toNode).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);

        object.getConnectionGeneList().add(gene);

        return null;
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
