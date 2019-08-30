package com.branwilliams.bundi.engine.util;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by Brandon Williams on 10/25/2018.
 */
public class PerlinNoise {

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

    public void noiseLoop2d(Vector2f start, Vector2f end, Vector2f increment, float z, Consumer<Double> noiseConsumer) {
        noiseLoop2d(start.x, start.y, end.x, end.y, increment.x, increment.y, z, noiseConsumer);
    }

    public void noiseLoop2d(Vector2f start, Vector2f end, Vector2f increment, Consumer<Double> noiseConsumer) {
        noiseLoop2d(start.x, start.y, end.x, end.y, increment.x, increment.y, noiseConsumer);
    }
    
    public void noiseLoop2d(Vector2d start, Vector2d end, Vector2d increment, double z, Consumer<Double> noiseConsumer) {
        noiseLoop2d(start.x, start.y, end.x, end.y, increment.x, increment.y, z, noiseConsumer);
    }

    public void noiseLoop2d(Vector2d start, Vector2d end, Vector2d increment, Consumer<Double> noiseConsumer) {
        noiseLoop2d(start.x, start.y, end.x, end.y, increment.x, increment.y, noiseConsumer);
    }


    public void noiseLoop2d(double startX, double startY, double endX, double endY,
                      double incrementX, double incrementY, double z, Consumer<Double> noiseConsumer) {
        for (double i = startX; i < endX; i+=incrementX) {
            for (double j = startY; j < endY; j+=incrementY) {
                    noiseConsumer.accept(noise(i, j, z));
            }
        }
    }
    public void noiseLoop2d(double startX, double startY, double endX, double endY,
                        double incrementX, double incrementY, Consumer<Double> noiseConsumer) {
        noiseLoop2d(startX, startY, endX, endY, incrementX, incrementY, 0F, noiseConsumer);
    }

    public void noiseLoop2d(Vector3f start, Vector3f end, Vector3f increment, Consumer<Double> noiseConsumer) {
        noiseLoop3d(start.x, start.y, start.z,
                end.x, end.y, end.z, increment.x,
                increment.y, increment.z, noiseConsumer);
    }


    public void noiseLoop3d(Vector3d start, Vector3d end, Vector3d increment, Consumer<Double> noiseConsumer) {
        noiseLoop3d(start.x, start.y, start.z,
                end.x, end.y, end.z, increment.x,
                increment.y, increment.z, noiseConsumer);
    }

    public void noiseLoop3d(double startX, double startY, double startZ, double endX, double endY, double endZ,
                      double incrementX, double incrementY, double incrementZ, Consumer<Double> noiseConsumer) {
        for (double i = startX; i < endX; i+=incrementX) {
            for (double j = startY; j < endY; j+=incrementY) {
                for (double k = startZ; k < endZ; k+=incrementZ) {
                    noiseConsumer.accept(noise(i, j, k));
                }
            }
        }         
    }

    public double noise(Vector3f position) {
        return noise(position.x, position.y, position.z);
    }
    
    public double noise(Vector3d position) {
        return noise(position.x, position.y, position.z);
    }

    /**
     * Something to note: all integer values may produce zero.
     * Credits go to Ken Perlin for his noise function implementation.
     * Produces values between -1 ~ 1.
     * */
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
