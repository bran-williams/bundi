package com.branwilliams.bundi.engine.util;

/**
 * Simple timer class, mostly used for keeping track of when st00f occured. <br/>
 *
 * Created in like 2012 by Brandon Williams.
 */
public final class Timer {

    private long lastCheck = getSystemTime();

    /**
     * Checks if the passed time reached the targetted time.
     */
    public boolean hasReach(int targetTime) {
        return getTimePassed() >= targetTime;
    }

    public long getTimePassed() {
        return getSystemTime() - lastCheck;
    }

    /**
     * Sets this timer's last checked time to the current system time (in milliseconds).
     * */
    public void reset() {
        lastCheck = getSystemTime();
    }

    /**
     * @return The system time in milliseconds.
     * */
    public static long getSystemTime() {
        return System.nanoTime() / 1_000_000L;
    }
    
}