package com.javaneat.evolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import com.javaneat.genome.ConnectionGene;
import com.javaneat.genome.NEATGenome;
import com.javaneat.genome.NEATSpecies;
import com.javaneat.genome.NeuronGene;
import com.javaneat.genome.NeuronType;

public class NEATEvolutionaryOperator implements EvolutionaryOperator<NEATGenome>
{
	private NEATGenomeManager	manager;
	private List<NEATSpecies>	speciesList;

	public NEATEvolutionaryOperator(NEATGenomeManager manager)
	{
		this.manager = manager;
		this.speciesList = new ArrayList<NEATSpecies>(manager.getSpeciesTarget());
	}

	private NeuronGene getNeuron(int neuronID, NEATGenome best, NEATGenome notBest, Random rng)
	{
		NeuronGene output = null;
		output = best.getNeuronGene(neuronID);
		if (output == null) output = notBest.getNeuronGene(neuronID);
		return output;
	}

	public NEATGenome mate(NEATGenome alpha, NEATGenome beta, Random rng)
	{
		NEATGenome best;
		NEATGenome notBest;
		if (alpha.getScore() == beta.getScore())
		{
			best = rng.nextBoolean() ? alpha : beta;
			notBest = best == alpha ? beta : alpha;
		}
		else
		{
			best = alpha.getScore() > beta.getScore() ? alpha : beta;
			notBest = alpha.getScore() < beta.getScore() ? alpha : beta;
		}

		List<ConnectionGene> alphaGenes = alpha.getConnectionGeneList();
		List<ConnectionGene> betaGenes = beta.getConnectionGeneList();
		alpha.sortGenes();
		beta.sortGenes();

		int alphaGeneIndex = 0;
		int betaGeneIndex = 0;

		List<ConnectionGene> offspringConnectionGenes = new ArrayList<ConnectionGene>();
		List<NeuronGene> offspringNeuronGenes = new ArrayList<NeuronGene>();
		Set<Integer> addedNeuronIDs = new HashSet<Integer>();

		for (int i = 0; i < 1 + this.manager.getNumInputs() + this.manager.getNumOutputs(); i++) // Acquire required neurons
		{
			offspringNeuronGenes.add(alpha.getNeuronGene(i));
			addedNeuronIDs.add(i);
		}

		while (alphaGeneIndex < alphaGenes.size() || betaGeneIndex < betaGenes.size())
		{
			ConnectionGene selectedGene = null;

			ConnectionGene alphaGene = null;
			if (alphaGeneIndex < alphaGenes.size()) alphaGene = alphaGenes.get(alphaGeneIndex);
			ConnectionGene betaGene = null;
			if (betaGeneIndex < betaGenes.size()) betaGene = betaGenes.get(betaGeneIndex);

			if (alphaGene == null && betaGene != null)
			{
				if (best == beta)
				{
					selectedGene = betaGene;
				}
				betaGeneIndex++;
			}
			else if (alphaGene != null && betaGene == null)
			{
				if (alpha == best)
				{
					selectedGene = alphaGene;
				}
				alphaGeneIndex++;
			}
			else if (alphaGene.getInnovationID() > betaGene.getInnovationID())
			{
				if (best == beta)
				{
					selectedGene = betaGene;
				}
				betaGeneIndex++;
			}
			else if (alphaGene.getInnovationID() < betaGene.getInnovationID())
			{
				if (best == alpha)
				{
					selectedGene = alphaGene;
				}
				alphaGeneIndex++;
			}
			else if (alphaGene.getInnovationID() == betaGene.getInnovationID())
			{
				if (rng.nextBoolean())
				{
					selectedGene = alphaGene;
				}
				else
				{
					selectedGene = betaGene;
				}
				alphaGeneIndex++;
				betaGeneIndex++;
			}

			if (selectedGene != null)
			{
				selectedGene = new ConnectionGene(selectedGene.getFromNode(), selectedGene.getToNode(), selectedGene.getInnovationID(),
						selectedGene.getWeight(), selectedGene.getEnabled() || rng.nextInt(4) == 0); // 75% chance to be disabled if the parent's gene was
																										// disabled
				if (offspringConnectionGenes.size() == 0)
				{
					offspringConnectionGenes.add(selectedGene);
				}
				else
				{
					if (offspringConnectionGenes.get(offspringConnectionGenes.size() - 1).getInnovationID() != selectedGene.getInnovationID())
					{
						offspringConnectionGenes.add(selectedGene);
					}
					else
					{
						throw new IllegalStateException("Previous gene was duplicate, this should not happen.");
					}
				}

				if (!addedNeuronIDs.contains(selectedGene.getFromNode()))
				{
					offspringNeuronGenes.add(this.getNeuron(selectedGene.getFromNode(), best, notBest, rng));
					addedNeuronIDs.add(selectedGene.getFromNode());
				}
				if (!addedNeuronIDs.contains(selectedGene.getToNode()))
				{
					offspringNeuronGenes.add(this.getNeuron(selectedGene.getToNode(), best, notBest, rng));
					addedNeuronIDs.add(selectedGene.getToNode());
				}
			}
		}

		NEATGenome offspring = new NEATGenome(offspringConnectionGenes, offspringNeuronGenes, this.manager);

		return offspring;
	}

