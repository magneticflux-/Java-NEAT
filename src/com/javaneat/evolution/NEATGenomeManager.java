package com.javaneat.evolution;

import java.util.HashMap;
import java.util.Map;

import com.javaneat.genome.NEATInnovation;

public class NEATGenomeManager
{
	public static String getLinkKey(final int fromNode, final int toNode)
	{
		return "link_" + fromNode + ":" + toNode;
	}

	public static String getNodeKey(final int nodeID)
	{
		return "node_" + nodeID;
	}

	public static String getSplitKey(final int fromNode, final int toNode)
	{
		return "split_" + fromNode + ":" + toNode;
	}

	private final double						disjointGeneCoefficient;
	private final double						excessGeneCoefficient;
	private int									globalInnovationID	= 0;
	private int									globalNeuronID		= 0;
	private final Map<String, NEATInnovation>	innovations			= new HashMap<String, NEATInnovation>();
	private final int							numInputs;
	private final int							numOutputs;
	private final int							populationSize;
	private double								speciesCutoff;
	private final double						speciesCutoffDelta;
	private final int							speciesTarget;
	private final double						weightDifferenceCoefficient;
	private final int							speciesStagnantTimeLimit;
	private final double						mutationWeightProb;
	private final double						mutationAddLinkProb;
	private final double						mutationAddNodeProb;
	private final double						mutationWeightRange;

	@SuppressWarnings("unused")
	private NEATGenomeManager() // This is to serialize properly
	{
		this.numInputs = 0;
		this.numOutputs = 0;
		this.disjointGeneCoefficient = 0;
		this.excessGeneCoefficient = 0;
		this.weightDifferenceCoefficient = 0;
		this.speciesTarget = 0;
		this.speciesCutoffDelta = 0;
		this.populationSize = 0;
		this.speciesStagnantTimeLimit = 0;
		this.mutationWeightProb = 0;
		this.mutationWeightRange = 0;
		this.mutationAddLinkProb = 0;
		this.mutationAddNodeProb = 0;
	}

	public NEATGenomeManager(final int numInputs, final int numOutputs, final double disjointGeneCoefficient, final double excessGeneCoefficient,
			final double weightDifferenceCoefficient, final int speciesTarget, final double speciesCutoff, final double speciesCutoffDelta,
			final int populationSize, final int speciesStagnantTimeLimit, final double mutationWeightProb, final double mutationAddLinkProb,
			final double mutationAddNodeProb, final double mutationWeightRange)
	{
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		this.disjointGeneCoefficient = disjointGeneCoefficient;
		this.excessGeneCoefficient = excessGeneCoefficient;
		this.weightDifferenceCoefficient = weightDifferenceCoefficient;
		this.speciesTarget = speciesTarget;
		this.speciesCutoff = speciesCutoff;
		this.speciesCutoffDelta = speciesCutoffDelta;
		this.populationSize = populationSize;
		this.speciesStagnantTimeLimit = speciesStagnantTimeLimit;
		this.mutationWeightProb = mutationWeightProb;
		this.mutationWeightRange = mutationWeightRange;
		this.mutationAddLinkProb = mutationAddLinkProb;
		this.mutationAddNodeProb = mutationAddNodeProb;

		// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
		this.aquireNodeInnovation(this.getNewNeuronID()); // Bias
		for (int i = 0; i < this.numInputs; i++)
			this.aquireNodeInnovation(this.getNewNeuronID()); // Inputs
		for (int i = 0; i < this.numOutputs; i++)
			this.aquireNodeInnovation(this.getNewNeuronID()); // Outputs
	}

	public int getSpeciesStagnantTimeLimit()
	{
		return this.speciesStagnantTimeLimit;
	}

	public NEATInnovation aquireLinkInnovation(final int fromNode, final int toNode) // For a link mutation
	{
		final String key = NEATGenomeManager.getLinkKey(fromNode, toNode);
		if (!this.innovations.containsKey(key)) this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), -1));
		return this.innovations.get(key);
	}

	public NEATInnovation aquireNodeInnovation(final int nodeID) // ONLY for input, output, and bias nodes.
	{
		final String key = NEATGenomeManager.getNodeKey(nodeID);
		if (!this.innovations.containsKey(key)) this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), nodeID));
		return this.innovations.get(key);
	}

	public NEATInnovation aquireSplitInnovation(final int fromNode, final int toNode) // For a split mutation
	{
		final String key = NEATGenomeManager.getSplitKey(fromNode, toNode);
		if (!this.innovations.containsKey(key)) this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), this.getNewNeuronID()));
		return this.innovations.get(key);
	}

	public int getBiasOffset()
	{
		return 0;
	}

	public double getDisjointGeneCoefficient()
	{
		return this.disjointGeneCoefficient;
	}

	public double getExcessGeneCoefficient()
	{
		return this.excessGeneCoefficient;
	}

	public int getHiddenOffset()
	{
		return 1 + this.getNumInputs() + this.getNumOutputs();
	}

	public int getInputOffset()
	{
		return 1;
	}

	public int getNewInnovationID()
	{
		return this.globalInnovationID++;
	}

	public int getNewNeuronID()
	{
		return this.globalNeuronID++;
	}

	public int getNumInputs()
	{
		return this.numInputs;
	}

	public int getNumOutputs()
	{
		return this.numOutputs;
	}

	public int getOutputOffset()
	{
		return 1 + this.getNumInputs();
	}

	public int getPopulationSize()
	{
		return this.populationSize;
	}

	public double getSpeciesCutoff()
	{
		return this.speciesCutoff;
	}

	/**
	 * @return the speciesCutoffDelta
	 */
	public double getSpeciesCutoffDelta()
	{
		return this.speciesCutoffDelta;
	}

	public int getSpeciesTarget()
	{
		return this.speciesTarget;
	}

	public double getWeightDifferenceCoefficient()
	{
		return this.weightDifferenceCoefficient;
	}

	public void tweakSpeciesCutoff(final boolean up)
	{
		if (this.speciesCutoff + (up ? this.speciesCutoffDelta : -this.speciesCutoffDelta) > 0)
			this.speciesCutoff += up ? this.speciesCutoffDelta : -this.speciesCutoffDelta;
	}

	/**
	 * @return the mutationWeightProb
	 */
	public double getMutationWeightProb()
	{
		return mutationWeightProb;
	}

	/**
	 * @return the mutationAddLinkProb
	 */
	public double getMutationAddLinkProb()
	{
		return mutationAddLinkProb;
	}

	/**
	 * @return the mutationAddNodeProb
	 */
	public double getMutationAddNodeProb()
	{
		return mutationAddNodeProb;
	}

	/**
	 * @return the mutationWeightRange
	 */
	public double getMutationWeightRange()
	{
		return mutationWeightRange;
	}
}