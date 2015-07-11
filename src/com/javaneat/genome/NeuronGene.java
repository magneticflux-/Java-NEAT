package com.javaneat.genome;

public class NeuronGene implements Comparable<NeuronGene>
{
	private int			neuronID;
	private int			innovationID;
	private NeuronType	neuronType;

	@SuppressWarnings("unused")
	private NeuronGene() // This is to serialize properly
	{
	}

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

	public int getNeuronID()
	{
		return this.neuronID;
	}

	public String toString()
	{
		return "NeuronGene=[NeuronID:" + this.neuronID + ",InnovationID:" + this.innovationID + ",NeuronType:" + this.neuronType + "]";
	}

	public int compareTo(NeuronGene o)
	{
		return this.innovationID - o.innovationID;
	}
}