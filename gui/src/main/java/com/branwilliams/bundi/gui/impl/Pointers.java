package com.branwilliams.bundi.gui.impl;

import com.branwilliams.bundi.engine.core.Nameable;

/**
 * Pointers used by the toolbox. <br/>
 * Created by Brandon Williams on 3/15/2017.
 */
public enum Pointers implements Nameable {
    // DINOSAUR, RELIC
    COLOR_HIGHLIGHT("color_highlight",
            "Highlight color used within the GUI."),

    COLOR_DEFAULT("color_default", "Default color used by components within the GUI."),

    COLOR_BACKGROUND("color_background",
            "Background color used within the GUI."),
    COLOR_SECONDARY_BACKGROUND("color_secondary_background",
            "Secondary background color used within the GUI."),
    COLOR_TERTIARY_BACKGROUND("color_tertiary_background",
            "Tertiary background color used within the GUI."),

    COLOR_ENABLED_TEXT("color_enabled_text",
            "Color used for enabled text within the GUI"),
    COLOR_DISABLED_TEXT("color_disabled_text",
            "Color used for disabled text within the GUI."),




    // NEW-AGE
    COLOR_DEFAULT_TEXT("color_default_text", "Default color used by text."),

    COLOR_DEFAULT_BUTTON_INACTIVE("color_default_button_inactive", "Default color for an inactive button used within the GUI."),
    COLOR_DEFAULT_BUTTON_ACTIVE("color_default_button_active", "Default color for an active button used within the GUI."),

    COLOR_DEFAULT_BUTTON_TEXT_INACTIVE("color_default_button_text_inactive", "Default color for an inactive button's text used within the GUI."),
    COLOR_DEFAULT_BUTTON_TEXT_ACTIVE("color_default_button_text_active", "Default color for an active button's text used within the GUI."),





    FONT_TOOLTIP("font_tooltip", "Font used within tooltips."),

    IMAGE_ARROW("image_arrow", "Image used for the arrow of the combo box.");

    private final String name, description;

    Pointers(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
