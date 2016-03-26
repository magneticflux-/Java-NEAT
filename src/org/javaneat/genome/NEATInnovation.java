package org.javaneat.genome;

import java.io.Serializable;

public class NEATInnovation implements Serializable {
    private int innovationID;
    private int neuronID;        // -1 if link

    @SuppressWarnings("unused")
    private NEATInnovation() // This is to serialize properly
    {
    }

    public NEATInnovation(int innovationID, int neuronID) {
        this.innovationID = innovationID;
        this.neuronID = neuronID;
    }

    public int getInnovationID() {
        return this.innovationID;
    }

    public int getNeuronID() {
        return this.neuronID;
    }

    public String toString() {
        return "NEATInnovation=[InnovationID:" + this.innovationID + ",NeuronID:" + this.neuronID + "]";
    }
}
