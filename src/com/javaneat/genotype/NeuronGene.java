package com.javaneat.genotype;

public class NeuronGene
{
	private int			neuronID;
	private int			innovationID;
	private NeuronType	neuronType;

	public NeuronGene(NeuronGene other)
	{
		this.neuronID = other.neuronID;
		this.innovationID = other.innovationID;
		this.neuronType = other.neuronType;
	}

	public NeuronGene(int neuronID, int innovationID, NeuronType neuronType)
	{
		this.neuronID = neuronID;
		this.innovationID = innovationID;
		this.neuronType = neuronType;
	}

	public String toString()
	{
		return "NeuronGene=[NeuronID:" + this.neuronID + ",InnovationID:" + this.innovationID + ",NeuronType:" + this.neuronType + "]";
	}
}