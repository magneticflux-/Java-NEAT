package org.javaneat.evolution.nsgaii;

import org.apache.commons.math3.util.FastMath;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.operators.speciation.DistanceSpeciatorEx;
import org.jnsgaii.operators.speciation.Species;
import org.jnsgaii.population.Population;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.AspectUser;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;
import org.jnsgaii.util.Utils;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by Mitchell Skaggs on 10/27/16.
 */

public class NEATSpeciatorEx extends DistanceSpeciatorEx<NEATGenome> {

    private int[] speciesSizes = new int[0];
    private int numTargetSpecies;

    @Override
    protected double getDistance(Individual<NEATGenome> first, Individual<NEATGenome> second) {
        return getGenomeDistance(startIndex, first, second);
    }

    static double getGenomeDistance(int startIndex, Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        // System.out.println("[DistanceCalc]");
        NEATGenome genome1 = individual.getIndividual(), genome2 = individual2.getIndividual();

        //genome1.sortGenes();
        //genome2.sortGenes();

        List<ConnectionGene> genome1Genes = genome1.getConnectionGeneList();
        List<ConnectionGene> genome2Genes = genome2.getConnectionGeneList();

        // int largestGenomeSize = FastMath.max(genome1Genes.size(), genome2Genes.size()); // Only for normalization
        int numDisjoint = 0;
        int numExcess = 0;
        int numMatched = 0;
        double weightDifference = 0;

        ListIterator<ConnectionGene> genome1Iterator = genome1Genes.listIterator();
        ListIterator<ConnectionGene> genome2Iterator = genome2Genes.listIterator();

        while (genome1Iterator.hasNext() || genome2Iterator.hasNext()) {
            ConnectionGene currentGenome1Gene = null, currentGenome2Gene = null;
            if (genome1Iterator.hasNext())
                currentGenome1Gene = genome1Iterator.next();
            if (genome2Iterator.hasNext())
                currentGenome2Gene = genome2Iterator.next();

            assert currentGenome1Gene != null || currentGenome2Gene != null;

            if (currentGenome1Gene != null && currentGenome2Gene != null) {

                if (currentGenome1Gene.getInnovationID() == currentGenome2Gene.getInnovationID()) {
                    numMatched++;
                    weightDifference += FastMath.abs(currentGenome1Gene.getWeight() - currentGenome2Gene.getWeight());
                } else if (currentGenome1Gene.getInnovationID() > currentGenome2Gene.getInnovationID()) {
                    numDisjoint++;
                    genome1Iterator.previous();
                } else if (currentGenome1Gene.getInnovationID() < currentGenome2Gene.getInnovationID()) {
                    numDisjoint++;
                    genome1Iterator.previous();
                }

            } else if (currentGenome1Gene != null) { //currentGenome1Gene != null && currentGenome2Gene == null
                numExcess++;
            } else { //currentGenome1Gene == null && currentGenome2Gene != null
                numExcess++;
            }
        }
        double disjointGeneCoefficient = (individual.aspects[startIndex + 1] + individual2.aspects[startIndex + 1]) / 2;
        double excessGeneCoefficient = (individual.aspects[startIndex + 2] + individual2.aspects[startIndex + 2]) / 2;

        return (disjointGeneCoefficient * numDisjoint) + (excessGeneCoefficient * numExcess) + (numMatched == 0 ? 0 : (weightDifference / numMatched));
    }

    @Override
    protected double getMaxDistance(Individual<NEATGenome> first, Individual<NEATGenome> second) {
        return (first.aspects[startIndex] + second.aspects[startIndex]) / 2d;
    }

    @Override
    public Set<Species> getSpecies(Population<NEATGenome> oldPopulation, List<Individual<NEATGenome>> newPopulation, long currentSpeciesID) {
        Set<Species> species = super.getSpecies(oldPopulation, newPopulation, currentSpeciesID);
        speciesSizes = species.stream().mapToInt(s -> s.getIndividualIDs().size()).toArray();
        return species;
    }

    @Override
    public int requestAspectLocation(int startIndex) {
        super.requestAspectLocation(startIndex);
        return 3;
    }

    @Override
    public void modifyAspects(double[] aspects, Random r) {
        //AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex, r, 0, Double.POSITIVE_INFINITY);

        int multiplier = 0;
        if (speciesSizes.length > numTargetSpecies)
            multiplier = 1;
        else if (speciesSizes.length < numTargetSpecies)
            multiplier = -1;

        aspects[startIndex] = aspects[startIndex] * (1 + (aspectModificationArray[startIndex * 2] * multiplier * r.nextDouble()));
        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex + 1, r, 0, Double.POSITIVE_INFINITY);
        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex + 2, r, 0, Double.POSITIVE_INFINITY);
    }

    @Override
    public void updateProperties(Properties properties) {
        super.updateProperties(properties);
        numTargetSpecies = properties.getInt(NEATIntKey.TARGET_SPECIES);
    }

    @Override
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[]{NEATIntKey.TARGET_SPECIES});
    }

    @Override
    public String[] getAspectDescriptions() {
        return new String[]{"Max Mating Distance", "Disjoint Gene Coefficient", "Excess Gene Coefficient"};
    }
}
