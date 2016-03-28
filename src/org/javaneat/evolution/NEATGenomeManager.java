package org.javaneat.evolution;

import org.javaneat.genome.InnovationKey;
import org.javaneat.genome.NEATInnovation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NEATGenomeManager implements Serializable {
    @Deprecated
    private final double disjointGeneCoefficient;
    @Deprecated
    private final double excessGeneCoefficient;
    @Deprecated
    private final int populationSize;
    @Deprecated
    private final double speciesCutoffDelta;
    @Deprecated
    private final int speciesTarget;
    @Deprecated
    private final double weightDifferenceCoefficient;
    @Deprecated
    private final int speciesStagnantTimeLimit;
    @Deprecated
    private final double mutationWeightProb;
    @Deprecated
    private final double mutationAddLinkProb;
    @Deprecated
    private final double mutationAddNodeProb;
    @Deprecated
    private final double mutationWeightRange;
    @Deprecated
    private final double mutationWeightWholeProb;
    @Deprecated
    private final double enableMutationProb;
    @Deprecated
    private final double disableMutationProb;
    @Deprecated
    private final double crossoverChance;
    @Deprecated
    private final double mutationRemoveLinkProb;
    private final Map<InnovationKey, NEATInnovation> innovations;
    public int numInputs;
    public int numOutputs;
    private int globalInnovationID = 0;
    private int globalNeuronID = 0;
    @Deprecated
    private double speciesCutoff;

    private NEATGenomeManager() // This is to serialize properly
    {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Deprecated
    public NEATGenomeManager(final int numInputs, final int numOutputs, final double disjointGeneCoefficient, final double excessGeneCoefficient, final double weightDifferenceCoefficient, final int speciesTarget, final double speciesCutoff, final double speciesCutoffDelta, final int populationSize, final int speciesStagnantTimeLimit, final double mutationWeightWholeProb, final double mutationWeightProb, final double mutationAddLinkProb, final double mutationAddNodeProb, final double mutationWeightRange, final double enableMutationProb, final double disableMutationProb, final double crossoverChance, final double mutationRemoveLinkProb) {
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        this.disjointGeneCoefficient = disjointGeneCoefficient;
        this.excessGeneCoefficient = excessGeneCoefficient;
        this.weightDifferenceCoefficient = weightDifferenceCoefficient;
        this.speciesTarget = speciesTarget;
        this.speciesCutoff = speciesCutoff;
        this.speciesCutoffDelta = speciesCutoffDelta;
        this.populationSize = populationSize;
        this.speciesStagnantTimeLimit = speciesStagnantTimeLimit;
        this.mutationWeightProb = mutationWeightProb;
        this.mutationWeightRange = mutationWeightRange;
        this.mutationAddLinkProb = mutationAddLinkProb;
        this.mutationAddNodeProb = mutationAddNodeProb;
        this.mutationWeightWholeProb = mutationWeightWholeProb;
        this.enableMutationProb = enableMutationProb;
        this.disableMutationProb = disableMutationProb;
        this.crossoverChance = crossoverChance;
        this.mutationRemoveLinkProb = mutationRemoveLinkProb;

        this.innovations = new HashMap<>();

        // Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
        this.acquireNodeInnovation(this.getNewNeuronID()); // Bias
        for (int i = 0; i < this.numInputs; i++)
            this.acquireNodeInnovation(this.getNewNeuronID()); // Inputs
        for (int i = 0; i < this.numOutputs; i++)
            this.acquireNodeInnovation(this.getNewNeuronID()); // Outputs
    }

    public NEATInnovation acquireNodeInnovation(final int nodeID) // ONLY for input, output, and bias nodes.
    {
        InnovationKey key = NEATGenomeManager.getNodeKey(nodeID);
        if (!this.innovations.containsKey(key))
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), nodeID));
        return this.innovations.get(key);
    }

    public int getNewNeuronID() {
        int result = this.globalNeuronID;
        this.globalNeuronID++;
        return result;
    }

    private static InnovationKey getNodeKey(final int nodeID) {
        return new InnovationKey(InnovationKey.InnovationType.NODE, nodeID, -1);
    }

    private int getNewInnovationID() {
        int result = this.globalInnovationID;
        this.globalInnovationID++;
        return result;
    }

    public NEATGenomeManager(int numInputs, int numOutputs) {
        this(numInputs, numOutputs, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Deprecated
    double getCrossoverChance() {
        return this.crossoverChance;
    }

    @Deprecated
    int getSpeciesStagnantTimeLimit() {
        return this.speciesStagnantTimeLimit;
    }

    @Deprecated
    double getEnableMutationProb() {
        return this.enableMutationProb;
    }

    @Deprecated
    double getDisableMutationProb() {
        return this.disableMutationProb;
    }

    @Deprecated
    double getMutationWeightWholeProb() {
        return this.mutationWeightWholeProb;
    }

    public NEATInnovation acquireLinkInnovation(final int fromNode, final int toNode) // For a link mutation
    {
        InnovationKey key = NEATGenomeManager.getLinkKey(fromNode, toNode);
        if (!this.innovations.containsKey(key))
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), -1));
        return this.innovations.get(key);
    }

    private static InnovationKey getLinkKey(final int fromNode, final int toNode) {
        return new InnovationKey(InnovationKey.InnovationType.LINK, fromNode, toNode);
    }

    public NEATInnovation acquireSplitInnovation(final int fromNode, final int toNode) // For a split mutation
    {
        InnovationKey key = NEATGenomeManager.getSplitKey(fromNode, toNode);
        if (!this.innovations.containsKey(key))
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), this.getNewNeuronID()));
        return this.innovations.get(key);
    }

    private static InnovationKey getSplitKey(final int fromNode, final int toNode) {
        return new InnovationKey(InnovationKey.InnovationType.SPLIT, fromNode, toNode);
    }

    public int getBiasOffset() {
        return 0;
    }

    @Deprecated
    public double getDisjointGeneCoefficient() {
        return this.disjointGeneCoefficient;
    }

    @Deprecated
    public double getExcessGeneCoefficient() {
        return this.excessGeneCoefficient;
    }

    @Deprecated
    public int getHiddenOffset() {
        return 1 + this.getNumInputs() + this.getNumOutputs();
    }

    public int getNumInputs() {
        return this.numInputs;
    }

    public int getNumOutputs() {
        return this.numOutputs;
    }

    public int getInputOffset() {
        return 1;
    }

    public int getOutputOffset() {
        return 1 + this.getNumInputs();
    }

    @Deprecated
    int getPopulationSize() {
        return this.populationSize;
    }

    @Deprecated
    public double getSpeciesCutoff() {
        return this.speciesCutoff;
    }

    /**
     * @return the speciesCutoffDelta
     */
    @Deprecated
    public double getSpeciesCutoffDelta() {
        return this.speciesCutoffDelta;
    }

    @Deprecated
    int getSpeciesTarget() {
        return this.speciesTarget;
    }

    @Deprecated
    public double getWeightDifferenceCoefficient() {
        return this.weightDifferenceCoefficient;
    }

    @Deprecated
    void tweakSpeciesCutoff(final boolean up) {
        if (this.speciesCutoff + (up ? this.speciesCutoffDelta : -this.speciesCutoffDelta) > 0)
            this.speciesCutoff += up ? this.speciesCutoffDelta : -this.speciesCutoffDelta;
    }

    /**
     * @return the mutationWeightProb
     */
    @Deprecated
    double getMutationWeightProb() {
        return mutationWeightProb;
    }

    /**
     * @return the mutationAddLinkProb
     */
    @Deprecated
    double getMutationAddLinkProb() {
        return mutationAddLinkProb;
    }

    /**
     * @return the mutationAddNodeProb
     */
    @Deprecated
    double getMutationAddNodeProb() {
        return mutationAddNodeProb;
    }

    /**
     * @return the mutationWeightRange
     */
    @Deprecated
    double getMutationWeightRange() {
        return mutationWeightRange;
    }

    /**
     * @return the mutationRemoveLinkProb
     */
    @Deprecated
    double getMutationRemoveLinkProb() {
        return mutationRemoveLinkProb;
    }
}
