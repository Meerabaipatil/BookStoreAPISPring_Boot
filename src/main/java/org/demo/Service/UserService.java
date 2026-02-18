package org.demo.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.demo.DTO.BookDTO;
import org.demo.Entity.User;
import org.demo.Repository.UserRepository;
import org.demo.SpringSecurity.JwtUtil;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BookServiceImpl bookService; // to interact with books

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // ========== REGISTER ==========
    public ResponseEntity<ResponseStructure<User>> register(User user) {
        Optional<User> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            ResponseStructure<User> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.CONFLICT.value());
            rs.setMessage("User already exists with email: " + user.getEmail());
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(rs);
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(user.getEmail().split("@")[0]);
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("USER");

        User saved = repo.save(user);
        saved.setPassword(".......");

        ResponseStructure<User> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.CREATED.value());
        rs.setMessage("User registered successfully");
        rs.setData(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    // ========== LOGIN ==========
    public ResponseEntity<ResponseStructure<String>> login(String email, String password) {
        Optional<User> opt = repo.findByEmail(email);

        if (opt.isEmpty() || !encoder.matches(password, opt.get().getPassword())) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            rs.setMessage("Authentication failed: check email or password");
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(rs);
        }

        User user = opt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("Login successful, Welcome " + user.getUsername());
        rs.setData(token);

        return ResponseEntity.ok(rs);
    }

    // ========== FORGOT PASSWORD ==========
    public ResponseEntity<ResponseStructure<String>> forgotPassword(String email) {
        Optional<User> opt = repo.findByEmail(email);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("User not found with email: " + email);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        User user = opt.get();
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(300).toEpochMilli());
        repo.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("OTP sent to your email");
        rs.setData(user.getEmail());

        return ResponseEntity.ok(rs);
    }

    // ========== RESET PASSWORD ==========
    public ResponseEntity<ResponseStructure<String>> resetPassword(String email, String otp, String newPassword) {
        Optional<User> opt = repo.findByEmail(email);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("User not found with email: " + email);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        User user = opt.get();

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
            rs.setMessage("Invalid OTP");
            rs.setData(null);
            return ResponseEntity.badRequest().body(rs);
        }

        if (user.getOtpExpiry() < Instant.now().toEpochMilli()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
            rs.setMessage("OTP expired");
            rs.setData(null);
            return ResponseEntity.badRequest().body(rs);
        }

        user.setPassword(encoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        repo.save(user);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("Password reset successful");
        rs.setData(email);

        return ResponseEntity.ok(rs);
    }

    // ========== BORROW BOOK ==========
    public ResponseEntity<ResponseStructure<BookDTO>> borrowBook(int bookId, Long userId) {
        return bookService.borrowBook(bookId, userId);
    }

    // ========== LIST ALL BOOKS ==========
    public ResponseEntity<ResponseStructure<List<BookDTO>>> getAllBooks() {
        return bookService.findAll(Pageable.unpaged(), null, null, null, null, null);
    }
}
