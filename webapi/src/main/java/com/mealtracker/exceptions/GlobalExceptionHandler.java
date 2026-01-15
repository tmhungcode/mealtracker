package com.mealtracker.exceptions;

import com.mealtracker.payloads.ErrorEnvelop;
import com.mealtracker.payloads.ErrorField;
import com.mealtracker.security.jwt.JwtValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final ErrorIdGenerator generator;

    public GlobalExceptionHandler(ErrorIdGenerator generator) {
        this.generator = generator;
    }

    @ExceptionHandler({ResourceNotFoundAppException.class})
    public ResponseEntity<ErrorEnvelop> handleNotFoundException(ResourceNotFoundAppException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorEnvelop(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEnvelop> handleBadRequestException(MethodArgumentNotValidException ex) {
        var errorFields = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorEnvelop(BadRequestAppException.commonBadInputsError(errorFields)));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorEnvelop> handleBindingException(BindException ex) {
        var errorFields = ex.getFieldErrors().stream()
                .map(fieldError -> new ErrorField(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorEnvelop(BadRequestAppException.commonBadInputsError(errorFields)));
    }

    @ExceptionHandler(BadRequestAppException.class)
    public ResponseEntity<ErrorEnvelop> handleBadRequestException(BadRequestAppException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorEnvelop(ex));
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorEnvelop> handleAuthenticationException(AuthenticationException ex) {
        if (ex.getCause() instanceof AuthenticationAppException appException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorEnvelop(appException));
        }
        log.warn("Please add a new handler for the new subclass of AuthenticationException: {}", ex.getClass());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorEnvelop(AuthenticationAppException.missingToken()));
    }

    @ExceptionHandler({InsufficientAuthenticationException.class})
    public ResponseEntity<ErrorEnvelop> handleInsufficientAuthenticationException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorEnvelop(AuthenticationAppException.missingToken()));
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ErrorEnvelop> handleBadCredentialsException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorEnvelop(AuthenticationAppException.invalidPassword()));
    }

    @ExceptionHandler({JwtValidationException.class})
    public ResponseEntity<ErrorEnvelop> handleJwtValidationException(JwtValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorEnvelop(AuthenticationAppException.invalidJwtToken(ex)));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorEnvelop> handleAuthorizationException() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorEnvelop(AuthorizationAppException.apiAccessDeniedError()));
    }

    @ExceptionHandler({AuthorizationAppException.class})
    public ResponseEntity<ErrorEnvelop> handleAuthorizationException(AuthorizationAppException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorEnvelop(ex));
    }

    @ExceptionHandler({Exception.class, InternalAppException.class})
    public ResponseEntity<ErrorEnvelop> handleUnexpectedException(Exception ex) {
        String errorId = generator.generateUniqueId();
        log.error("Please investigate the error {}", errorId, ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorEnvelop(InternalAppException.unexpectException(errorId)));
    }
}
