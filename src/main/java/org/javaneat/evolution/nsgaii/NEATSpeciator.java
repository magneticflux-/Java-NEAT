package org.javaneat.evolution.nsgaii;

import org.apache.commons.math3.util.FastMath;
import org.javaneat.evolution.nsgaii.keys.NEATIntKey;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;
import org.jnsgaii.multiobjective.population.FrontedIndividual;
import org.jnsgaii.observation.EvolutionObserver;
import org.jnsgaii.operators.speciation.Speciator;
import org.jnsgaii.population.PopulationData;
import org.jnsgaii.population.individual.Individual;
import org.jnsgaii.properties.AspectUser;
import org.jnsgaii.properties.Key;
import org.jnsgaii.properties.Properties;
import org.jnsgaii.util.Utils;
import org.jnsgaii.visualization.TabbedVisualizationWindow;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import edu.uci.ics.jung.algorithms.cluster.BicomponentClusterer;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * Created by Mitchell Skaggs on 3/22/2016.
 */
public class NEATSpeciator extends Speciator<NEATGenome> implements EvolutionObserver<NEATGenome> {

    private int[] speciesSizes = new int[0];
    private final TabbedVisualizationWindow.StatisticFunction<NEATGenome> numSpeciesStatisticFunction = new TabbedVisualizationWindow.StatisticFunction<NEATGenome>() {
        @Override
        public String getName() {
            return "Number of Species (Bicomponent Clusters)";
        }

        @Override
        public double[] apply(PopulationData<NEATGenome> populationData) {
            return new double[]{speciesSizes.length};
        }
    };
    private final TabbedVisualizationWindow.StatisticFunction<NEATGenome> speciesSizeStatisticFunction = new TabbedVisualizationWindow.StatisticFunction<NEATGenome>() {
        @Override
        public String getName() {
            return "Size of Species (Bicomponent Clusters)";
        }

        @Override
        public double[] apply(PopulationData<NEATGenome> populationData) {
            return Arrays.stream(speciesSizes).mapToDouble(value -> value).toArray();
        }
    };
    private int numTargetSpecies;

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
    public void updateProperties(Properties properties) {
        super.updateProperties(properties);
        numTargetSpecies = properties.getInt(NEATIntKey.TARGET_SPECIES);
    }

    @Override
    public Key[] requestProperties() {
        return Utils.concat(super.requestProperties(), new Key[]{NEATIntKey.TARGET_SPECIES});
    }

    @Override
    public double getDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        return getGenomeDistance(individual, individual2);
    }

    @Override
    public void modifyAspects(double[] aspects, Random r) {
        //AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex, r, 0, Double.POSITIVE_INFINITY);

        int multiplier = 0;
        if (speciesSizes.length > numTargetSpecies)
            multiplier = 1;
        else if (speciesSizes.length < numTargetSpecies)
            multiplier = -1;

        aspects[startIndex] = aspects[startIndex] * (1 + (aspectModificationArray[startIndex * 2] * multiplier * ThreadLocalRandom.current().nextDouble()));
        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex + 1, r, 0, Double.POSITIVE_INFINITY);
        AspectUser.mutateAspect(aspectModificationArray, aspects, startIndex + 2, r, 0, Double.POSITIVE_INFINITY);
    }

    private double getGenomeDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
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
    protected double getMaxDistance(Individual<NEATGenome> individual, Individual<NEATGenome> individual2) {
        return (individual.aspects[startIndex] + individual2.aspects[startIndex]) / 2;
    }

    @Override
    public void update(PopulationData<NEATGenome> populationData) {
        UndirectedGraph<Integer, GeneticCompatibilityEdge> geneticGraph = new UndirectedSparseGraph<>();
        @SuppressWarnings("unchecked")
        List<FrontedIndividual<NEATGenome>> population = (List<FrontedIndividual<NEATGenome>>) populationData.getTruncatedPopulation().getPopulation();
        IntStream.range(0, population.size()).forEach(geneticGraph::addVertex);
        for (int outer = 0; outer < population.size(); outer++) {
            FrontedIndividual<NEATGenome> outerIndividual = population.get(outer);
            for (int inner = outer + 1; inner < population.size(); inner++) {
                FrontedIndividual<NEATGenome> innerIndividual = population.get(inner);
                if (this.apply(outerIndividual, innerIndividual)) {
                    geneticGraph.addEdge(new GeneticCompatibilityEdge(), outer, inner);
                }
            }
        }
        BicomponentClusterer<Integer, GeneticCompatibilityEdge> bicomponentClusterer = new BicomponentClusterer<>();
        Set<Set<Integer>> clusters = bicomponentClusterer.apply(geneticGraph);

        assert clusters != null;
        speciesSizes = clusters.stream().mapToInt(Set::size).toArray();
    }

    public TabbedVisualizationWindow.StatisticFunction<NEATGenome> getNumSpeciesStatisticFunction() {
        return numSpeciesStatisticFunction;
    }

    public TabbedVisualizationWindow.StatisticFunction<NEATGenome> getSpeciesSizeStatisticFunction() {
        return speciesSizeStatisticFunction;
    }

    private static class GeneticCompatibilityEdge {
    }
}
