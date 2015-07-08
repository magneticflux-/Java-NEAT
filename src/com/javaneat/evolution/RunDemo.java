package com.javaneat.evolution;

import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;

import com.javaneat.genome.NEATGenome;

public class RunDemo
{
	public static void main(String[] args)
	{
		SelectionStrategy<Object> selectionStrategy = new TournamentSelection(Probability.ONE);
		Random rng = new Random(0);

		final int numInputs = 2;
		final int numOutputs = 2;
		final int populationSize = 100;
		final double disjointGeneCoefficient = 2;
		final double excessGeneCoefficient = 2;
		final double weightDifferenceCoefficient = 1;
		final int speciesTarget = 10;
		final int speciesStagnantTimeLimit = 20;
		final double speciesCutoff = 4;
		final double speciesCutoffDelta = 0.3;
		final double mutationWeightProb = 0.5;
		final double mutationAddLinkProb = 0.1;
		final double mutationAddNodeProb = 0.05;
		final double mutationWeightRange = 1;
		NEATGenomeManager manager = new NEATGenomeManager(numInputs, numOutputs, disjointGeneCoefficient, excessGeneCoefficient, weightDifferenceCoefficient,
				speciesTarget, speciesCutoff, speciesCutoffDelta, populationSize, speciesStagnantTimeLimit, mutationWeightProb, mutationAddLinkProb,
				mutationAddNodeProb, mutationWeightRange);

		CandidateFactory<NEATGenome> candidateFactory = new NEATGenotypeFactory(manager);
		EvolutionaryOperator<NEATGenome> evolutionScheme = new NEATEvolutionaryOperator(manager);
		FitnessEvaluator<NEATGenome> fitnessEvaluator = new FitnessEvaluator<NEATGenome>()
		{

			public double getFitness(NEATGenome candidate, List<? extends NEATGenome> population)
			{
				return candidate.getConnectionGeneList().size() + candidate.getNeuronGeneList().size();
			}

			public boolean isNatural()
			{
				return true;
			}
		};

		GenerationalEvolutionEngine<NEATGenome> ge = new GenerationalEvolutionEngine<NEATGenome>(candidateFactory, evolutionScheme, fitnessEvaluator,
				selectionStrategy, rng);
	}
}