package org.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.demo.DTO.BookDTO;
import org.demo.Entity.User;
import org.demo.Repository.UserRepository;
import org.demo.Service.BookServiceImpl;
import org.demo.Service.EmailService;
import org.demo.Service.UserService;
import org.demo.SpringSecurity.JwtUtil;
import org.demo.responseStructure.ResponseStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BookServiceImpl bookService;

    // ---------- REGISTER SUCCESS ----------
    @Test
    void testRegisterSuccess() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("123");

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(encoder.encode("123")).thenReturn("ENCODED");
        when(repo.save(any(User.class))).thenReturn(user);

        ResponseEntity<ResponseStructure<User>> response = service.register(user);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody().getMessage());
    }

    // ---------- REGISTER ALREADY EXISTS ----------
    @Test
    void testRegisterAlreadyExists() {
        User user = new User();
        user.setEmail("test@gmail.com");

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        ResponseEntity<ResponseStructure<User>> response = service.register(user);

        assertEquals(409, response.getStatusCode().value());
        assertTrue(response.getBody().getMessage().contains("already exists"));
    }

    // ---------- LOGIN SUCCESS ----------
    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("ENCODED");
        user.setRole("USER");
        user.setUsername("test");

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "ENCODED")).thenReturn(true);
        when(jwtUtil.generateToken("test@gmail.com", "USER")).thenReturn("TOKEN");

        ResponseEntity<ResponseStructure<String>> response = service.login("test@gmail.com", "123");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("TOKEN", response.getBody().getData());
    }

    // ---------- LOGIN FAIL ----------
    @Test
    void testLoginFail() {
        when(repo.findByEmail("x@gmail.com")).thenReturn(Optional.empty());

        ResponseEntity<ResponseStructure<String>> response = service.login("x@gmail.com", "123");

        assertEquals(401, response.getStatusCode().value());
    }

    // ---------- FORGOT PASSWORD ----------
    @Test
    void testForgotPassword() {
        User user = new User();
        user.setEmail("test@gmail.com");

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        ResponseEntity<ResponseStructure<String>> response = service.forgotPassword("test@gmail.com");

        assertEquals(200, response.getStatusCode().value());
        verify(emailService, times(1)).sendOtp(eq("test@gmail.com"), anyString());
    }

    // ---------- RESET PASSWORD SUCCESS ----------
    @Test
    void testResetPasswordSuccess() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("1234");
        user.setOtpExpiry(Instant.now().plusSeconds(300).toEpochMilli());

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(encoder.encode("newpass")).thenReturn("ENCODED");

        ResponseEntity<ResponseStructure<String>> response =
                service.resetPassword("test@gmail.com", "1234", "newpass");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password reset successful", response.getBody().getMessage());
    }

    // ---------- RESET PASSWORD WRONG OTP ----------
    @Test
    void testResetPasswordInvalidOtp() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("9999");

        when(repo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        ResponseEntity<ResponseStructure<String>> response =
                service.resetPassword("test@gmail.com", "1234", "newpass");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid OTP", response.getBody().getMessage());
    }

    // ---------- BORROW BOOK ----------
    @Test
    void testBorrowBook() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Java");

        ResponseStructure<BookDTO> rs = new ResponseStructure<>();
        rs.setStatusCode(200);
        rs.setMessage("Book borrowed");
        rs.setData(dto);

        when(bookService.borrowBook(1, 1L))
                .thenReturn(ResponseEntity.ok(rs));

        ResponseEntity<ResponseStructure<BookDTO>> response =
                service.borrowBook(1, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Java", response.getBody().getData().getTitle());
    }

    // ---------- GET ALL BOOKS ----------
    @Test
    void testGetAllBooks() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Java");

        ResponseStructure<List<BookDTO>> rs = new ResponseStructure<>();
        rs.setStatusCode(200);
        rs.setMessage("All books fetched");
        rs.setData(List.of(dto));

        when(bookService.findAll(any(Pageable.class), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(ResponseEntity.ok(rs));

        ResponseEntity<ResponseStructure<List<BookDTO>>> response =
                service.getAllBooks();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Java", response.getBody().getData().get(0).getTitle());
    }
}
