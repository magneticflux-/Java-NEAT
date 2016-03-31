package org.javaneat.genome;

import java.io.Serializable;

public class NEATInnovation implements Serializable {
    private final int innovationID;
    private final int neuronID;
    private final int toNeuron;
    private final int fromNeuron;        // -1 if link

    @SuppressWarnings("unused")
    private NEATInnovation() // This is to serialize properly
    {
        this(-1, -1, -1, -1);
    }

    public NEATInnovation(int innovationID, int neuronID) {
        this(innovationID, neuronID, -1, -1);
    }

    public NEATInnovation(int innovationID, int fromNeuron, int toNeuron) {
        this(innovationID, -1, toNeuron, fromNeuron);
    }

    public NEATInnovation(int innovationID, int neuronID, int fromNeuron, int toNeuron) {
        this.innovationID = innovationID;
        this.neuronID = neuronID;
        this.toNeuron = toNeuron;
        this.fromNeuron = fromNeuron;
    }

    public int getInnovationID() {
        return this.innovationID;
    }

    public int getNeuronID() {
        return this.neuronID;
    }

    @Override
    public String toString() {
        return "NEATInnovation{" +
                "innovationID=" + innovationID +
                ", neuronID=" + neuronID +
                ", toNeuron=" + toNeuron +
                ", fromNeuron=" + fromNeuron +
                '}';
    }

    public int getFromNeuron() {
        return fromNeuron;
    }

    public int getToNeuron() {
        return toNeuron;
    }
}
