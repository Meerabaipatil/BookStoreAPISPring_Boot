package org.demo.Service;

import java.time.LocalDate;
import java.util.List;

import org.demo.DTO.BookDTO;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

	ResponseEntity<ResponseStructure<BookDTO>> save(BookDTO dto);

	ResponseEntity<ResponseStructure<BookDTO>> findById(int id);

	ResponseEntity<ResponseStructure<BookDTO>> deleteById(int id);

	ResponseEntity<ResponseStructure<BookDTO>> update(BookDTO dto, int id);

	ResponseEntity<ResponseStructure<List<BookDTO>>> findAll(Pageable pageable, Integer id, String title, String author,
			Integer price, LocalDate publishesAt);

	ResponseEntity<ResponseStructure<BookDTO>> borrowBook(int bookId, Long userId);

	ResponseEntity<ResponseStructure<BookDTO>> removeUserFromBook(int bookId);
	
	ResponseEntity<ResponseStructure<List<BookDTO>>> getAvailableBooks();

    ResponseEntity<ResponseStructure<List<BookDTO>>> getBorrowedBooks();

    ResponseEntity<ResponseStructure<List<BookDTO>>> getBooksByUser(Long userId);

    ResponseEntity<ResponseStructure<List<BookDTO>>> getBooksByPriceRange(int min, int max);

    ResponseEntity<ResponseStructure<List<BookDTO>>> searchBooksByTitle(String title);
}
