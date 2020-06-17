package com.branwilliams.mcskin.steve;

import com.branwilliams.bundi.engine.material.Material;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class HumanoidModel extends MCModel {

    public ModelPart head;
    public ModelPart headwear;
    public ModelPart body;
    public ModelPart rightArm;
    public ModelPart leftArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;

    public HumanoidModel(Material material) {
        this(material, 0.0F);
    }

    public HumanoidModel(Material material, float scale) {
        super(material);

        this.head = new ModelPart(material, 0, 0);
        this.head.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale);
        this.addModelPart(this.head);

        this.headwear = new ModelPart(material, 32, 0);
        this.headwear.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F);
        this.addModelPart(this.headwear);

        this.body = new ModelPart(material, 16, 16);
        this.body.setBounds(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
        this.addModelPart(this.body);

        this.rightArm = new ModelPart(material, 40, 16);
        this.rightArm.setBounds(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale);
        this.rightArm.setPosition(-5.0F, 2.0F, 0.0F);
        this.addModelPart(this.rightArm);

        this.leftArm = new ModelPart(material, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.setBounds(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
        this.leftArm.setPosition(5.0F, 2.0F, 0.0F);
        this.addModelPart(this.leftArm);

        this.rightLeg = new ModelPart(material, 0, 16);
        this.rightLeg.setBounds(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.rightLeg.setPosition(-2.0F, 12.0F, 0.0F);
        this.addModelPart(this.rightLeg);

        this.leftLeg = new ModelPart(material, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.setBounds(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.leftLeg.setPosition(2.0F, 12.0F, 0.0F);
        this.addModelPart(this.leftLeg);
    }
}
