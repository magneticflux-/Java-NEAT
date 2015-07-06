package com.javaneat.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.CandidateFactory;

import com.javaneat.genotype.NEATGenotype;

public class NEATGenotypeFactory implements CandidateFactory<NEATGenotype>
{
	NEATGenomeManager	manager;

	public NEATGenotypeFactory(NEATGenomeManager manager)
	{
		this.manager = manager;
	}

	public List<NEATGenotype> generateInitialPopulation(int populationSize, Random rng)
	{
		List<NEATGenotype> population = new ArrayList<NEATGenotype>(populationSize);
		for (int i = 0; i < populationSize; i++)
		{
			population.add(new NEATGenotype(rng, this.manager));
		}
		return population;
	}

	public List<NEATGenotype> generateInitialPopulation(int populationSize, Collection<NEATGenotype> seedCandidates, Random rng)
	{
		List<NEATGenotype> population = new ArrayList<NEATGenotype>(populationSize);
		if (seedCandidates.size() <= populationSize)
		{
			population.addAll(seedCandidates);
			for (int i = 0; i < populationSize - seedCandidates.size(); i++)
			{
				population.add(new NEATGenotype(rng, this.manager));
			}
		}
		else
		{
			Iterator<NEATGenotype> iter = seedCandidates.iterator();
			for (int i = 0; i < populationSize; i++)
			{
				population.add(iter.next());
			}
		}
		assert population.size() == populationSize;
		return population;
	}

	public NEATGenotype generateRandomCandidate(Random rng)
	{
		NEATGenotype genome = new NEATGenotype(rng, this.manager);
		return genome;
	}
}