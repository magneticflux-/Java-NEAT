package org.javaneat.genome;

import java.io.Serializable;

public class NEATInnovation implements Serializable {
    private final long innovationID;
    private final long neuronID;
    private final long toNeuron;
    private final long fromNeuron;        // -1 if link

    @SuppressWarnings("unused")
    private NEATInnovation() // This is to serialize properly
    {
        this(-1, -1, -1, -1);
    }

    public NEATInnovation(long innovationID, long neuronID) {
        this(innovationID, neuronID, -1, -1);
    }

    public NEATInnovation(long innovationID, long fromNeuron, long toNeuron) {
        this(innovationID, -1, toNeuron, fromNeuron);
    }

    public NEATInnovation(long innovationID, long neuronID, long fromNeuron, long toNeuron) {
        this.innovationID = innovationID;
        this.neuronID = neuronID;
        this.fromNeuron = fromNeuron;
        this.toNeuron = toNeuron;
    }

    public long getInnovationID() {
        return this.innovationID;
    }

    public long getNeuronID() {
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

    public long getFromNeuron() {
        return fromNeuron;
    }

    public long getToNeuron() {
        return toNeuron;
    }
}
