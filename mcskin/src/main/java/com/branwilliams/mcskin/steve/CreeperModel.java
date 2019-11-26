package com.branwilliams.mcskin.steve;

import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.util.Mathf;

/**
 * @author Brandon
 * @since November 26, 2019
 */
public final class CreeperModel extends MCModel {

    private ModelPart head;
    private ModelPart unused;
    private ModelPart body;
    private ModelPart leg1;
    private ModelPart leg2;
    private ModelPart leg3;
    private ModelPart leg4;


    public CreeperModel(Material material) {
        super(material);

        this.head = new ModelPart(material, 0, 0);
        this.head.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.addModelPart(this.head);

//        this.unused = new ModelPart(material, 32, 0);
//        this.unused.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F + 0.5F);
//        this.addModelPart(this.unused);

        this.body = new ModelPart(material, 16, 16);
        this.body.setBounds(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.addModelPart(this.body);

        this.leg1 = new ModelPart(material, 0, 16);
        this.leg1.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
        this.leg1.setPosition(-2.0F, 12.0F, 4.0F);
        this.addModelPart(this.leg1);

        this.leg2 = new ModelPart(material, 0, 16);
        this.leg2.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
        this.leg2.setPosition(2.0F, 12.0F, 4.0F);
        this.addModelPart(this.leg2);

        this.leg3 = new ModelPart(material, 0, 16);
        this.leg3.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
        this.leg3.setPosition(-2.0F, 12.0F, -4.0F);
        this.addModelPart(this.leg3);

        this.leg4 = new ModelPart(material, 0, 16);
        this.leg4.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
        this.leg4.setPosition(2.0F, 12.0F, -4.0F);
        this.addModelPart(this.leg4);
    }
}