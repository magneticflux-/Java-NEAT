package org.javaneat.evolution;

import org.javaneat.genome.InnovationKey;
import org.javaneat.genome.NEATInnovation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mitchell Skaggs on 6/20/2016.
 */
public class NEATInnovationMap implements Serializable {
    private final Map<InnovationKey, NEATInnovation> innovations;
    private long globalInnovationID = 0;
    private long globalNeuronID = 0;

    @SuppressWarnings("unused")
    public NEATInnovationMap() {
        innovations = new HashMap<>();
    }

    public NEATInnovationMap(int numInputs, int numOutputs) {
        innovations = new HashMap<>();

        // Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
        this.acquireNodeInnovation(this.getNewNeuronID()); // Bias
        for (int i = 0; i < numInputs; i++)
            this.acquireNodeInnovation(this.getNewNeuronID()); // Inputs
        for (int i = 0; i < numOutputs; i++)
            this.acquireNodeInnovation(this.getNewNeuronID()); // Outputs
    }

    private static InnovationKey getNodeKey(final long nodeID) {
        return new InnovationKey(InnovationKey.InnovationType.NODE, nodeID, -1);
    }

    private static InnovationKey getLinkKey(final long fromNode, final long toNode) {
        return new InnovationKey(InnovationKey.InnovationType.LINK, fromNode, toNode);
    }

    private static InnovationKey getSplitKey(final long fromNode, final Long toNode) {
        return new InnovationKey(InnovationKey.InnovationType.SPLIT, fromNode, toNode);
    }

    private long getNewInnovationID() {
        long result = this.globalInnovationID;
        this.globalInnovationID++;
        return result;
    }

    private long getNewNeuronID() {
        long result = this.globalNeuronID;
        this.globalNeuronID++;
        //System.out.println("Global neuron ID is now: " + globalNeuronID);
        return result;
    }

    public NEATInnovation acquireNodeInnovation(final long nodeID) // ONLY for input, output, and bias nodes.
    {
        InnovationKey key = getNodeKey(nodeID);
        if (!this.innovations.containsKey(key)) {
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), nodeID));
        }
        return this.innovations.get(key);
    }

    public NEATInnovation acquireLinkInnovation(final long fromNode, final long toNode) // For a link mutation
    {
        InnovationKey key = getLinkKey(fromNode, toNode);
        if (!this.innovations.containsKey(key)) {
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), fromNode, toNode));
        }
        return this.innovations.get(key);
    }

    public NEATInnovation acquireSplitInnovation(final long fromNode, final Long toNode) // For a split mutation
    {
        InnovationKey key = getSplitKey(fromNode, toNode);
        if (!this.innovations.containsKey(key)) {
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), this.getNewNeuronID(), fromNode, toNode));
        }
        return this.innovations.get(key);
    }

    public int size() {
        return innovations.size();
    }
}
