package com.javaneat.evolution;

import java.util.HashMap;
import java.util.Map;

import com.javaneat.genotype.NEATInnovation;

public class NEATGenomeManager
{
	private Map<String, NEATInnovation>	innovations			= new HashMap<String, NEATInnovation>();
	private int							globalInnovationID	= 0;
	private int							globalNeuronID		= 0;
	private int							numInputs;
	private int							numOutputs;

	public NEATGenomeManager(int numInputs, int numOutput)
	{
		this.numInputs = numInputs;
		this.numOutputs = numOutput;

		// Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
		this.aquireNodeInnovation(this.getNewNeuronID()); // Bias
		for (int i = 0; i < this.numInputs; i++) // Inputs
		{
			this.aquireNodeInnovation(this.getNewNeuronID());
		}
		for (int i = 0; i < this.numOutputs; i++) // Outputs
		{
			this.aquireNodeInnovation(this.getNewNeuronID());
		}
	}

	public int getInputOffset()
	{
		return 1;
	}

	public int getOutputOffset()
	{
		return 1 + this.getNumInputs();
	}

	public int getBiasOffset()
	{
		return 0;
	}

	public int getHiddenOffset()
	{
		return 1 + this.getNumInputs() + this.getNumOutputs();
	}

	public int getNumInputs()
	{
		return this.numInputs;
	}

	public int getNumOutputs()
	{
		return this.numOutputs;
	}

	public int getNewInnovationID()
	{
		return this.globalInnovationID++;
	}

	public int getNewNeuronID()
	{
		return this.globalNeuronID++;
	}

	public static String getLinkKey(int fromNode, int toNode)
	{
		return "link_" + fromNode + ":" + toNode;
	}

	public static String getNodeKey(int nodeID)
	{
		return "node_" + nodeID;
	}

	public static String getSplitKey(int fromNode, int toNode)
	{
		return "split_" + fromNode + ":" + toNode;
	}

	public NEATInnovation aquireLinkInnovation(int fromNode, int toNode) // For a link mutation
	{
		String key = NEATGenomeManager.getLinkKey(fromNode, toNode);
		if (!innovations.containsKey(key))
		{
			this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), -1));
		}
		return innovations.get(key);
	}

	public NEATInnovation aquireNodeInnovation(int nodeID) // ONLY for input, output, and bias nodes.
	{
		String key = NEATGenomeManager.getNodeKey(nodeID);
		if (!innovations.containsKey(key))
		{
			this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), nodeID));
		}
		return innovations.get(key);
	}

	public NEATInnovation aquireSplitInnovation(int fromNode, int toNode) // For a split mutation
	{
		String key = NEATGenomeManager.getSplitKey(fromNode, toNode);
		if (!innovations.containsKey(key))
		{
			this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), this.getNewNeuronID()));
		}
		return innovations.get(key);
	}
}