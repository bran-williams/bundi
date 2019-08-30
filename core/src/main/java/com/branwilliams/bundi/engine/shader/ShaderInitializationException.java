package com.branwilliams.bundi.engine.shader;

/**
 * Thrown during the initialization of a shader program.
 * Created by Brandon Williams on 11/17/2018.
 */
public class ShaderInitializationException extends Throwable {
    public ShaderInitializationException(String message) {
        super(message);
    }

    public ShaderInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderInitializationException(Throwable cause) {
        super(cause);
    }

    public ShaderInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ShaderInitializationException() {
    }
}