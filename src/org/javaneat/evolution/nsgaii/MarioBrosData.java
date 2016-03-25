package org.javaneat.evolution.nsgaii;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mitchell on 3/24/2016.
 */
public class MarioBrosData {
    public final List<DataPoint> dataPoints;

    public MarioBrosData() {
        dataPoints = new LinkedList<>();
    }

    public void addDataPoint(DataPoint dataPoint) {
        this.dataPoints.add(dataPoint);
    }

    public static class DataPoint {
        public final int score, time, world, level, lives, marioX, marioY, marioState;

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
    }
}
