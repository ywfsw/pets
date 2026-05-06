package com.tox.tox.pets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", ex.getMessage());
        result.put("code", ex.getCode());
        return ResponseEntity.status(ex.getCode()).body(result);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        result.put("error", "参数校验失败");
        result.put("details", errors);
        result.put("code", 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理 IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", ex.getMessage());
        result.put("code", 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理所有其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        Map<String, Object> result = new HashMap<>();
        // 不泄漏具体异常信息，仅返回通用错误
        result.put("error", "服务器内部错误，请稍后重试");
        result.put("code", 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
