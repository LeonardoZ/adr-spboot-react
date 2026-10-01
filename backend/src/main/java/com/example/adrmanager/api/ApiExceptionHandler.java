package com.example.adrmanager.api;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiProblem> validation(MethodArgumentNotValidException exception) {
		Map<String, String> fields = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(),
					(first, ignored) -> first));
		return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fields);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiProblem> missing(ResourceNotFoundException exception) {
		return response(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), Map.of());
	}

	@ExceptionHandler(ConflictException.class)
	ResponseEntity<ApiProblem> conflict(ConflictException exception) {
		return response(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage(), Map.of());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<ApiProblem> invalidParameter(IllegalArgumentException exception) {
		return response(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", exception.getMessage(), Map.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ResponseEntity<ApiProblem> invalidTypedParameter(MethodArgumentTypeMismatchException exception) {
		return response(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "Invalid " + exception.getName() + " parameter",
				Map.of());
	}

	private ResponseEntity<ApiProblem> response(HttpStatus status, String code, String message,
			Map<String, String> fields) {
		return ResponseEntity.status(status).body(new ApiProblem(Instant.now(), status.value(), code, message, fields));
	}

}
