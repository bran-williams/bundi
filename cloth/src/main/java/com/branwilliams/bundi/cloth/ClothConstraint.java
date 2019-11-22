package com.branwilliams.bundi.cloth;

import org.joml.Vector3f;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothConstraint {

    ClothParticle particle1;

    ClothParticle particle2;

    private float restDistance;

    public ClothConstraint(ClothParticle particle1, ClothParticle particle2) {
        this.particle1 = particle1;
        this.particle2 = particle2;
        this.restDistance = particle1.getPosition().distance(particle2.getPosition());
    }

    public void satisfyConstraint() {
        Vector3f particle1ToParticle2 = particle2.getPosition().sub(particle1.getPosition(), new Vector3f());
        float currentDistance = particle1ToParticle2.length();
        Vector3f correctionVector = particle1ToParticle2.mul(1 - restDistance / currentDistance);
        Vector3f correctionVectorHalf = correctionVector.mul(0.5F);
        particle1.offsetPosition(correctionVectorHalf);
        particle2.offsetPosition(correctionVectorHalf.negate());
    }
}
