package org.demo.responseStructure;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class ResponseStructure<T> {

	private int statusCode;
	private String Message;
	private T data;
}
