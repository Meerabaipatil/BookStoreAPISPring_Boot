package org.demo.Controller;

import java.util.List;

import org.demo.DTO.BookDTO;
import org.demo.Entity.Admin;
import org.demo.responseStructure.ResponseStructure;
import org.demo.Service.AdminService;
import org.demo.Service.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService service;

    @Autowired
    private BookServiceImpl bookService;

    // -------- Admin Auth --------
    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<Admin>> register(@RequestBody Admin admin) {
        return service.register(admin);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<String>> login(@RequestParam String email, @RequestParam String password) {
        return service.login(email, password);
    }

    @PostMapping("/forgot")
    public ResponseEntity<ResponseStructure<String>> forgot(@RequestParam String email) {
        return service.forgotPassword(email);
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseStructure<String>> reset(@RequestParam String email, @RequestParam String otp,
                                                           @RequestParam String newPassword) {
        return service.resetPassword(email, otp, newPassword);
    }

    // -------- Book Management --------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/books/add")
    public ResponseEntity<ResponseStructure<BookDTO>> addBook(@RequestBody BookDTO dto) {
        return bookService.save(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<ResponseStructure<BookDTO>> deleteBook(@PathVariable int id) {
        return bookService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/books/{id}/removeUser")
    public ResponseEntity<ResponseStructure<BookDTO>> removeUserFromBook(@PathVariable int id) {
        return bookService.removeUserFromBook(id);
    }

    // -------- User Management --------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ResponseStructure<List<User>>> getAllUsers() {
        return service.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteUser(@PathVariable Long id) {
        return service.deleteUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}/block")
    public ResponseEntity<ResponseStructure<String>> blockUser(@PathVariable Long id) {
        return service.blockUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}/suspend")
    public ResponseEntity<ResponseStructure<String>> suspendUser(@PathVariable Long id) {
        return service.suspendUser(id);
    }
}
