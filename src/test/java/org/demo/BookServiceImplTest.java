package org.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.demo.DTO.BookDTO;
import org.demo.Dao.BookDao;
import org.demo.Entity.Book;
import org.demo.Entity.User;
import org.demo.ExceptionHandling.BookIdNotFoundException;
import org.demo.ModelMapper.ModelMapper;
import org.demo.Repository.UserRepository;
import org.demo.Service.BookServiceImpl;
import org.demo.responseStructure.ResponseStructure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl service;

    @Mock
    private BookDao dao;

    @Mock
    private ModelMapper mapper;

    @Mock
    private UserRepository userRepo;

    private Book book;
    private BookDTO dto;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        book = new Book();
        book.setId(1);
        book.setTitle("Java");
        book.setAuthor("James");
        book.setPrice(500);
        book.setPublishesAt(LocalDate.now());

        dto = new BookDTO();
        dto.setId(1);
        dto.setTitle("Java");
        dto.setAuthor("James");
        dto.setPrice(500);
        dto.setPublishesAt(LocalDate.now());
        dto.setUserId(1L);
    }

    @Test
    void saveBook_success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.convertIntoEntity(dto, user)).thenReturn(book);
        when(dao.save(book)).thenReturn(book);
        when(mapper.convertIntoDTO(book)).thenReturn(dto);

        ResponseEntity<ResponseStructure<BookDTO>> response = service.save(dto);

        assertEquals(200, response.getStatusCode());
        assertEquals("Book saved successfully", response.getBody().getMessage());
    }

    @Test
    void findById_success() {
        when(dao.findById(1)).thenReturn(Optional.of(book));
        when(mapper.convertIntoDTO(book)).thenReturn(dto);

        ResponseEntity<ResponseStructure<BookDTO>> response = service.findById(1);

        assertEquals("Book found", response.getBody().getMessage());
    }

    @Test
    void findById_notFound() {
        when(dao.findById(1)).thenReturn(Optional.empty());

        assertThrows(BookIdNotFoundException.class, () -> service.findById(1));
    }

    @Test
    void deleteBook_success() {
        when(dao.findById(1)).thenReturn(Optional.of(book));
        when(mapper.convertIntoDTO(book)).thenReturn(dto);

        ResponseEntity<ResponseStructure<BookDTO>> response = service.deleteById(1);

        assertEquals("Book deleted", response.getBody().getMessage());
    }

    @Test
    void updateBook_success() {
        when(dao.findById(1)).thenReturn(Optional.of(book));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(dao.update(book)).thenReturn(book);
        when(mapper.convertIntoDTO(book)).thenReturn(dto);

        ResponseEntity<ResponseStructure<BookDTO>> response = service.update(dto, 1);

        assertEquals("Book updated successfully", response.getBody().getMessage());
    }
}
