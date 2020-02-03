package com.branwilliams.cubes.builder.evaluators;

/**
 * @author Brandon
 * @since January 26, 2020
 */
public interface IsoEvaluator {

    float evaluate(float x, float y, float z, float isoValue);

    default IsoEvaluator andThen(IsoEvaluator next) {
        class SequentialIsoEvaluator implements IsoEvaluator {

            private final IsoEvaluator next;

            public SequentialIsoEvaluator(IsoEvaluator next) {
                this.next = next;
            }

            @Override
            public float evaluate(float x, float y, float z, float isoValue) {
                isoValue = IsoEvaluator.this.evaluate(x, y, z, isoValue);
                return next.evaluate(x, y, z, isoValue);
            }
        }

        return new SequentialIsoEvaluator(next);
    }
}
