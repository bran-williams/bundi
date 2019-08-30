package com.branwilliams.bundi.engine;

import com.branwilliams.bundi.engine.core.Nameable;
import com.branwilliams.bundi.engine.util.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to track how long some chunk of code takes to execute.
 * Useful for determining bottlenecks and profiling portions of a codebase.
 * Created by Brandon Williams on 10/23/2018.
 */
public class Profiler implements Nameable {

    public static final int DEFAULT_SECTION_CAPACITY = 60;
    public static final int DEFAULT_AVERAGE_CAPACITY = 30;


    private final String name;

    private final int maxSectionLogs;

    private final int maxAverageLogs;

    private String previous, current;

    private long startTime = 0;

    private Map<String, DebugInfo> sectionDurations = new HashMap<>();

    public Profiler(String name) {
        this(name, DEFAULT_SECTION_CAPACITY, DEFAULT_AVERAGE_CAPACITY);
    }

    public Profiler(String name, int maxSectionLogs, int maxAverageLogs) {
        this.name = name;
        this.maxSectionLogs = maxSectionLogs;
        this.maxAverageLogs = maxAverageLogs;
    }

    /**
     * Ends the current section and stores the duration of it within it's list of logged durations.
     * */
    public void end() {
        this.previous = this.current;
        this.sectionDurations.computeIfAbsent(this.current, (k) -> new DebugInfo(maxSectionLogs, maxAverageLogs))
                .addTime(Timer.getSystemTime() - startTime);
        this.current = null;
    }

    /**
     * Begin profiling a section of code. This will store the current system time for the duration calculation within
     * {@link Profiler#end()}.
     * @param current The name of the section to profile.
     * @throws IllegalStateException If the current section has not ended before invocation of this function.
     * */
    public void begin(String current) {
        if (this.current != null) {
            throw new IllegalStateException("The current profile must end first!");
        }
        this.startTime = Timer.getSystemTime();
        this.current = current;
    }

    /**
     * Ends the current section and begins the provided section.
     * @param current The name of the section to profile.
     * */
    public void endBegin(String current) {
        this.end();
        this.begin(current);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getCurrent() {
        return current;
    }

    public String getPrevious() {
        return previous;
    }

    public Map<String, DebugInfo> getSectionDurations() {
        return sectionDurations;
    }

    /**
     * Assigns the initial capacity to also be the max capacity of this list. It will remove the 0th element from the
     * list to make room for any added elements.
     * */
    public class LimitedArrayList <T> extends ArrayList<T> {
        private final int maxCapacity;

        public LimitedArrayList(int initialCapacity) {
            super(initialCapacity);
            maxCapacity = initialCapacity;
        }

        @Override
        public boolean add(T object) {
            if (isFull()) {
                super.remove(0);
            }
            return super.add(object);
        }

        public boolean isFull() {
            return this.size() >= maxCapacity;
        }
    }

    public class DebugInfo {
        private final LimitedArrayList<Long> times;
        private final LimitedArrayList<Long> averageTimes;

        public DebugInfo(int timeCapacity, int averageCapacity) {
            this.times = new LimitedArrayList<>(timeCapacity);
            this.averageTimes = new LimitedArrayList<>(averageCapacity);
        }

        public void addTime(long time) {
            times.add(time);
            if (times.isFull()) {
                long average = times.stream().mapToLong(Long::longValue).sum() / times.size();
                times.clear();
                averageTimes.add(average);
            }
        }

        /**
         * @return The most recent measurement stored.
         * */
        public long getCurrent() {
            if (times.isEmpty())
                return -1L;
            return times.get(times.size() - 1);
        }

        public LimitedArrayList<Long> getAverageTimes() {
            return averageTimes;
        }
    }
}
