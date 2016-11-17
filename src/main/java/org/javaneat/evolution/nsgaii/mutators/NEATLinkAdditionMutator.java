package org.javaneat.evolution.nsgaii.mutators;

import org.javaneat.evolution.NEATInnovationMap;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.javaneat.genome.NEATInnovation;
import org.javaneat.genome.NeuronGene;
import org.jnsgaii.operators.Mutator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Mitchell Skaggs on 3/24/2016.
 */
public class NEATLinkAdditionMutator extends Mutator<NEATGenome> {

    private final NEATInnovationMap neatInnovationMap;

    public NEATLinkAdditionMutator(NEATInnovationMap neatInnovationMap) {
        this.neatInnovationMap = neatInnovationMap;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Link Addition Mutation Strength", "Link Addition Mutation Probability"};
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    @Override
    protected NEATGenome mutate(NEATGenome object, double mutationStrength, double mutationProbability) {
        NEATGenome newObject = object.copy();

        //log.info("Mutating " + newObject);

        List<PotentialLink> potentialLinks = new ArrayList<>();
        for (NeuronGene neuronGene1 : newObject.getNeuronGeneList()) { // Add everything
            potentialLinks.addAll(newObject.getNeuronGeneList().stream()
                    .map(neuronGene2 -> new PotentialLink(neuronGene1.getNeuronID(), neuronGene2.getNeuronID())).collect(Collectors.toList()));
        }
        potentialLinks.removeIf(possibleLink -> possibleLink.toNode < newObject.getOutputOffset()); // Input or bias

        //log.info("Found " + potentialLinks.size() + " possible links...");

        if (potentialLinks.size() > 0) {
            PotentialLink chosenLink = potentialLinks.get(ThreadLocalRandom.current().nextInt(potentialLinks.size()));

            NEATInnovation linkInnovation = neatInnovationMap.acquireLinkInnovation(chosenLink.fromNode, chosenLink.toNode);

            ConnectionGene gene = new ConnectionGene(chosenLink.fromNode, chosenLink.toNode, linkInnovation.getInnovationID(), Mutator.mutate(0, ThreadLocalRandom.current(), mutationStrength), true);

            //log.info("Created ConnectionGene " + gene);

            /*
            if (newObject.getConnectionGeneList().stream().anyMatch(connectionGene -> connectionGene.getInnovationID() == gene.getInnovationID())) {
                throw new IllegalStateException("Tried to add duplicate gene! Gene: " + gene + " Genome: " + newObject);
            }
            */

            newObject.getConnectionGeneList().add(gene);
            newObject.sortGenes();
        }

        return newObject;
    }

    private class PotentialLink {
        private final long fromNode;
        private final long toNode;

        public PotentialLink(long fromNode, long toNode) {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PotentialLink that = (PotentialLink) o;

            if (fromNode != that.fromNode) return false;
            return toNode == that.toNode;

        }

        @Override
        public int hashCode() {
            int result = (int) (fromNode ^ (fromNode >>> 32));
            result = 31 * result + (int) (toNode ^ (toNode >>> 32));
            return result;
        }
    }
}
