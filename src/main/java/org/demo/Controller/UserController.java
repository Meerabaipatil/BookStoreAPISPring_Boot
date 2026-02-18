package org.demo.Controller;

import java.util.List;

import org.demo.DTO.BookDTO;
import org.demo.Entity.User;
import org.demo.Service.UserService;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    // ========== REGISTER ==========
    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<User>> register(@RequestBody User user) {
        return service.register(user);
    }

    // ========== LOGIN ==========
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<String>> login(
            @RequestParam String email,
            @RequestParam String password) {
        return service.login(email, password);
    }

    // ========== FORGOT PASSWORD ==========
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseStructure<String>> forgot(@RequestParam String email) {
        return service.forgotPassword(email);
    }

    // ========== RESET PASSWORD ==========
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseStructure<String>> reset(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        return service.resetPassword(email, otp, newPassword);
    }

    // ========== BORROW BOOK ==========
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/books/borrow")
    public ResponseEntity<ResponseStructure<BookDTO>> borrowBook(
            @RequestParam int bookId,
            @RequestParam Long userId) {
        return service.borrowBook(bookId, userId);
    }

    // ========== LIST ALL BOOKS ==========
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/books/all")
    public ResponseEntity<ResponseStructure<List<BookDTO>>> getAllBooks() {
        return service.getAllBooks();
    }
}
