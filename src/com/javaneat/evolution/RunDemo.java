package com.javaneat.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;

import com.javaneat.genotype.NEATGenotype;

public class RunDemo
{
	public static void main(String[] args)
	{
		SelectionStrategy<Object> selectionStrategy = new TournamentSelection(Probability.ONE);
		Random rng = new Random(0);

		final int numInputs = 2;
		final int numOutputs = 2;
		final double disjointGeneCoefficient = 2;
		final double excessGeneCoefficient = 2;
		final double weightDifferenceCoefficient = 1;
		final int speciesTarget = 10;
		final double speciesCutoff = 4;
		final double speciesCutoffDelta = 0.3;
		NEATGenomeManager manager = new NEATGenomeManager(numInputs, numOutputs, disjointGeneCoefficient, excessGeneCoefficient, weightDifferenceCoefficient,
				speciesTarget, speciesCutoff, speciesCutoffDelta);

		List<NEATGenotype> genomes = new ArrayList<NEATGenotype>();
		for (int i = 0; i < 10; i++)
		{
			NEATGenotype genotype = new NEATGenotype(rng, manager);
			genomes.add(genotype);
			System.out.println(genotype);
		}

		NEATEvolutionaryOperator operator = new NEATEvolutionaryOperator(manager);
		List<NEATGenotype> alteredGenomes = operator.apply(genomes, rng);
		for (NEATGenotype genome : alteredGenomes)
		{
			System.out.println(genome);
		}
		// System.out.println("Distance between 1 and 2: " + operator.getGenomeDistance(genomes.get(0), genomes.get(1)));

		CandidateFactory<NEATGenotype> candidateFactory = new NEATGenotypeFactory(manager);
		EvolutionaryOperator<NEATGenotype> evolutionScheme = null;
		FitnessEvaluator<NEATGenotype> fitnessEvaluator = null;

		GenerationalEvolutionEngine<NEATGenotype> ge = new GenerationalEvolutionEngine<NEATGenotype>(candidateFactory, evolutionScheme, fitnessEvaluator,
				selectionStrategy, rng);
	}
}