package org.javaneat.genome;

import java.io.Serializable;

public class NeuronGene implements Comparable<NeuronGene>, Serializable {
    private long neuronID;
    private long innovationID;
    private NeuronType neuronType;

    @SuppressWarnings("unused")
    private NeuronGene() // This is to serialize properly
    {
    }

    public NeuronGene(NeuronGene other) {
        this.neuronID = other.neuronID;
        this.innovationID = other.innovationID;
        this.neuronType = other.neuronType;
    }

    public NeuronGene(long neuronID, long innovationID, NeuronType neuronType) {
        this.neuronID = neuronID;
        this.innovationID = innovationID;
        this.neuronType = neuronType;
    }

    public NeuronType getNeuronType() {
        return neuronType;
    }

    public long getNeuronID() {
        return this.neuronID;
    }

    public String toString() {
        return "NeuronGene=[NeuronID:" + this.neuronID + ",InnovationID:" + this.innovationID + ",NeuronType:" + this.neuronType + "]";
    }

    public int compareTo(NeuronGene o) {
        return Long.compare(this.innovationID, o.innovationID);
    }
}
