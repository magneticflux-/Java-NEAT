package com.javaneat.evolution;

import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
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
		SelectionStrategy<Object> selectionStrategy = new TournamentSelection(new Probability(1));
		Random rng = new Random(0);
		NEATGenomeManager manager = new NEATGenomeManager(2, 1);

		for (int i = 0; i < 10; i++)
		{
			NEATGenotype genotype = new NEATGenotype(rng, manager);
			System.out.println(genotype);
		}

		CandidateFactory<NEATGenotype> candidateFactory = null;
		EvolutionaryOperator<NEATGenotype> evolutionScheme = null;
		FitnessEvaluator<NEATGenotype> fitnessEvaluator = null;

		GenerationalEvolutionEngine<NEATGenotype> ge = new GenerationalEvolutionEngine<NEATGenotype>(candidateFactory, evolutionScheme, fitnessEvaluator,
				selectionStrategy, rng);
	}
}