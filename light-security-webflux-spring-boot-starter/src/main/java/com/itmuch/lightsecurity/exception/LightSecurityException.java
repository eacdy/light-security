package com.itmuch.lightsecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * light security异常
 *
 * @author itmuch.com
 */
public class LightSecurityException extends ResponseStatusException {
    public LightSecurityException(HttpStatus status) {
        super(status);
    }

    public LightSecurityException(String reason) {
        this(HttpStatus.UNAUTHORIZED, reason);
    }

    public LightSecurityException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public LightSecurityException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
