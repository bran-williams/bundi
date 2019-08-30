package com.branwilliams.bundi.engine.core;

/**
 * Anything implementing this interface has a name. <br/>
 * Created by Brandon Williams on 12/9/2017.
 */
public interface Nameable {

    /**
     * @return The name of this object.
     * */
    String getName();

    default String name() {
        return getName();
    }

}
