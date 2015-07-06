package com.javaneat.genotype;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.javaneat.evolution.NEATGenomeManager;

public class NEATGenotype
{
	private final List<ConnectionGene>	connectionGeneList;
	private NEATGenomeManager			manager;
	private final List<NeuronGene>		neuronGeneList;

	public NEATGenotype(final List<ConnectionGene> connections, final List<NeuronGene> neurons, final NEATGenomeManager manager)
	{
		this.manager = manager;
		this.connectionGeneList = new ArrayList<ConnectionGene>(connections.size());
		this.neuronGeneList = new ArrayList<NeuronGene>(neurons.size());
		for (final ConnectionGene gene : connections)
			this.connectionGeneList.add(new ConnectionGene(gene));
		for (final NeuronGene gene : neurons)
			this.neuronGeneList.add(new NeuronGene(gene));
	}

	public NEATGenotype(final NEATGenotype other)
	{
		this(other.connectionGeneList, other.neuronGeneList, other.manager);
	}

	private void addInitialNodes()
	{
		this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.aquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
				NeuronType.BIAS));
		for (int i = 0; i < this.manager.getNumInputs(); i++)
		{
			this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.aquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
					NeuronType.INPUT));
		}
		for (int i = 0; i < this.manager.getNumOutputs(); i++)
		{
			this.neuronGeneList.add(new NeuronGene(this.neuronGeneList.size(), this.manager.aquireNodeInnovation(this.neuronGeneList.size()).getInnovationID(),
					NeuronType.OUTPUT));
		}
	}

	private void addRandomFirstLink(Random rng)
	{
		final int inputIndex = rng.nextInt(this.manager.getNumInputs() + 1); // Bias + Inputs considered
		final int outputIndex = rng.nextInt(this.manager.getNumOutputs()) + this.manager.getOutputOffset(); // Only Outputs considered
		NEATInnovation link = this.manager.aquireLinkInnovation(inputIndex, outputIndex);
		this.connectionGeneList.add(new ConnectionGene(inputIndex, outputIndex, link.getInnovationID(), 1, true));
	}

	public NEATGenotype(final Random rng, final NEATGenomeManager manager)
	{
		this.manager = manager;
		this.connectionGeneList = new ArrayList<ConnectionGene>(1);
		this.neuronGeneList = new ArrayList<NeuronGene>(this.manager.getNumInputs() + this.manager.getNumOutputs() + 1);

		this.addInitialNodes();
		this.addRandomFirstLink(rng);
	}

	public String toString()
	{
		return "NEATGenotype=[ConnectionGenes:" + this.connectionGeneList + ",NodeGenes:" + this.neuronGeneList + ",Manager:" + this.manager + "]";
	}
}