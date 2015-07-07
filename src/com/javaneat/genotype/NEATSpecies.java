package com.javaneat.genotype;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

import com.javaneat.evolution.NEATGenomeManager;

public class NEATSpecies
{
	NEATGenotype		leader;
	NEATGenomeManager	manager;
	List<NEATGenotype>	members	= new ArrayList<NEATGenotype>();

	public NEATSpecies(final NEATGenotype leader)
	{
		this.leader = leader;
		this.manager = leader.getManager();
		this.attemptAddMember(leader);
	}

	public NEATGenotype getLeader()
	{
		return this.leader;
	}

	public boolean attemptAddMember(NEATGenotype genome)
	{
		if (this.isCompatible(genome))
		{
			this.members.add(genome);
			genome.setSpecies(this);
			return true;
		}
		return false;
	}

	public List<NEATGenotype> getMembers()
	{
		return this.members;
	}

	public void setLeader(final NEATGenotype leader)
	{
		this.leader = leader;
	}

	public boolean isCompatible(NEATGenotype genome)
	{
		return this.getGenomeDistance(this.leader, genome) < this.manager.getSpeciesCutoff();
	}

	public String toString()
	{
		return "NEATSpecies=[Leader:" + this.leader + ",Members:" + this.members + "]";
	}

	public double getGenomeDistance(NEATGenotype genome1, NEATGenotype genome2)
	{
		// System.out.println("[DistanceCalc]");
		genome1.sortGenes();
		genome2.sortGenes();

		List<ConnectionGene> genome1Genes = genome1.getConnectionGeneList();
		List<ConnectionGene> genome2Genes = genome2.getConnectionGeneList();

		int innovationCutoff;
		if (genome1Genes.get(genome1Genes.size() - 1).getInnovationID() > genome2Genes.get(genome2Genes.size() - 1).getInnovationID())
		{ // If the newest innovation is in genome 1, get the newest innovation from genome 2
			innovationCutoff = genome2Genes.get(genome2Genes.size() - 1).getInnovationID();
		}
		else
		{ // Else, use the newest innovation from genome 1
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
				if (genome2Genes.contains(gene))
				{
					// System.out.println("	Matched");
					numMatched++;
					weightDifference += FastMath.abs(gene.getWeight() - genome2Genes.get(genome2Genes.indexOf(gene)).getWeight());
				}
				else
				{
					// System.out.println("	Rejected");
					numDisjoint++;
				}
			}
			else
			// If it is past the length of the older genome
			{
				// System.out.println("	Above cutoff");
				numExcess++;
			}
		}

		// System.out.println("Genome2");
		for (ConnectionGene gene : genome2Genes)
		{
			// System.out.println("Gene: " + gene);
			if (gene.getInnovationID() <= innovationCutoff)
			{
				// System.out.println("	Below cutoff");
				if (genome2Genes.contains(gene))
				{
					// System.out.println("	Matched");
					// Matched genes have been found the first time
				}
				else
				{
					// System.out.println("	Rejected");
					numDisjoint++;
				}
			}
			else
			{
				// System.out.println("	Above cutoff");
				numExcess++;
			}
		}
		// System.out.println("[DistanceCalc]");
		return (this.manager.getDisjointGeneCoefficient() * numDisjoint) + (this.manager.getExcessGeneCoefficient() * numExcess)
				+ (numMatched == 0 ? 0 : (weightDifference / numMatched));
	}
}