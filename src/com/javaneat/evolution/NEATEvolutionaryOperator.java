package com.javaneat.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import com.javaneat.genotype.NEATGenotype;
import com.javaneat.genotype.NEATSpecies;
import com.javaneat.genotype.NeuronGene;

public class NEATEvolutionaryOperator implements EvolutionaryOperator<NEATGenotype>
{
	private NEATGenomeManager	manager;
	private List<NEATSpecies>	speciesList;

	public NEATEvolutionaryOperator(NEATGenomeManager manager)
	{
		this.manager = manager;
		this.speciesList = new ArrayList<NEATSpecies>(manager.getSpeciesTarget());
	}

	private NeuronGene getNeuron(int neuronID, NEATGenotype alpha, NEATGenotype beta, Random rng)
	{
		if (rng.nextBoolean())
		{
			// NeuronGene output = alpha.get
		}
		else
		{
		}
		return null;
	}

	private NEATGenotype mate(NEATGenotype alpha, NEATGenotype beta)
	{

		return null;
	}

	public List<NEATGenotype> apply(List<NEATGenotype> selectedCandidates, Random rng)
	{
		for (NEATGenotype genome : selectedCandidates)
		{
			boolean added = false;
			for (NEATSpecies species : this.speciesList)
			{
				if (added = species.attemptAddMember(genome))
				{
					break;
				}
			}
			if (!added)
			{
				this.speciesList.add(new NEATSpecies(genome));
			}

		}

		this.manager.tweakSpeciesCutoff(this.speciesList.size() < this.manager.getSpeciesTarget());

		List<NEATGenotype> newCandidates = new ArrayList<NEATGenotype>(selectedCandidates.size());
		for (NEATSpecies species : this.speciesList)
		{
			for (NEATGenotype genome : species.getMembers())
			{
				NEATGenotype mom = species.getMembers().get(rng.nextInt(species.getMembers().size()));
			}
		}

		for (NEATSpecies species : this.speciesList)
			System.out.println(species);

		return null;
	}
}