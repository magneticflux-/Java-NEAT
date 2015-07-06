package com.javaneat.genotype;

public class ConnectionGene
{
	private int		innovationID;
	private int		fromNode;
	private int		toNode;
	private double	weight;
	private boolean	enabled;

	public ConnectionGene(int fromNode, int toNode, int innovationID, double weight, boolean enabled)
	{
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.innovationID = innovationID;
		this.weight = weight;
		this.enabled = enabled;
	}

	public ConnectionGene(ConnectionGene other)
	{
		this(other.fromNode, other.toNode, other.innovationID, other.weight, other.enabled);
	}

	public int getFromNode()
	{
		return this.fromNode;
	}

	public int getToNode()
	{
		return this.toNode;
	}

	public String toString()
	{
		return "ConnectionGene=[FromNode:" + this.fromNode + ",ToNode:" + this.toNode + ",Weight:" + this.weight + ",Enabled:" + this.enabled
				+ ",InnovationID:" + this.innovationID + "]";
	}
}