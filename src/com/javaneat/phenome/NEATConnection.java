package com.javaneat.phenome;

public class NEATConnection {
    private final int toIndex;
    private final int fromIndex;
    private final double weight;

    public NEATConnection(int toIndex, int fromIndex, double weight) {
        this.toIndex = toIndex;
        this.fromIndex = fromIndex;
        this.weight = weight;
    }

    /**
     * @return the toNeuron
     */
    public int getToIndex() {
        return toIndex;
    }

    /**
     * @return the fromNeuron
     */
    public int getFromIndex() {
        return fromIndex;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }
}