package org.javaneat.genome;

import java.io.Serializable;

public class ConnectionGene implements Comparable<ConnectionGene>, Cloneable, Serializable {
    private final int innovationID;
    private final int fromNode;
    private final int toNode;
    private double weight;
    private boolean enabled;

    @SuppressWarnings("unused")
    private ConnectionGene() // This is to serialize properly
    {
        fromNode = -1;
        toNode = -1;
        innovationID = -1;
    }

    public ConnectionGene(ConnectionGene other, boolean enabled) {
        this(other.fromNode, other.toNode, other.innovationID, other.weight, enabled);
    }

    public ConnectionGene(int fromNode, int toNode, int innovationID, double weight, boolean enabled) // Immutable-ish
    {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.innovationID = innovationID;
        this.weight = weight;
        this.enabled = enabled;
    }

    public ConnectionGene(ConnectionGene other) {
        this(other.fromNode, other.toNode, other.innovationID, other.weight, other.enabled);
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

    @Override
    public int hashCode() {
        return innovationID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionGene that = (ConnectionGene) o;

        return innovationID == that.innovationID;

    }

    public ConnectionGene clone() {
        try {
            return (ConnectionGene) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "ConnectionGene=[FromNode:" + this.fromNode + ",ToNode:" + this.toNode + ",Weight:" + this.weight + ",Enabled:" + this.enabled + ",InnovationID:" + this.innovationID + "]";
    }

    public int compareTo(ConnectionGene o) {
        return Integer.compare(this.innovationID, o.innovationID);
    }
}
