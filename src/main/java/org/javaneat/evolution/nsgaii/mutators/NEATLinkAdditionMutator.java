package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NeuronGene;
import org.jnsgaii.operators.Mutator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class NEATLinkAdditionMutator extends Mutator<NEATGenome> {

    private static final Logger log = Logger.getLogger("NEATLinkAdditionMutator");

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Addition Mutation Strength", "Link Addition Mutation Probability"};
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        //log.info("Mutating " + newObject);

        List<PossibleLink> possibleLinks = new ArrayList<>();
        for (NeuronGene neuronGene1 : newObject.getNeuronGeneList()) { // Add everything
            possibleLinks.addAll(newObject.getNeuronGeneList().stream().map(neuronGene2 -> new PossibleLink(neuronGene1.getNeuronID(), neuronGene2.getNeuronID())).collect(Collectors.toList()));
        }
        possibleLinks.removeIf(possibleLink -> possibleLink.toNode < newObject.getManager().getOutputOffset() // Input or bias
                || newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> possibleLink.fromNode == connectionGene.getFromNode() && possibleLink.toNode == connectionGene.getToNode()));

        //log.info("Found " + possibleLinks.size() + " possible links...");

        if (possibleLinks.size() > 0) {
            PossibleLink chosenLink = possibleLinks.get(ThreadLocalRandom.current().nextInt(possibleLinks.size()));

            ConnectionGene gene = new ConnectionGene(chosenLink.fromNode, chosenLink.toNode, newObject.getManager().acquireLinkInnovation(chosenLink.fromNode, chosenLink.toNode).getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);

            //log.info("Created ConnectionGene " + gene);

            /*
            if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == gene.getInnovationID())) {
                throw new IllegalStateException("Tried to add duplicate gene! Gene: " + gene + " Genome: " + newObject);
            }
            */

            newObject.getConnectionGeneList().add(gene);
        }

        newObject.sortGenes();
        newObject.verifyGenome();
        return newObject;
    }

    private class PossibleLink {
        private final int fromNode;
        private final int toNode;

        public PossibleLink(int fromNode, int toNode) {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        @Override
        public int hashCode() {
            int result = fromNode;
            result = 31 * result + toNode;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PossibleLink that = (PossibleLink) o;

            if (fromNode != that.fromNode) return false;
            return toNode == that.toNode;

        }
    }
}
