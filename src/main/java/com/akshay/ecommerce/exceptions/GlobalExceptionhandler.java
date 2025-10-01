package com.akshay.ecommerce.exceptions;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionhandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        ValidationErrorResponse response = new ValidationErrorResponse("Validation failing", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> handleDbUnique(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Duplicate email/contact"));
    }

    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<Map<String, Object>> handleRequiredMissing(RequiredFieldMissingException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidUuidLengthException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidUuidLength(InvalidUuidLengthException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidUuid(InvalidUuidException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }



    private ResponseEntity<Map<String,Object>> body(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(Map.of("message", msg));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidToken(InvalidTokenException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(InvalidContactNumerException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidContactNumber(InvalidContactNumerException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String,Object>> handleTokenExpired(TokenExpiredException ex) {
        return body(HttpStatus.GONE, ex.getMessage());
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<Map<String,Object>> handleTokenAlreadyUsed(TokenAlreadyUsedException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(jakarta.validation.ValidationException.class)
    public ResponseEntity<Map<String,Object>> handleValidationException(jakarta.validation.ValidationException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(NotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(InvalidGSTNumerException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(InvalidGSTNumerException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }


    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicate(DuplicateException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<Map<String,Object>> handlePasswordMismatch(PasswordMismatchException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PermissionDenied.class)
    public ResponseEntity<Map<String,Object>> PermissionDenied(PermissionDenied ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Does not support");
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = ex.getMethod() + " method is not supported for this request.";
        return new ResponseEntity<>(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
