package by.bsuir.labworks.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

  @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
  public ResponseEntity<ErrorResponseDto> handleNotFound(Exception ex, HttpServletRequest request) {
    LOG.warn("Not found on path={} message={}", request.getRequestURI(), ex.getMessage());
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    LOG.warn("Illegal argument on path={} message={}", request.getRequestURI(), ex.getMessage());
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {
    LOG.warn("Illegal state on path={} message={}", request.getRequestURI(), ex.getMessage());
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    String message = ex.getMostSpecificCause().getMessage();
    LOG.warn("Data integrity violation on path={} message={}", request.getRequestURI(), message);
    return buildErrorResponse(HttpStatus.CONFLICT, message, request, null);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String message = String.format("Invalid value for parameter '%s': %s",
        ex.getName(), ex.getValue());
    LOG.warn("Type mismatch on path={} message={}", request.getRequestURI(), message);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    LOG.warn("Malformed JSON on path={} message={}", request.getRequestURI(), ex.getMessage());
    String message = "Malformed JSON request. Please check your request body syntax.";
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    LOG.warn("Validation failed on path={} errors={}", request.getRequestURI(), errors);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, request, errors);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponseDto> handleBindException(BindException ex,
      HttpServletRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    LOG.warn("Bind validation failed on path={} errors={}", request.getRequestURI(), errors);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, request, errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach(violation ->
        errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
    LOG.warn("Constraint violation on path={} errors={}", request.getRequestURI(), errors);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, request, errors);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception ex,
      HttpServletRequest request) {
    LOG.error("Unexpected error on path={}", request.getRequestURI(), ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "Unexpected server error", request, null);
  }

  @ExceptionHandler(by.bsuir.labworks.exception.PartialBulkOperationException.class)
  public ResponseEntity<ErrorResponseDto> handlePartialBulkOperation(
        by.bsuir.labworks.exception.PartialBulkOperationException ex,
        HttpServletRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    details.put("savedCount", String.valueOf(ex.getSavedCount()));
    details.put("failedCount", String.valueOf(ex.getFailedCount()));
    details.putAll(ex.getFailedOperations());
    LOG.warn("Partial bulk operation failure on path={} details={}",
        request.getRequestURI(), details);
    return buildErrorResponse(
        HttpStatus.valueOf(422),
        "Some operations failed: " + ex.getMessage(),
        request,
        details);
  }

  private ResponseEntity<ErrorResponseDto> buildErrorResponse(
      HttpStatus status, String message, HttpServletRequest request,
      Map<String, String> validationErrors) {
    ErrorResponseDto response = new ErrorResponseDto(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        validationErrors
    );
    return ResponseEntity.status(status).body(response);
  }
}