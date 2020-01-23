package com.branwilliams.bundi.engine.util.noise;

import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Random;

/**
 * COPYRIGHT 2002 KEN PERLIN.
 *
 * From: https://mrl.nyu.edu/~perlin/noise/
 * Paper: https://mrl.nyu.edu/~perlin/paper445.pdf
 * <br/> <br/>
 * Added by Brandon Williams on 10/25/2018.
 */
public class PerlinNoise implements Noise {

    // Permutation array. Random numbers from 0 ~ 256
    private final int p[] = new int[512];

    private final Random random;

    private final long seed;

    public PerlinNoise() {
        this(new Random().nextLong());
    }

    /**
     *
     * @param seed Used by the Random variable. The random variable shuffles the permutation used for the noise function.
     * */
    public PerlinNoise(long seed) {
        this.seed = seed;
        this.random = new Random(seed);

        // Populate 'p'.
        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = random.nextInt(256);
        }
        shufflePermutation();
    }

    /**
     * Shuffles the permutation elements.
     * */
    public void shufflePermutation() {
        /*
        * Swaps the i-th element with a random element.
        * */
        int temp = 0;
        int randomIndex = 0;
        for (int i = p.length; i > 1; i--) {
            temp = p[i - 1];
            randomIndex = this.random.nextInt(i);
            p[i - 1] = p[randomIndex];
            p[randomIndex] = temp;
        }
    }

    public double noise(Vector3f position) {
        return noise(position.x, position.y, position.z);
    }
    
    public double noise(Vector3d position) {
        return noise(position.x, position.y, position.z);
    }

    @Override
    public double noise(double x, double y) {
        return noise(x, y, 0);
    }

    /**
     * Something to note: all integer values may produce zero.
     * Credits go to Ken Perlin for his noise function implementation.
     * Produces values between -1 ~ 1.
     * */
    @Override
    public double noise(double x, double y, double z) {
        int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
                Y = (int)Math.floor(y) & 255,              // CONTAINS POINT.
                Z = (int)Math.floor(z) & 255;

        x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
        y -= Math.floor(y);                                // OF POINT IN CUBE.
        z -= Math.floor(z);

        double u = fade(x),                                // COMPUTE FADE CURVES
                v = fade(y),                               // FOR EACH OF X,Y,Z.
                w = fade(z);

        int A = p[X  ]+Y,
                AA = p[A]+Z,
                AB = p[A+1]+Z,
                B = p[X+1]+Y,
                BA = p[B]+Z,
                BB = p[B+1]+Z;

        return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),     // AND ADD
                grad(p[BA  ], x-1, y  , z   )),                        // BLENDED
                lerp(u, grad(p[AB  ], x  , y-1, z   ),                 // RESULTS
                        grad(p[BB  ], x-1, y-1, z   ))),            // FROM  8
                lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),         // CORNERS
                        grad(p[BA+1], x-1, y  , z-1 )),             // OF CUBE
                        lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
                                grad(p[BB+1], x-1, y-1, z-1 ))));
    }

    /**
     * Credits go to Ken Perlin for his noise function implementation.
     * */
    private double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }

    /**
     * Credits go to Ken Perlin for his noise function implementation.
     * */
    private double lerp(double t, double a, double b) { return a + t * (b - a); }

    /**
     * Credits go to Ken Perlin for his noise function implementation.
     * */
    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
        double u = h<8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
                v = h<4 ? y : h==12||h==14 ? x : z;
        return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
    }

    public long getSeed() {
        return seed;
    }

    public Random getRandom() {
        return random;
    }
}
