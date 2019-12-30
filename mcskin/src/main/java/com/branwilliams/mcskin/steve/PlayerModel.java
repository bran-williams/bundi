package com.branwilliams.mcskin.steve;

import com.branwilliams.bundi.engine.shader.Material;

/**
 * @author Brandon
 * @since December 18, 2019
 */
public class PlayerModel extends HumanoidModel {

    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
//    private final ModelPart cloak;
//    private final ModelPart ear;

    public PlayerModel(Material material, float scale) {
        super(material, scale);

        //(this.ear = new ModelPart(material, 24, 0)).setBounds(-3.0f, -6.0f, -1.0f, 6, 6, 1, scale);
        //(this.cloak = new ModelPart(material, 0, 0)).setTexSize(64, 32);
        //this.cloak.setBounds(-5.0f, 0.0f, -1.0f, 10, 16, 1, scale);
//        boolean slim = false;
//
//        if (slim) {
//            (this.leftArm = new ModelPart(this, 32, 48)).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, float1);
//            this.leftArm.setPos(5.0f, 2.5f, 0.0f);
//            (this.rightArm = new ModelPart(this, 40, 16)).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, float1);
//            this.rightArm.setPos(-5.0f, 2.5f, 0.0f);
//            (this.leftSleeve = new ModelPart(this, 48, 48)).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, float1 + 0.25f);
//            this.leftSleeve.setPos(5.0f, 2.5f, 0.0f);
//            (this.rightSleeve = new ModelPart(this, 40, 32)).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, float1 + 0.25f);
//            this.rightSleeve.setPos(-5.0f, 2.5f, 10.0f);
//        }
//        else {
        this.leftArm = new ModelPart(material, 32, 48);
        this.leftArm.setBounds(-1.0f, -2.0f, -2.0f, 4, 12, 4, scale);
        this.leftArm.setPosition(5.0f, 2.0f, 0.0f);
//        this.addModelPart(this.leftArm);

        this.leftSleeve = new ModelPart(material, 48, 48);
        this.leftSleeve.setBounds(-1.0f, -2.0f, -2.0f, 4, 12, 4, scale + 0.25f);
        this.leftSleeve.setPosition(5.0f, 2.0f, 0.0f);
        this.addModelPart(this.leftSleeve);

        this.rightSleeve = new ModelPart(material, 40, 32);
        this.rightSleeve.setBounds(-3.0f, -2.0f, -2.0f, 4, 12, 4, scale + 0.25f);
        this.rightSleeve.setPosition(-5.0f, 2.0f, 10.0f);
        this.addModelPart(this.rightSleeve);
//        }

        this.leftLeg = new ModelPart(material, 16, 48);
        this.leftLeg.setBounds(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.leftLeg.setPosition(1.9f, 12.0f, 0.0f);
//        this.addModelPart(this.leftLeg);

        this.leftPants = new ModelPart(material, 0, 48);
        this.leftPants.setBounds(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale + 0.25f);
        this.leftPants.setPosition(1.9f, 12.0f, 0.0f);
        this.addModelPart(this.leftPants);

        this.rightPants = new ModelPart(material, 0, 32);
        this.rightPants.setBounds(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale + 0.25f);
        this.rightPants.setPosition(-1.9f, 12.0f, 0.0f);
        this.addModelPart(this.rightPants);

        this.jacket = new ModelPart(material, 16, 32);
        this.jacket.setBounds(-4.0f, 0.0f, -2.0f, 8, 12, 4, scale + 0.25f);
        this.jacket.setPosition(0.0f, 0.0f, 0.0f);
        this.addModelPart(this.jacket);
    }
}
