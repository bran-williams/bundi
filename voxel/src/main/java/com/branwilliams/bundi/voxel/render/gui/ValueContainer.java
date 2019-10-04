package com.branwilliams.bundi.voxel.render.gui;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.components.Label;
import com.branwilliams.bundi.gui.api.components.Slider;
import com.branwilliams.bundi.gui.api.layouts.GridLayout;

import java.awt.*;
import java.util.Random;

/**
 * @author Brandon
 * @since May 15, 2019
 */
public class ValueContainer extends Container {

    private final Label title, description;

    private final Slider slider;

    private final Value value;

    public ValueContainer(Value value, FontData titleFont, FontData descriptionFont) {
        super("hmmmmm");

        // Using a grid layout with infinite rows and three columns ensures that the components are stacked upon each other.
        this.setLayout(new GridLayout(3, 1, 2, 2,4));
        this.setUseLayoutSize(true);
        this.setAutoLayout(true);
        this.value = value;
        this.add(title = new Label("title", String.format("%s (%.1f%s)", value.getName(), value.getValue(), value.getCarot())));
        this.title.setFont(titleFont);

        this.add(description = new Label("description", value.getDescription()));
        this.description.setFont(descriptionFont);
        this.description.setColor(new Color(118, 118, 118));

        this.add(slider = new Slider("slider"));
        // Set the default dimensions of the slider as well as updating it's percentage to reflect the value node.
        this.slider.setSize(150, 18);
        this.slider.setSliderPercentage((value.getValue() - value.getMinValue()) / (value.getMaxValue() - value.getMinValue()));
        this.slider.onValueChange((slider1 -> {
            // Calculate the value the slider's percentage would create (before the minimum value)
            float calculatedValue = (slider.getSliderPercentage() * (value.getMaxValue() - value.getMinValue()));

            // Use the modulus operator to trim the excess value from the slider percentage.
            this.value.setValue(value.getMinValue() + calculatedValue - (value.getIncrementValue() == -1 ? 0 : calculatedValue % value.getIncrementValue()));

            // Update the slider percentage with the values current value, as it may differ if there were excess value.
            this.slider.setSliderPercentage((value.getValue() - value.getMinValue()) / (value.getMaxValue() - value.getMinValue()));

            // Update the title label to coincide with any value change.
            this.title.setText(String.format("%s (%.1f%s)", value.getName(), value.getValue(), value.getCarot()));
        }));
        this.layout();
    }

    /**
     * A node which contains a float value with a minimum and maximum value. <br/>
     * Each value can contain a carot which represents the type of data this float value represents.
     * */
    public static class Value {

        private final String name;

        private final String description;

        private final String carot;

        private float minValue, value, maxValue, incrementValue;

        public Value(String name, String carot, float minValue, float value, float maxValue, String description) {
            this(name, carot, minValue, value, maxValue, -1, description);
        }

        public Value(String name, float minValue, float value, float maxValue, String description) {
            this(name, "", minValue, value, maxValue, -1, description);
        }

        public Value(String name, float minValue, float value, float maxValue, float incrementValue, String description) {
            this(name, "", minValue, value, maxValue, incrementValue, description);
        }

        public Value(String name, String carot, float minValue, float value, float maxValue, float incrementValue, String description) {
            this.name = name;
            this.carot = carot;
            this.minValue = minValue;
            this.value = value;
            this.maxValue = maxValue;
            this.incrementValue = incrementValue;
            this.description = description;
        }

        public String getCarot() {
            return carot;
        }

        public float getMinValue() {
            return minValue;
        }

        public void setMinValue(float minValue) {
            this.minValue = minValue;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
            if (this.value < minValue)
                this.value = minValue;
            if (this.value > maxValue)
                this.value = maxValue;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        /**
         * @return a randomly generated value between the min and max of this value object.
         * */
        public float getRandom(Random random) {
            return (minValue + maxValue * random.nextFloat());
        }

        public float getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(float maxValue) {
            this.maxValue = maxValue;
        }

        public float getIncrementValue() {
            return incrementValue;
        }

        public void setIncrementValue(float incrementValue) {
            this.incrementValue = incrementValue;
        }

    }
}
