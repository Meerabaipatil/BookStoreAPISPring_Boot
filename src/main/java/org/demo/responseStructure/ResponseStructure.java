package org.demo.responseStructure;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class ResponseStructure<T> {

	private int statusCode;
	private String Message;
	private T data;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
