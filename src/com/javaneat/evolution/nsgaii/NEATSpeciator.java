package com.javaneat.evolution.nsgaii;

import com.javaneat.genome.ConnectionGene;
import com.javaneat.genome.NEATGenome;
import org.apache.commons.math3.util.FastMath;
import org.skaggs.ec.operators.Speciator;
import org.skaggs.ec.population.individual.Individual;
import org.skaggs.ec.properties.AspectUser;
import org.skaggs.ec.properties.Key;
import org.skaggs.ec.properties.Properties;
import org.skaggs.ec.util.Utils;

import java.util.List;
import java.util.Random;

/**
 * Created by Mitchell on 3/22/2016.
 */
public class NEATSpeciator extends Speciator<NEATGenome> {

    @Override
    public int requestAspectLocation(int startIndex) {
        super.requestAspectLocation(startIndex);
        return 3;
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Max Mating Distance", "Disjoint Gene Coefficient", "Excess Gene Coefficient"};
    }

    @Override
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[0]);
    }

    @Override
    public void updateProperties(Properties properties) {
    }

    @Override
    protected double getDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        return getGenomeDistance(individual, individual2);
    }

    @Override
    public void modifyAspects(Individual<NEATGenome> individual, Random r) {
        double[] aspects = individual.aspects;

        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex, r, 0, Double.POSITIVE_INFINITY);
        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex + 1, r, 0, Double.POSITIVE_INFINITY);
        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex + 2, r, 0, Double.POSITIVE_INFINITY);
    }

    private double getGenomeDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        // System.out.println("[DistanceCalc]");
        NEATGenome genome1 = individual.getIndividual(), genome2 = individual2.getIndividual();

        genome1.sortGenes();
        genome2.sortGenes();

        List<ConnectionGene> genome1Genes = genome1.getConnectionGeneList();
        List<ConnectionGene> genome2Genes = genome2.getConnectionGeneList();

        int innovationCutoff;
        if (genome1Genes.get(genome1Genes.size() - 1).getInnovationID() > genome2Genes.get(genome2Genes.size() - 1).getInnovationID()) { // If the newest innovation is in genome 1, get the newest innovation from genome 2
            innovationCutoff = genome2Genes.get(genome2Genes.size() - 1).getInnovationID();
        } else { // Else, use the newest innovation from genome 1
            innovationCutoff = genome1Genes.get(genome1Genes.size() - 1).getInnovationID();
        }
        // System.out.println("Cutoff: " + innovationCutoff);

        // int largestGenomeSize = FastMath.max(genome1Genes.size(), genome2Genes.size()); // Only for normalization
        int numDisjoint = 0;
        int numExcess = 0;
        int numMatched = 0;
        double weightDifference = 0;

        // System.out.println("Genome1");
        for (ConnectionGene gene : genome1Genes) // ONLY CHECK MATCHING GENES FIRST TIME
        {
            // System.out.println("Gene: " + gene);
            if (gene.getInnovationID() <= innovationCutoff) // If it is below the disjoint - excess cutoff
            {
                // System.out.println("	Below cutoff");
                if (genome2Genes.contains(gene)) {
                    // System.out.println("	Matched");
                    numMatched++;
                    weightDifference += FastMath.abs(gene.getWeight() - genome2Genes.get(genome2Genes.indexOf(gene)).getWeight());
                } else {
                    // System.out.println("	Rejected");
                    numDisjoint++;
                }
            } else
            // If it is past the length of the older genome
            {
                // System.out.println("	Above cutoff");
                numExcess++;
            }
        }

        // System.out.println("Genome2");
        for (ConnectionGene gene : genome2Genes) {
            // System.out.println("Gene: " + gene);
            if (gene.getInnovationID() <= innovationCutoff) {
                // System.out.println("	Below cutoff");
                if (genome2Genes.contains(gene)) {
                    // System.out.println("	Matched");
                    // Matched genes have been found the first time
                } else {
                    // System.out.println("	Rejected");
                    numDisjoint++;
                }
            } else {
                // System.out.println("	Above cutoff");
                numExcess++;
            }
        }
        // System.out.println("[DistanceCalc]");
        double disjointGeneCoefficient = (individual.aspects[startIndex] + individual2.aspects[startIndex]) / 2;
        double excessGeneCoefficient = (individual.aspects[startIndex + 1] + individual2.aspects[startIndex + 1]) / 2;

        return (disjointGeneCoefficient * numDisjoint) + (excessGeneCoefficient * numExcess) + (numMatched == 0 ? 0 : (weightDifference / numMatched));
    }

    @Override
    protected double getMaxDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        return 0;
    }
}
