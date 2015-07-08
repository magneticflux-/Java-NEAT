package com.javaneat.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.CandidateFactory;

import com.javaneat.genome.NEATGenome;

public class NEATGenotypeFactory implements CandidateFactory<NEATGenome>
{
	NEATGenomeManager	manager;

	public NEATGenotypeFactory(NEATGenomeManager manager)
	{
		this.manager = manager;
	}

	public List<NEATGenome> generateInitialPopulation(int populationSize, Random rng)
	{
		List<NEATGenome> population = new ArrayList<NEATGenome>(populationSize);
		for (int i = 0; i < populationSize; i++)
		{
			population.add(new NEATGenome(rng, this.manager));
		}
		return population;
	}

	public List<NEATGenome> generateInitialPopulation(int populationSize, Collection<NEATGenome> seedCandidates, Random rng)
	{
		List<NEATGenome> population = new ArrayList<NEATGenome>(populationSize);
		if (seedCandidates.size() <= populationSize)
		{
			population.addAll(seedCandidates);
			for (int i = 0; i < populationSize - seedCandidates.size(); i++)
			{
				population.add(new NEATGenome(rng, this.manager));
			}
		}
		else
		{
			Iterator<NEATGenome> iter = seedCandidates.iterator();
			for (int i = 0; i < populationSize; i++)
			{
				population.add(iter.next());
			}
		}
		assert population.size() == populationSize;
		return population;
	}

	public NEATGenome generateRandomCandidate(Random rng)
	{
		NEATGenome genome = new NEATGenome(rng, this.manager);
		return genome;
	}
}