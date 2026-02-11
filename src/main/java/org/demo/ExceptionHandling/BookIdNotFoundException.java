package org.demo.ExceptionHandling;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookIdNotFoundException extends RuntimeException {
	
	private String msg;
	
	

}
