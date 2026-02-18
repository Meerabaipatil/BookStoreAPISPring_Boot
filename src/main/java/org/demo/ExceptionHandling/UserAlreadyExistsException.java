package org.demo.ExceptionHandling;

import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class UserAlreadyExistsException extends RuntimeException {

	
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public UserAlreadyExistsException(String msg) {
		super();
		this.msg = msg;
	}
	
	
	
}
