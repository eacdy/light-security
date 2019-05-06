package com.itmuch.lightsecurity.example;

import com.itmuch.lightsecurity.exception.LightSecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author itmuch.com
 */
@Slf4j
@ControllerAdvice
public class LightSecurityExceptionHandler {
    /**
     * Light Security相关异常
     *
     * @param exception 异常
     * @return 发生异常时的返回
     */
    @ExceptionHandler(value = {LightSecurityException.class})
    @ResponseBody
    public ResponseEntity<String> error(LightSecurityException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
