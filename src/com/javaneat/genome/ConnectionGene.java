package com.javaneat.genome;

public class ConnectionGene implements Comparable<ConnectionGene> {
    private int innovationID;
    private int fromNode;
    private int toNode;
    private double weight;
    private boolean enabled;

    @SuppressWarnings("unused")
    private ConnectionGene() // This is to serialize properly
    {
    }

    public ConnectionGene(ConnectionGene other) {
        this(other.fromNode, other.toNode, other.innovationID, other.weight, other.enabled);
    }

    public ConnectionGene(int fromNode, int toNode, int innovationID, double weight, boolean enabled) // Immutable-ish
    {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.innovationID = innovationID;
        this.weight = weight;
        this.enabled = enabled;
    }

    public int getInnovationID() {
        return this.innovationID;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getFromNode() {
        return this.fromNode;
    }

    public int getToNode() {
        return this.toNode;
    }

    public boolean equals(Object o) {
        return o instanceof ConnectionGene && this.innovationID == ((ConnectionGene) o).innovationID;
    }

    public String toString() {
        return "ConnectionGene=[FromNode:" + this.fromNode + ",ToNode:" + this.toNode + ",Weight:" + this.weight + ",Enabled:" + this.enabled
                + ",InnovationID:" + this.innovationID + "]";
    }

    public int compareTo(ConnectionGene o) {
        return this.innovationID - o.innovationID;
    }
}
