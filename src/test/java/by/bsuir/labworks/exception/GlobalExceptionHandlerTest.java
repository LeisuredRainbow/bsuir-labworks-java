package by.bsuir.labworks.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import by.bsuir.labworks.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private MockHttpServletRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    request = new MockHttpServletRequest();
    request.setRequestURI("/api/test");
  }

  @Test
  void handleNotFoundShouldReturn404() {
    ResponseEntity<ErrorResponseDto> response =
        handler.handleNotFound(new NoSuchElementException("Not found"), request);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertBody(response, 404, "Not Found", "Not found", "/api/test");
    assertNull(response.getBody().getValidationErrors());
  }

  @Test
  void handleIllegalArgumentShouldReturn400() {
    ResponseEntity<ErrorResponseDto> response =
        handler.handleIllegalArgument(new IllegalArgumentException("Invalid input"), request);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertBody(response, 400, "Bad Request", "Invalid input", "/api/test");
    assertNull(response.getBody().getValidationErrors());
  }

  @Test
  void handleIllegalStateShouldReturn400() {
    ResponseEntity<ErrorResponseDto> response =
        handler.handleIllegalState(new IllegalStateException("State error"), request);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertBody(response, 400, "Bad Request", "State error", "/api/test");
    assertNull(response.getBody().getValidationErrors());
  }

  @Test
  void handleDataIntegrityViolationShouldReturn409() {
    DataIntegrityViolationException ex =
        new DataIntegrityViolationException("Integrity",
            new RuntimeException("duplicate key value"));
    ResponseEntity<ErrorResponseDto> response = handler.handleDataIntegrityViolation(ex, request);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertBody(response, 409, "Conflict", "duplicate key value", "/api/test");
  }

  @Test
  void handleTypeMismatchShouldReturn400() {
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex =
        new org.springframework.web.method.annotation.MethodArgumentTypeMismatchException(
            "abc", Integer.class, "id", null, null);
    ResponseEntity<ErrorResponseDto> response = handler.handleTypeMismatch(ex, request);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertBody(response, 400, "Bad Request",
        "Invalid value for parameter 'id': abc", "/api/test");
  }

  @Test
  void handleMethodArgumentNotValidShouldReturn400() {
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "request");
    bindingResult.addError(new FieldError("request", "amount", "must be positive"));
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
    ResponseEntity<ErrorResponseDto> response =
        handler.handleMethodArgumentNotValid(ex, request);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertBody(response, 400, "Bad Request", "Validation failed", "/api/test");
    assertEquals("must be positive",
        response.getBody().getValidationErrors().get("amount"));
  }

  @Test
  void handleBindExceptionShouldReturn400() {
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "request");
    bindingResult.addError(new FieldError("request", "code", "size must be 3"));
    BindException ex = new BindException(bindingResult);
    ResponseEntity<ErrorResponseDto> response = handler.handleBindException(ex, request);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertBody(response, 400, "Bad Request", "Validation failed", "/api/test");
    assertEquals("size must be 3",
        response.getBody().getValidationErrors().get("code"));
  }

  @Test
  void handleConstraintViolationShouldReturn400() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    TestPayload payload = new TestPayload(0);
    ConstraintViolationException ex =
        new ConstraintViolationException(validator.validate(payload));
    ResponseEntity<ErrorResponseDto> response = handler.handleConstraintViolation(ex, request);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertBody(response, 400, "Bad Request", "Validation failed", "/api/test");
    String amountError = response.getBody().getValidationErrors().get("amount");
    assertThat(amountError).isNotNull();
}

  @Test
  void handleGenericExceptionShouldReturn500() {
    ResponseEntity<ErrorResponseDto> response =
        handler.handleUnexpectedException(new RuntimeException("boom"), request);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertBody(response, 500, "Internal Server Error", "Unexpected server error", "/api/test");
    assertNull(response.getBody().getValidationErrors());
  }

  @Test
  void handleEmptyResultDataAccessExceptionShouldReturn404() {
    EmptyResultDataAccessException ex = new EmptyResultDataAccessException(1);
    ResponseEntity<ErrorResponseDto> response = handler.handleNotFound(ex, request);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private void assertBody(ResponseEntity<ErrorResponseDto> response, int status, String error,
                          String message, String path) {
    ErrorResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(status, body.getStatus());
    assertEquals(error, body.getError());
    assertEquals(message, body.getMessage());
    assertEquals(path, body.getPath());
    assertNotNull(body.getTimestamp());
  }

  private static class TestPayload {
    @Min(1)
    private final int amount;

    TestPayload(int amount) {
      this.amount = amount;
    }
  }
}