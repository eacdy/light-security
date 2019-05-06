package com.itmuch.lightsecurity.exception;

/**
 * light security异常
 *
 * @author itmuch.com
 */
public class LightSecurityException extends RuntimeException {
    public LightSecurityException(Throwable cause) {
        super(cause);
    }

    public LightSecurityException(String message) {
        super(message);
    }

    public LightSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
