package org.demo.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.DTO.BookDTO;
import org.demo.Dao.Dao;
import org.demo.Entity.Book;
import org.demo.ExceptionHandling.BookIdNotFoundException;
import org.demo.ModelMapper.ModelMapper;
import org.demo.responseStructure.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class BookServiceImpl implements BookService {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private Dao dao;
	
	private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);


	@Override
	public ResponseEntity<ResponseStructure<BookDTO>> save(BookDTO dto) {
		log.info("Saving Book", dto);
		Book book = mapper.converIntoEntity(dto);
		if (book != null) {
			Book save = dao.save(book);
			log.info("Book saved with id : "  , save.getId());
			BookDTO convertIntoDTO = mapper.convertIntoDTO(save);
			ResponseStructure<BookDTO> structure = new ResponseStructure<>();
			structure.setStatusCode(HttpStatus.OK.value());
			structure.setMessage("Book Object saved");
			structure.setData(convertIntoDTO);

			return new ResponseEntity<ResponseStructure<BookDTO>>(structure, HttpStatus.OK);

		} else {
			log.error("Book data is invalid: {} ", dto);
			ResponseStructure<BookDTO> structure = new ResponseStructure<>();
			structure.setStatusCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Book data is invalid, cannot save");
			structure.setData(null);

			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);

		}

	}

	@Override
	public ResponseEntity<ResponseStructure<BookDTO>> findById(int id) {
		log.info("Finding book with id: {}", id);
		Optional<Book> byId = dao.findById(id);
		if (byId.isPresent()) {
			Book book = byId.get();
			log.info("Book found with id: {}", id);
			BookDTO convertIntoDTO = mapper.convertIntoDTO(book);
			ResponseStructure<BookDTO> structure = new ResponseStructure<>();
			structure.setStatusCode(HttpStatus.OK.value());
			structure.setMessage("Product Object Found");
			structure.setData(convertIntoDTO);
			return new ResponseEntity<ResponseStructure<BookDTO>>(structure, HttpStatus.OK);
		} else {
			log.warn("Book not found with id: {}", id);
			throw new BookIdNotFoundException("Book not found with id: " + id);
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<BookDTO>> deleteById(int id) {
		 log.info("Deleting book with id: {}", id);
		Optional<Book> byId = dao.findById(id);
		if (byId.isPresent()) {
			Book book = byId.get();
			dao.deleteById(id);
			log.info("Book deleted with id: {}", id);
			BookDTO convertIntoDTO = mapper.convertIntoDTO(book);
			ResponseStructure<BookDTO> structure = new ResponseStructure<>();
			structure.setStatusCode(HttpStatus.OK.value());
			structure.setMessage("Book with id " + id + " deleted Successfully");
			structure.setData(convertIntoDTO);

			return new ResponseEntity<ResponseStructure<BookDTO>>(structure, HttpStatus.OK);

		} else {
			log.warn(" book not found with id: {}", id);
			throw new BookIdNotFoundException("Product not found with id: " + id);
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<BookDTO>> update(BookDTO dto, int id) {
		 log.info("Finding book with id: {}", id);
		Optional<Book> byId = dao.findById(id);
		if (byId.isPresent()) {
			Book book = byId.get();

			if (book.getCreatedAt() != null && book.getUpdatedAt() != null) {
				if (book.getUpdatedAt().isBefore(book.getCreatedAt())) {
					log.error("Updated time is there before created time for book id: {}", id);
					throw new RuntimeException("Updated time cannot be before created time");

				}
			}
			book.setTitle(dto.getTitle());
			book.setPrice(dto.getPrice());
			book.setAuthor(dto.getAuthor());
			book.setPublishesAt(dto.getPublishesAt());

			Book update = dao.update(book);
			log.info("Book updated successfully with id: {}", update.getId());
			BookDTO convertIntoDTO = mapper.convertIntoDTO(update);
			ResponseStructure<BookDTO> structure = new ResponseStructure<>();
			structure.setStatusCode(HttpStatus.OK.value());
			structure.setMessage("Book with id " + id + " Updated  Successfully");
			structure.setData(convertIntoDTO);

			return new ResponseEntity<ResponseStructure<BookDTO>>(structure, HttpStatus.OK);
		} else {
			 log.warn("Update failed. Book not found with id: {}", id);
			throw new BookIdNotFoundException("Product not found with id: " + id);

		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<BookDTO>>> findAll(Pageable pageable, Integer id, String title,
			String author, Integer price, LocalDate publishesAt) {
		log.info("Fetching all books with filters -> id: {}, title: {}, author: {}, price: {}, publishesAt: {}",
	            id, title, author, price, publishesAt);
		List<Book> all = dao.findAll(pageable, id, title, author, price, publishesAt).getContent();
		log.info("Total books fetched: {}", all.size());

		List<BookDTO> list = all.stream().map(book -> mapper.convertIntoDTO(book)).toList();

		// Build response
		ResponseStructure<List<BookDTO>> structure = new ResponseStructure<>();
		structure.setStatusCode(HttpStatus.OK.value());
		structure.setMessage("List Of Books:");
		structure.setData(list);

		return new ResponseEntity<>(structure, HttpStatus.OK);
	}

}
