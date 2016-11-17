package org.javaneat.genome;

import java.io.Serializable;

/**
 * Created by Mitchell Skaggs on 1/18/2016.
 */
public class InnovationKey implements Serializable {
    private final InnovationType type;
    private final long fromNode;
    private final long toNode;

    @SuppressWarnings("unused")
    private InnovationKey() {
        this(null, -1, -1);
    }

    public InnovationKey(InnovationType type, long fromNode, long toNode) {
        this.type = type;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InnovationKey that = (InnovationKey) o;

        if (fromNode != that.fromNode) return false;
        if (toNode != that.toNode) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (int) (fromNode ^ (fromNode >>> 32));
        result = 31 * result + (int) (toNode ^ (toNode >>> 32));
        return result;
    }

    public enum InnovationType {
        NODE, LINK, SPLIT
    }
}
