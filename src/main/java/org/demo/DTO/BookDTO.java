package org.demo.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {

	private Integer id;
	private String title;
	private String author;
	private int price;

	private LocalDate publishesAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
