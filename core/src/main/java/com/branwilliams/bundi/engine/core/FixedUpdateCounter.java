package com.branwilliams.bundi.engine.core;

public class FixedUpdateCounter {

    private double updateDelta;

    private double lastFrameDuration;

    private double prevTime;

    private double updatesPerSecond;

    private double updatesFraction; // fantastic

    public FixedUpdateCounter(float updatesPerSecond, double time) {
        this.updatesPerSecond = updatesPerSecond;
        this.updatesFraction = 1F / updatesPerSecond;
        this.prevTime = time;
    }

    public int countUpdates(double time) {
        this.lastFrameDuration = (time - this.prevTime) / this.updatesFraction;
        this.prevTime = time;
        this.updateDelta += this.lastFrameDuration;
        int numberOfUpdatesThisFrame = (int) this.updateDelta;
        this.updateDelta -= (float) numberOfUpdatesThisFrame;
        return numberOfUpdatesThisFrame;
    }

    public double getUpdatesPerSecond() {
        return updatesPerSecond;
    }

    public void setUpdatesPerSecond(double updatesPerSecond) {
        this.updatesPerSecond = updatesPerSecond;
        this.updatesFraction = 1F / updatesPerSecond;
    }

    public double getUpdateDelta() {
        return updateDelta;
    }

    public void setUpdateDelta(double updateDelta) {
        this.updateDelta = updateDelta;
    }

    public double getLastFrameDuration() {
        return lastFrameDuration;
    }

    public void setLastFrameDuration(double lastFrameDuration) {
        this.lastFrameDuration = lastFrameDuration;
    }
}

