package com.branwilliams.bundi.engine.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Brandon
 * @since January 25, 2020
 */
public class MathfTest {

    @Test
    public void testGetTwosPower() {
        assertEquals(-1, Mathf.getTwosPower(-1));

        assertEquals(0, Mathf.getTwosPower(1));
        assertEquals(1, Mathf.getTwosPower(2));
        assertEquals(2, Mathf.getTwosPower(4));
        assertEquals(3, Mathf.getTwosPower(8));
        assertEquals(4, Mathf.getTwosPower(16));
    }

    @Test
    public void testIsPowerOfTwo() {
        assertFalse(Mathf.isPowerOfTwo(0));
        assertTrue(Mathf.isPowerOfTwo(2));
        assertTrue(Mathf.isPowerOfTwo(4));
        assertTrue(Mathf.isPowerOfTwo(8));
        assertTrue(Mathf.isPowerOfTwo(16));
    }
}
