package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.event.LockableStateUpdateEvent;

/**
 * @author Brandon
 * @since August 17, 2019
 */
public interface LockableStateListener {

    void consume(LockableStateUpdateEvent event);

}
