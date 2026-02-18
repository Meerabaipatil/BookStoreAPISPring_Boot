package org.demo.ExceptionHandling;

import java.util.HashMap;
import java.util.Map;

import org.demo.responseStructure.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandling {
	
	


	@ExceptionHandler(BookIdNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleProductIdNotFound(BookIdNotFoundException e) {

		ResponseStructure<String> structure = new ResponseStructure<>();
		structure.setStatusCode(HttpStatus.NOT_FOUND.value());
		structure.setMessage(e.getMsg());
		structure.setData(null);

		return new ResponseEntity<>(structure, HttpStatus.OK);

	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResponseStructure<Map<String, String>>> handleHibernateValidation(
			ConstraintViolationException e) {

		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation violation : e.getConstraintViolations()) {
			String field = violation.getPropertyPath().toString();
			String message = violation.getMessage();
			errors.put(field, message);
		}

		ResponseStructure<Map<String, String>> structure = new ResponseStructure<>();
		structure.setStatusCode(HttpStatus.BAD_REQUEST.value());
		structure.setMessage("Validation Failed");
		structure.setData(errors);
		return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ResponseStructure<String>> handleUserExists(UserAlreadyExistsException ex) {
		ResponseStructure<String> structure = new ResponseStructure<>();
		structure.setStatusCode(HttpStatus.ALREADY_REPORTED.value());
		structure.setMessage(ex.getMsg());
		structure.setData(null);
		return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
	}

}
