package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.event.LockableStateUpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class Lock implements Lockable {

    private final List<LockableStateListener> lockableStateListeners = new ArrayList<>();

    private boolean locked;

    public Lock() {
        this(false);
    }

    public Lock(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        if (locked != this.locked) {
            final LockableStateUpdateEvent event = new LockableStateUpdateEvent(this, this.locked);
            this.locked = locked;
            lockableStateListeners.forEach((listener) -> listener.consume(event));
        }
    }

    public boolean addListener(LockableStateListener listener) {
        return lockableStateListeners.add(listener);
    }

    public boolean removeListener(LockableStateListener listener) {
        return lockableStateListeners.remove(listener);
    }
}
