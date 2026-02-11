package org.demo.ModelMapper;

import org.demo.DTO.BookDTO;
import org.demo.Entity.Book;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

	public Book converIntoEntity(BookDTO dto) {
		Book book = new Book();
		book.setId(dto.getId());
		book.setTitle(dto.getTitle());
		book.setPrice(dto.getPrice());
		book.setAuthor(dto.getAuthor());
		book.setPublishesAt(dto.getPublishesAt());
		book.setCreatedAt(dto.getCreatedAt());
		book.setUpdatedAt(dto.getUpdatedAt());

		return book;
	}

	public BookDTO convertIntoDTO(Book book) {
		BookDTO dto = new BookDTO();
		 dto.setId(book.getId());
		dto.setTitle(book.getTitle());
		dto.setAuthor(book.getAuthor());
		dto.setPrice(book.getPrice());
		dto.setCreatedAt(book.getCreatedAt());
		dto.setUpdatedAt(book.getUpdatedAt());
		dto.setPublishesAt(book.getPublishesAt());

		return dto;
	}

}
