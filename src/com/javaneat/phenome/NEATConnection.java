package com.javaneat.phenome;

public class NEATConnection
{
	private final int		toNeuron;
	private final int		fromNeuron;
	private final double	weight;

	public NEATConnection(int toNeuron, int fromNeuron, double weight)
	{
		this.toNeuron = toNeuron;
		this.fromNeuron = fromNeuron;
		this.weight = weight;
	}

	/**
	 * @return the toNeuron
	 */
	public int getToNeuron()
	{
		return toNeuron;
	}

	/**
	 * @return the fromNeuron
	 */
	public int getFromNeuron()
	{
		return fromNeuron;
	}

	/**
	 * @return the weight
	 */
	public double getWeight()
	{
		return weight;
	}
}