package com.branwilliams.bundi.engine.core.event;

import com.branwilliams.bundi.engine.core.Lockable;
import com.branwilliams.bundi.tukio.Event;

/**
 * @author Brandon
 * @since August 17, 2019
 */
public class LockableStateUpdateEvent implements Event {

    private Lockable lockable;

    private boolean oldState;

    public LockableStateUpdateEvent(Lockable lockable, boolean oldState) {
        this.lockable = lockable;
        this.oldState = oldState;
    }

    public Lockable getLockable() {
        return lockable;
    }

    public boolean isOldState() {
        return oldState;
    }

}
