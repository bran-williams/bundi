package com.branwilliams.bundi.engine.core.window;

/**
 * This exception is thrown when GLFW is unable to initialize.
 * */
public class WindowInitializationException extends RuntimeException {

    public WindowInitializationException() {
    }

    public WindowInitializationException(String message) {
        super(message);
    }

    public WindowInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WindowInitializationException(Throwable cause) {
        super(cause);
    }

    public WindowInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
