package org.demo.Controller;

import java.time.LocalDate;
import java.util.List;

import org.demo.DTO.BookDTO;
import org.demo.Service.BookServiceImpl;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.Data;

@RestController
@Data
@RequestMapping("/book")

public class BookController {

	@Autowired
	private BookServiceImpl impl;

	// -----------------Save Book-------------//
	@PostMapping("/save")
	public ResponseEntity<ResponseStructure<BookDTO>> save(@RequestBody @Valid BookDTO dto,
	                                                       BindingResult bindingResult) {
	    if(bindingResult.hasErrors()) {
	        ResponseStructure<BookDTO> rs = new ResponseStructure<>();
	        rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        rs.setMessage(bindingResult.getFieldError().getDefaultMessage());
	        rs.setData(null);
	        return new ResponseEntity<>(rs, HttpStatus.BAD_REQUEST);
	    }

	    return impl.save(dto);
	}


	// -------------------Find Book byID-------//
	@GetMapping("/{id}")

	public ResponseEntity<ResponseStructure<BookDTO>> findById(@PathVariable int id) {
		return impl.findById(id);
	}

	// --------------delete Book By Id------------//
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<BookDTO>> deleteById(@PathVariable int id) {
		return impl.deleteById(id);
	}

	// -----------------Update Book-------------//
	@PutMapping("/{id}")
	public ResponseEntity<ResponseStructure<BookDTO>> update(@RequestBody BookDTO dto, @PathVariable int id) {
		return impl.update(dto, id);
	}

	// ------------findAll using paging,sorting and filtering---------//
	@GetMapping("/all")
	public ResponseEntity<ResponseStructure<List<BookDTO>>> findAll(
			@RequestParam(required = false, defaultValue = "1") int pageNo,
			@RequestParam(required = false, defaultValue = "5") int pageSize,
			@RequestParam(required = false, defaultValue = "id") String sortBy,
			@RequestParam(required = false, defaultValue = "ASC") String sortDir,
			@RequestParam(required = false) Integer id, @RequestParam(required = false) String title,
			@RequestParam(required = false) String author, @RequestParam(required = false) Integer price,
			@RequestParam(required = false) LocalDate publishesAt) {

		Sort sort;
		if (sortDir.equalsIgnoreCase("ASC")) {
			sort = Sort.by(sortBy).ascending();
		} else {
			sort = Sort.by(sortBy).descending();
		}

		return impl.findAll(PageRequest.of(pageNo - 1, pageSize, sort), id, title, author, price, publishesAt);
	}
	
	
	@GetMapping("/available")
	public ResponseEntity<ResponseStructure<List<BookDTO>>> availableBooks() {
	    return impl.getAvailableBooks();
	}

	@GetMapping("/borrowed")
	public ResponseEntity<ResponseStructure<List<BookDTO>>> borrowedBooks() {
	    return impl.getBorrowedBooks();
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<ResponseStructure<List<BookDTO>>> booksByUser(@PathVariable Long id) {
	    return impl.getBooksByUser(id);
	}

	@GetMapping("/price")
	public ResponseEntity<ResponseStructure<List<BookDTO>>> booksByPrice(
	        @RequestParam int min,
	        @RequestParam int max) {
	    return impl.getBooksByPriceRange(min, max);
	}

	@GetMapping("/search")
	public ResponseEntity<ResponseStructure<List<BookDTO>>> searchBooks(@RequestParam String title) {
	    return impl.searchBooksByTitle(title);
	}
	
	@GetMapping("/external")
	public ResponseEntity<String> callExternalApi() {
	    String data = impl.callExternalApi();
	    return ResponseEntity.ok(data);
	}


}
