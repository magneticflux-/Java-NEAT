package org.javaneat.evolution.nsgaii;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class MarioBrosData implements Serializable {
    public final List<DataPoint> dataPoints;

    public MarioBrosData() {
        dataPoints = new LinkedList<>();
    }

    @Override
    public String toString() {
        return dataPoints.toString();
    }

    public void addDataPoint(DataPoint dataPoint) {
        this.dataPoints.add(dataPoint);
    }

    public static class DataPoint implements Serializable {
        public final int score, time, world, level, lives, marioX, marioY, marioState;

        @SuppressWarnings("unused")
        private DataPoint() {
            this(0, 0, 0, 0, 0, 0, 0, 0);
        }

        public DataPoint(int score, int time, int world, int level, int lives, int marioX, int marioY, int marioState) {
            this.score = score;
            this.time = time;
            this.world = world;
            this.level = level;
            this.lives = lives;
            this.marioX = marioX;
            this.marioY = marioY;
            this.marioState = marioState;
        }

        @Override
        public String toString() {
            return String.format("%d %d %d %d %d %d %d %d", score, time, world, level, lives, marioX, marioY, marioState);
        }
    }
}