	public List<NEATGenome> apply(List<NEATGenome> selectedCandidates, Random rng)
	{
		for (NEATSpecies species : this.speciesList) // Reset species and chose the closest to the leader in the next generation
		{
			species.getMembers().clear();
			double minDistance = Double.MAX_VALUE; // Just to be safe lol
			NEATGenome bestCandidate = null;
			for (NEATGenome genome : selectedCandidates)
			{
				double val = species.getGenomeDistance(species.getLeader(), genome);
				if (val < minDistance)
				{
					minDistance = val;
					bestCandidate = genome;
				}
			}
			assert bestCandidate != null : "Failed to choose a successor :(";
			species.setLeader(bestCandidate);
			species.attemptAddMember(bestCandidate);
			selectedCandidates.remove(bestCandidate); // Prevent it from being chosen twice
		}

		for (NEATGenome genome : selectedCandidates) // Add genomes to the species
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
				this.speciesList.add(new NEATSpecies(genome)); // If it doesn't fit in any other genomes, make a new one
			}

		}

		this.manager.tweakSpeciesCutoff(this.speciesList.size() < this.manager.getSpeciesTarget()); // Adjust the number of species to the target

		List<NEATGenome> newCandidates = new ArrayList<NEATGenome>(selectedCandidates.size()); // The list of modified candidates to be output

		double totalAverageFitness = 0;
		for (NEATSpecies species : this.speciesList) // Adding the average species fitness from the new species
		{
			if (species.getTimesSinceLastImprovement() < this.manager.getSpeciesStagnantTimeLimit()) // Only include successful species in breeding
			{
				totalAverageFitness += species.getAverageFitness();
			}
		}

		for (NEATSpecies species : this.speciesList) // Generate the allotted offspring for each species, based on the average fitness proportion
		{
			if (species.getTimesSinceLastImprovement() < this.manager.getSpeciesStagnantTimeLimit()) // Only include successful species in breeding
			{
				int offspringAllotment = species.getOffspringAllotment(totalAverageFitness, this.manager.getPopulationSize());

				for (int i = 0; i < offspringAllotment; i++)
				{
					NEATGenome mom = species.getMembers().get(rng.nextInt(species.getMembers().size())); // Get two random genomes from the species
					NEATGenome dad = species.getMembers().get(rng.nextInt(species.getMembers().size()));
					NEATGenome offspring = this.mate(mom, dad, rng);
					offspring = this.mutate(offspring, rng);
					newCandidates.add(offspring);
				}
			}
		}

		for (NEATSpecies species : this.speciesList)
			System.out.println(species);

		assert newCandidates.size() == this.manager.getPopulationSize() : "Apparently there was a rounding error, and the size of the population was less than usual.";
		return newCandidates;
	}

	private boolean linkAlreadyExists(NEATGenome alpha, int neuronFrom, int neuronTo)
	{
		for (ConnectionGene gene : alpha.getConnectionGeneList())
		{
			if (gene.getFromNode() == neuronFrom && gene.getToNode() == neuronTo) return true;
		}
		return false;
	}

	public NEATGenome mutate(NEATGenome alpha, Random rng)
	{
		NEATGenome mutated = new NEATGenome(alpha);

		for (ConnectionGene gene : mutated.getConnectionGeneList()) // Perturb weights
		{
			if (rng.nextDouble() < this.manager.getMutationWeightProb())
			{
				gene.setWeight(gene.getWeight() + (rng.nextDouble() * 2 - 1) * this.manager.getMutationWeightRange());
			}
		}

		if (rng.nextDouble() < this.manager.getMutationAddLinkProb()) // Add a link
		{
			int neuronFrom = 0;
			int neuronTo = 0;
			int tries = 0;
			do
			{
				neuronFrom = rng.nextInt(mutated.getNeuronGeneList().size());
				neuronTo = rng.nextInt(mutated.getNeuronGeneList().size() - this.manager.getOutputOffset()) + this.manager.getOutputOffset();
				// Exclude input and bias nodes, they don't make sense
			}
			while (this.linkAlreadyExists(mutated, neuronFrom, neuronTo) && (tries++ < 10));

			if (!this.linkAlreadyExists(mutated, neuronFrom, neuronTo))
				mutated.getConnectionGeneList().add(
						new ConnectionGene(neuronFrom, neuronTo, this.manager.aquireLinkInnovation(neuronFrom, neuronTo).getInnovationID(), 1, true));
		}

		if (rng.nextDouble() < this.manager.getMutationAddNodeProb()) // Split a link
		{
			ConnectionGene replaced = mutated.getConnectionGeneList().get(rng.nextInt(mutated.getConnectionGeneList().size()));
			// Get a random connection to replace

			replaced.setEnabled(false); // Disable it

			int neuronID = this.manager.getNewNeuronID();
			NeuronGene insertedNeuron = new NeuronGene(neuronID, this.manager.aquireNodeInnovation(neuronID).getInnovationID(), NeuronType.HIDDEN);
			ConnectionGene leftConnection = new ConnectionGene(replaced.getFromNode(), neuronID, this.manager.aquireLinkInnovation(replaced.getFromNode(),
					neuronID).getInnovationID(), 1, true);
			ConnectionGene rightConnection = new ConnectionGene(neuronID, replaced.getToNode(), this.manager.aquireLinkInnovation(neuronID,
					replaced.getToNode()).getInnovationID(), 1, true);

			mutated.getNeuronGeneList().add(insertedNeuron);
			mutated.getConnectionGeneList().add(leftConnection);
			mutated.getConnectionGeneList().add(rightConnection);
		}

		mutated.sortGenes();

		return mutated;
	}
}