package org.demo.ModelMapper;

import org.demo.DTO.BookDTO;
import org.demo.Entity.Book;
import org.demo.Entity.User;
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
		if (book.getUser() != null) {
			dto.setUserId(book.getUser().getId());
			
		}
		return dto;
	}
	
	public Book convertIntoEntity(BookDTO dto, User user) {
	    Book book = new Book();
	    book.setTitle(dto.getTitle());
	    book.setAuthor(dto.getAuthor());
	    book.setPrice(dto.getPrice());
	    book.setPublishesAt(dto.getPublishesAt());

	    if (user != null) {
	        book.setUser(user);
	    }

	    return book;
	}

}
