package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.core.Joystick;
import com.branwilliams.bundi.engine.core.Keycode;

import java.util.Map;
import java.util.Objects;

public class PlayerInputMapping {

    private Map<ControllerInput, InputMapping> map;

    public static class ControllerInput {

        private Keycode keycode;

        private Joystick joystick;

        public ControllerInput(Keycode keycode, Joystick joystick) {
            this.keycode = keycode;
            this.joystick = joystick;
        }

        public Keycode getKeycode() {
            return keycode;
        }

        public void setKeycode(Keycode keycode) {
            this.keycode = keycode;
        }

        public Joystick getJoystick() {
            return joystick;
        }

        public void setJoystick(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ControllerInput that = (ControllerInput) o;
            return Objects.equals(keycode, that.keycode) &&
                    Objects.equals(joystick, that.joystick);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keycode, joystick);
        }
    }

    public static class InputMapping {

    }

}
