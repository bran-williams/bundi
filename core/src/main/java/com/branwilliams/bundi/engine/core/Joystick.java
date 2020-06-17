package com.branwilliams.bundi.engine.core;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWGamepadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Joystick implements Destructible {

    public enum JoystickId {
        JOYSTICK1(GLFW_JOYSTICK_1),
        JOYSTICK2(GLFW_JOYSTICK_2),
        JOYSTICK3(GLFW_JOYSTICK_3),
        JOYSTICK4(GLFW_JOYSTICK_4),
        JOYSTICK5(GLFW_JOYSTICK_5),
        JOYSTICK6(GLFW_JOYSTICK_6),
        JOYSTICK7(GLFW_JOYSTICK_7),
        JOYSTICK8(GLFW_JOYSTICK_8),
        JOYSTICK9(GLFW_JOYSTICK_9),
        JOYSTICK10(GLFW_JOYSTICK_10),
        JOYSTICK11(GLFW_JOYSTICK_11),
        JOYSTICK12(GLFW_JOYSTICK_12),
        JOYSTICK13(GLFW_JOYSTICK_13),
        JOYSTICK14(GLFW_JOYSTICK_14),
        JOYSTICK15(GLFW_JOYSTICK_15),
        JOYSTICK16(GLFW_JOYSTICK_16),
        JOYSTICK_LAST(GLFW_JOYSTICK_LAST);

        public final int glId;

        JoystickId(int glId) {
            this.glId = glId;
        }

        public boolean isJoystickId(int glId) {
            return this.glId == glId;
        }
    }

    public enum JoystickButton {
        A(GLFW_GAMEPAD_BUTTON_A),
        B(GLFW_GAMEPAD_BUTTON_B),
        X(GLFW_GAMEPAD_BUTTON_X),
        Y(GLFW_GAMEPAD_BUTTON_Y),

        DPAD_UP(GLFW_GAMEPAD_BUTTON_DPAD_UP),
        DPAD_DOWN(GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
        DPAD_LEFT(GLFW_GAMEPAD_BUTTON_DPAD_LEFT),
        DPAD_RIGHT(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT),

        LEFT_BUMPER(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER),
        RIGHT_BUMPER(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER),

        LEFT_THUMB(GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
        RIGHT_THUMB(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB),

        START(GLFW_GAMEPAD_BUTTON_START),
        GUIDE(GLFW_GAMEPAD_BUTTON_GUIDE);


        public final int glId;

        JoystickButton(int glId) {
            this.glId = glId;
        }

        public static JoystickButton fromName(String buttonName) {
            return JoystickButton.valueOf(buttonName.trim().toUpperCase());
        }
    }

    public enum JoystickAxes {
        LEFT_X(GLFW_GAMEPAD_AXIS_LEFT_X),
        LEFT_Y(GLFW_GAMEPAD_AXIS_LEFT_Y),
        LEFT_TRIGGER(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER),

        RIGHT_X(GLFW_GAMEPAD_AXIS_RIGHT_X),
        RIGHT_Y(GLFW_GAMEPAD_AXIS_RIGHT_Y),
        RIGHT_TRIGGER(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);

        public final int glId;

        JoystickAxes(int glId) {
            this.glId = glId;
        }

        public static JoystickAxes fromName(String axisName) {
            return JoystickAxes.valueOf(axisName.trim().toUpperCase());
        }
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JoystickId joystickId;

    private final GLFWGamepadState gamepadState;

    private final GLFWGamepadState previousGamepadState;

    private final Vector2f leftAxis;

    private final Vector2f rightAxis;

    public Joystick(JoystickId joystickId) {
        this.joystickId = joystickId;
        leftAxis = new Vector2f();
        rightAxis = new Vector2f();

        this.gamepadState = GLFWGamepadState.create();
        this.previousGamepadState = GLFWGamepadState.create();
    }

    public boolean isConnected() {
        return glfwJoystickPresent(joystickId.glId);
    }

    public String getName() {
        return glfwGetJoystickName(joystickId.glId);
    }

    public FloatBuffer getAxes() {
        return glfwGetJoystickAxes(joystickId.glId);
    }

    public ByteBuffer getButtons() {
        return glfwGetJoystickButtons(joystickId.glId);
    }

    public boolean isGamepad() {
        return glfwJoystickIsGamepad(joystickId.glId);
    }

    public boolean updateGamepadState() {
        previousGamepadState.set(this.gamepadState);

        if (glfwGetGamepadState(joystickId.glId, gamepadState)) {
            boolean changed = !equals(this.previousGamepadState, this.gamepadState);
            return changed;
        } else {

            if (!isGamepad()) {
                log.info(joystickId.name() + " is not a gamepad!");
            }
            return false;
        }
    }

    private boolean equals(GLFWGamepadState gamepadState, GLFWGamepadState gamepadState1) {
        return gamepadState.axes().equals(gamepadState1.axes())
                && gamepadState.buttons().equals(gamepadState1.buttons());
    }

    public float getLeftTrigger() {
        return gamepadState.axes(JoystickAxes.LEFT_TRIGGER.glId);
    }

    public Vector2f getLeftAxis() {
        leftAxis.set(gamepadState.axes().get(JoystickAxes.LEFT_X.glId), gamepadState.axes().get(JoystickAxes.LEFT_Y.glId));
        return leftAxis;
    }

    public float getRightTrigger() {
        return gamepadState.axes(JoystickAxes.RIGHT_TRIGGER.glId);
    }

    public Vector2f getRightAxis() {
        rightAxis.set(gamepadState.axes().get(JoystickAxes.RIGHT_X.glId), gamepadState.axes().get(JoystickAxes.RIGHT_Y.glId));
        return rightAxis;
    }

    public boolean isButtonPressed(JoystickButton button) {
        return isButtonPressed(button.glId);
    }


    public boolean isButtonPressed(int buttonId) {
        return gamepadState.buttons(buttonId) == GLFW_PRESS;
    }

    public JoystickId getJoystickId() {
        return joystickId;
    }

    public static Joystick get(int jid) {
        return new Joystick(JoystickId.values()[jid]);
    }

    @Override
    public void destroy() {
        this.gamepadState.free();
        this.previousGamepadState.free();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Joystick joystick = (Joystick) o;
        return joystickId.glId == joystick.joystickId.glId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(joystickId.glId);
    }
}
