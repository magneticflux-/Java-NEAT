package com.javaneat.evolution;

import java.util.HashMap;
import java.util.Map;

import com.javaneat.genotype.NEATInnovation;

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
	private double								speciesCutoff;
	private final double						speciesCutoffDelta;
	private final int							speciesTarget;
	private final double						weightDifferenceCoefficient;

	public NEATGenomeManager(final int numInputs, final int numOutputs, final double disjointGeneCoefficient, final double excessGeneCoefficient,
			final double weightDifferenceCoefficient, final int speciesTarget, final double speciesCutoff, final double speciesCutoffDelta)
	{
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		this.disjointGeneCoefficient = disjointGeneCoefficient;
		this.excessGeneCoefficient = excessGeneCoefficient;
		this.weightDifferenceCoefficient = weightDifferenceCoefficient;
		this.speciesTarget = speciesTarget;
		this.speciesCutoff = speciesCutoff;
		this.speciesCutoffDelta = speciesCutoffDelta;

		// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
		this.aquireNodeInnovation(this.getNewNeuronID()); // Bias
		for (int i = 0; i < this.numInputs; i++)
			this.aquireNodeInnovation(this.getNewNeuronID()); // Inputs
		for (int i = 0; i < this.numOutputs; i++)
			this.aquireNodeInnovation(this.getNewNeuronID()); // Outputs
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

	public void tweakSpeciesCutoff(boolean up)
	{
		this.speciesCutoff += up ? this.speciesCutoffDelta : -this.speciesCutoffDelta;
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

	public double getSpeciesCutoff()
	{
		return this.speciesCutoff;
	}

	public int getSpeciesTarget()
	{
		return this.speciesTarget;
	}

	public double getWeightDifferenceCoefficient()
	{
		return this.weightDifferenceCoefficient;
	}

	/**
	 * @return the speciesCutoffDelta
	 */
	public double getSpeciesCutoffDelta()
	{
		return speciesCutoffDelta;
	}
}