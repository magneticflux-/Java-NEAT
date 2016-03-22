package com.javaneat.evolution;

import com.javaneat.genome.InnovationKey;
import com.javaneat.genome.NEATInnovation;

import java.util.HashMap;
import java.util.Map;

public class NEATGenomeManager {
    @Deprecated
    private final double disjointGeneCoefficient;
    @Deprecated
    private final double excessGeneCoefficient;
    @Deprecated
    private final int numInputs;
    @Deprecated
    private final int numOutputs;
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

    private final Map<InnovationKey, NEATInnovation> innovations = new HashMap<>();
    private int globalInnovationID = 0;
    private int globalNeuronID = 0;
    private double speciesCutoff;

    @SuppressWarnings("unused")
    private NEATGenomeManager() // This is to serialize properly
    {
        this.numInputs = 0;
        this.numOutputs = 0;
        this.disjointGeneCoefficient = 0;
        this.excessGeneCoefficient = 0;
        this.weightDifferenceCoefficient = 0;
        this.speciesTarget = 0;
        this.speciesCutoffDelta = 0;
        this.populationSize = 0;
        this.speciesStagnantTimeLimit = 0;
        this.mutationWeightProb = 0;
        this.mutationWeightRange = 0;
        this.mutationAddLinkProb = 0;
        this.mutationAddNodeProb = 0;
        this.mutationWeightWholeProb = 0;
        this.enableMutationProb = 0;
        this.disableMutationProb = 0;
        this.crossoverChance = 0;
        this.mutationRemoveLinkProb = 0;
    }

    public NEATGenomeManager(final int numInputs, final int numOutputs, final double disjointGeneCoefficient, final double excessGeneCoefficient,
                             final double weightDifferenceCoefficient, final int speciesTarget, final double speciesCutoff, final double speciesCutoffDelta,
                             final int populationSize, final int speciesStagnantTimeLimit, final double mutationWeightWholeProb, final double mutationWeightProb,
                             final double mutationAddLinkProb, final double mutationAddNodeProb, final double mutationWeightRange, final double enableMutationProb,
                             final double disableMutationProb, final double crossoverChance, final double mutationRemoveLinkProb) {
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

        // Node placement in array of each genome/phenome: [1 bias][numInputs input nodes][numOutputs output nodes][Variable hidden nodes]
        this.acquireNodeInnovation(this.getNewNeuronID()); // Bias
        for (int i = 0; i < this.numInputs; i++)
            this.acquireNodeInnovation(this.getNewNeuronID()); // Inputs
        for (int i = 0; i < this.numOutputs; i++)
            this.acquireNodeInnovation(this.getNewNeuronID()); // Outputs
    }

    private static InnovationKey getNodeKey(final int nodeID) {
        return new InnovationKey(InnovationKey.InnovationType.NODE, nodeID, -1);//"node_" + nodeID;
    }

    private static InnovationKey getLinkKey(final int fromNode, final int toNode) {
        return new InnovationKey(InnovationKey.InnovationType.LINK, fromNode, toNode);//"link_" + fromNode + ":" + toNode;
    }

    private static InnovationKey getSplitKey(final int fromNode, final int toNode) {
        return new InnovationKey(InnovationKey.InnovationType.SPLIT, fromNode, toNode);//"split_" + fromNode + ":" + toNode;
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

    private int getNewInnovationID() {
        int result = this.globalInnovationID;
        this.globalInnovationID++;
        return result;
    }

    @Deprecated
    private double getCrossoverChance() {
        return this.crossoverChance;
    }

    @Deprecated
    private int getSpeciesStagnantTimeLimit() {
        return this.speciesStagnantTimeLimit;
    }

    @Deprecated
    private double getEnableMutationProb() {
        return this.enableMutationProb;
    }

    @Deprecated
    private double getDisableMutationProb() {
        return this.disableMutationProb;
    }

    @Deprecated
    private double getMutationWeightWholeProb() {
        return this.mutationWeightWholeProb;
    }

    public NEATInnovation acquireLinkInnovation(final int fromNode, final int toNode) // For a link mutation
    {
        InnovationKey key = NEATGenomeManager.getLinkKey(fromNode, toNode);
        if (!this.innovations.containsKey(key))
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), -1));
        return this.innovations.get(key);
    }

    public NEATInnovation acquireSplitInnovation(final int fromNode, final int toNode) // For a split mutation
    {
        InnovationKey key = NEATGenomeManager.getSplitKey(fromNode, toNode);
        if (!this.innovations.containsKey(key))
            this.innovations.put(key, new NEATInnovation(this.getNewInnovationID(), this.getNewNeuronID()));
        return this.innovations.get(key);
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

    @Deprecated
    public int getNumInputs() {
        return this.numInputs;
    }

    @Deprecated
    public int getNumOutputs() {
        return this.numOutputs;
    }

    @Deprecated
    public int getInputOffset() {
        return 1;
    }

    @Deprecated
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
