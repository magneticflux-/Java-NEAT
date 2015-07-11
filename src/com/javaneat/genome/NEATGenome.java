package com.javaneat.genome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.javaneat.evolution.NEATGenomeManager;

public class NEATGenome
// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
{
	private final List<ConnectionGene>	connectionGeneList;
	private final NEATGenomeManager		manager;
	private final List<NeuronGene>		neuronGeneList;
	private NEATSpecies					species;
	private double						score;

	@SuppressWarnings("unused")
	private NEATGenome() // This is to serialize properly
	{
		this.connectionGeneList = new ArrayList<ConnectionGene>();
		this.neuronGeneList = new ArrayList<NeuronGene>();
		this.manager = null;
	}

	public NEATGenome(final List<ConnectionGene> connections, final List<NeuronGene> neurons, final NEATGenomeManager manager)
	{
		this.manager = manager;
		this.connectionGeneList = new ArrayList<ConnectionGene>(connections.size());
		this.neuronGeneList = new ArrayList<NeuronGene>(neurons.size());
		for (final ConnectionGene gene : connections)
			this.connectionGeneList.add(new ConnectionGene(gene));
		for (final NeuronGene gene : neurons)
		{
			try
			{
				this.neuronGeneList.add(new NeuronGene(gene));
			}
			catch (NullPointerException e)
			{
				System.err.println("NeuronGene being added: " + gene);
				e.printStackTrace();
			}
		}
	}

	public NEATGenome(final NEATGenome other)
	{
		this(other.connectionGeneList, other.neuronGeneList, other.manager);
	}

	public NEATGenome(final Random rng, final NEATGenomeManager manager)
	{
		this.manager = manager;
		this.connectionGeneList = new ArrayList<ConnectionGene>(1);
		this.neuronGeneList = new ArrayList<NeuronGene>(this.manager.getNumInputs() + this.manager.getNumOutputs() + 1);

		this.addInitialNodes();
		this.addRandomFirstLink(rng);
		this.sortGenes();
	}

	private void addInitialNodes()
	{
		this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.aquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
				NeuronType.BIAS));
		for (int i = 0; i < this.manager.getNumInputs(); i++)
			this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.aquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
					NeuronType.INPUT));
		for (int i = 0; i < this.manager.getNumOutputs(); i++)
			this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.aquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
					NeuronType.OUTPUT));
	}

	public NeuronGene getNeuronGene(int neuronID)
	{
		for (NeuronGene gene : this.neuronGeneList)
		{
			if (gene.getNeuronID() == neuronID) return gene;
		}
		// throw new NullPointerException("Neuron not found! NeuronID: " + neuronID + "\n Neurons: " + this.neuronGeneList);
		return null;
	}

	private void addRandomFirstLink(final Random rng)
	{
		final int inputIndex = rng.nextInt(this.manager.getNumInputs() + 1); // Bias + Inputs considered
		final int outputIndex = rng.nextInt(this.manager.getNumOutputs()) + this.manager.getOutputOffset(); // Only Outputs considered
		final NEATInnovation link = this.manager.aquireLinkInnovation(inputIndex, outputIndex);
		this.connectionGeneList.add(new ConnectionGene(inputIndex, outputIndex, link.getInnovationID(), 1, true));
	}

	public void sortGenes()
	{
		Collections.sort(this.connectionGeneList);
		Collections.sort(this.neuronGeneList);
	}

	public List<ConnectionGene> getConnectionGeneList()
	{
		return this.connectionGeneList;
	}

	public NEATGenomeManager getManager()
	{
		return this.manager;
	}

	public List<NeuronGene> getNeuronGeneList()
	{
		return this.neuronGeneList;
	}

	@Override
	public String toString()
	{
		return "NEATGenome=[ConnectionGenes:" + this.connectionGeneList + ",NodeGenes:" + this.neuronGeneList + ",Manager:" + this.manager + "]";
	}

	/**
	 * @return the species
	 */
	public NEATSpecies getSpecies()
	{
		return species;
	}

	/**
	 * @param species
	 *            the species to set
	 */
	public void setSpecies(NEATSpecies species)
	{
		this.species = species;
	}

	public double getAdjustedScore()
	{
		if (this.species != null && this.species.getMembers().size() != 0)
			return this.score / this.species.getMembers().size();
		else
			return this.score;
	}

	/**
	 * @return the score
	 */
	public double getScore()
	{
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(double score)
	{
		this.score = score;
	}
}