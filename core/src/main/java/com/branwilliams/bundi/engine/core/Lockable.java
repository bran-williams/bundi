package com.branwilliams.bundi.engine.core;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Brandon
 * @since August 16, 2019
 */
public interface Lockable {

    /**
     * @return True if this object is set to 'locked'.
     * */
    boolean isLocked();

    /**
     * Sets the locked state of this object.
     * */
    void setLocked(boolean locked);

    /**
     * Sets the locked state of this object to true.
     * */
    default void lock() {
        setLocked(true);
    }

    /**
     * Sets the locked state of this object to false.
     * */
    default void unlock() {
        setLocked(false);
    }

    /**
     * Inverts the locked state. Toggling.
     * */
    default void toggle() {
        setLocked(!isLocked());
    }

    /**
     * @return An (immutable) unlocked Lockable.
     * */
    static Lockable unlocked() {
        return ImmutableLockable.unlocked;
    }

    /**
     * @return An (immutable) locked Lockable.
     * */
    static Lockable locked() {
        return ImmutableLockable.locked;
    }
    static Lockable ofImmutable(Supplier<Boolean> lockableState) {
            return of(lockableState, (stateConsumer) -> {});
    }

    static Lockable of(Supplier<Boolean> lockableState, Consumer<Boolean> stateConsumer) {
        return new UnlockableByLockable(lockableState, stateConsumer);
    }

    final class UnlockableByLockable implements Lockable {

        private final Supplier<Boolean> lockableState;

        private final Consumer<Boolean> stateConsumer;

        private UnlockableByLockable(Supplier<Boolean> lockableState, Consumer<Boolean> stateConsumer) {
            this.lockableState = lockableState;
            this.stateConsumer = stateConsumer;
        }

        @Override
        public boolean isLocked() {
            return lockableState.get();
        }

        @Override
        public void setLocked(boolean locked) {
            stateConsumer.accept(locked);
        }
    }

    final class ImmutableLockable implements Lockable {

        private static final ImmutableLockable locked = new ImmutableLockable(true);

        private static final ImmutableLockable unlocked = new ImmutableLockable(false);

        private final boolean state;

        private ImmutableLockable(boolean state) {
            this.state = state;
        }

        @Override
        public boolean isLocked() {
            return state;
        }

        @Override
        public void setLocked(boolean locked) {
            // Do nothing
        }
    }
}
