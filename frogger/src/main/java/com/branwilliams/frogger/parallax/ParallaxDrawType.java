package com.branwilliams.frogger.parallax;

/**
 * This enum controls the way that parallax objects are drawn.
 * */
public enum ParallaxDrawType {
    /**
     * Objects with this draw type will only render once.
     * */
    STATIC,

    /**
     * Objects with this draw type will render repeatedly past their edges.
     * */
    REPEAT
}
