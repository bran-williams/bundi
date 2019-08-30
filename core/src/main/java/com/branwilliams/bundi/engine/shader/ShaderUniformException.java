package com.branwilliams.bundi.engine.shader;

/**
 * Created by Brandon Williams on 11/17/2018.
 */
public class ShaderUniformException extends Throwable {
    public ShaderUniformException(String message) {
        super(message);
    }

    public ShaderUniformException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderUniformException(Throwable cause) {
        super(cause);
    }

    public ShaderUniformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ShaderUniformException() {
    }
}