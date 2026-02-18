package org.demo.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.demo.Controller.User;
import org.demo.DTO.BookDTO;
import org.demo.Entity.Admin;
import org.demo.Repository.AdminRepository;
import org.demo.Repository.UserRepository;
import org.demo.SpringSecurity.JwtUtil;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BookServiceImpl bookService; // use for book operations

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // ================= REGISTER =================
    public ResponseEntity<ResponseStructure<Admin>> register(Admin admin) {
        if (adminRepo.existsByEmail(admin.getEmail())) {
            ResponseStructure<Admin> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.CONFLICT.value());
            rs.setMessage("Admin already exists with email: " + admin.getEmail());
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(rs);
        }

        admin.setPassword(encoder.encode(admin.getPassword()));
        admin.setRole("ADMIN");

        Admin saved = adminRepo.save(admin);
        saved.setPassword(".......");

        ResponseStructure<Admin> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.CREATED.value());
        rs.setMessage("Admin registered successfully");
        rs.setData(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    // ================= LOGIN =================
    public ResponseEntity<ResponseStructure<String>> login(String email, String password) {
        Optional<Admin> opt = adminRepo.findByEmail(email);

        if (opt.isEmpty() || !encoder.matches(password, opt.get().getPassword())) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            rs.setMessage("Authentication required: check email or password");
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(rs);
        }

        Admin admin = opt.get();
        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole());

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("Admin login successful");
        rs.setData(token);

        return ResponseEntity.ok(rs);
    }

    // ================= FORGOT PASSWORD =================
    public ResponseEntity<ResponseStructure<String>> forgotPassword(String email) {
        Optional<Admin> opt = adminRepo.findByEmail(email);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("Admin not found with email: " + email);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        Admin admin = opt.get();
        String otp = generateOtp();
        admin.setOtp(otp);
        admin.setOtpExpiry(Instant.now().plusSeconds(300).toEpochMilli());
        adminRepo.save(admin);

        emailService.sendOtp(admin.getEmail(), otp);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("OTP sent to your email");
        rs.setData(admin.getEmail());

        return ResponseEntity.ok(rs);
    }

    // ================= RESET PASSWORD =================
    public ResponseEntity<ResponseStructure<String>> resetPassword(String email, String otp, String newPassword) {
        Optional<Admin> opt = adminRepo.findByEmail(email);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("Admin not found with email: " + email);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        Admin admin = opt.get();

        if (admin.getOtp() == null || !admin.getOtp().equals(otp)) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
            rs.setMessage("Invalid OTP");
            rs.setData(null);
            return ResponseEntity.badRequest().body(rs);
        }

        if (admin.getOtpExpiry() < Instant.now().toEpochMilli()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
            rs.setMessage("OTP expired");
            rs.setData(null);
            return ResponseEntity.badRequest().body(rs);
        }

        admin.setPassword(encoder.encode(newPassword));
        admin.setOtp(null);
        admin.setOtpExpiry(null);
        adminRepo.save(admin);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("Password reset successful");
        rs.setData(email);

        return ResponseEntity.ok(rs);
    }

    // ================= VIEW ALL USERS =================
    public ResponseEntity<ResponseStructure<List<User>>> getAllUsers() {
        List<User> users = userRepo.findAll();
        users.forEach(u -> u.setPassword("......."));

        ResponseStructure<List<User>> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("All users fetched");
        rs.setData(users);

        return ResponseEntity.ok(rs);
    }

    // ================= DELETE USER =================
    public ResponseEntity<ResponseStructure<String>> deleteUser(Long id) {
        Optional<User> opt = userRepo.findById(id);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("User not found with id: " + id);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        User user = opt.get();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.FORBIDDEN.value());
            rs.setMessage("Cannot delete an admin account");
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(rs);
        }

        userRepo.delete(user);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("User " + user.getUsername() + " deleted successfully");
        rs.setData(String.valueOf(id));

        return ResponseEntity.ok(rs);
    }

    // ================= BLOCK USER =================
    public ResponseEntity<ResponseStructure<String>> blockUser(Long id) {
        Optional<User> opt = userRepo.findById(id);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("User not found with id: " + id);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        User user = opt.get();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.FORBIDDEN.value());
            rs.setMessage("Cannot block an admin account");
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(rs);
        }

        user.setBlocked(true);
        userRepo.save(user);

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Account Blocked",
                "Dear " + user.getUsername() + ",\n\nYour account has been blocked by admin.\n\nRegards,\nAdmin Team"
        );

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("User blocked successfully");
        rs.setData(user.getEmail());

        return ResponseEntity.ok(rs);
    }

    // ================= SUSPEND USER =================
    public ResponseEntity<ResponseStructure<String>> suspendUser(Long id) {
        Optional<User> opt = userRepo.findById(id);

        if (opt.isEmpty()) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
            rs.setMessage("User not found with id: " + id);
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rs);
        }

        User user = opt.get();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            ResponseStructure<String> rs = new ResponseStructure<>();
            rs.setStatusCode(HttpStatus.FORBIDDEN.value());
            rs.setMessage("Cannot suspend an admin account");
            rs.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(rs);
        }

        user.setSuspended(true);
        userRepo.save(user);

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Account Suspended",
                "Dear " + user.getUsername() + ",\n\nYour account has been suspended by admin.\n\nRegards,\nAdmin Team"
        );

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("User suspended successfully");
        rs.setData(user.getEmail());

        return ResponseEntity.ok(rs);
    }

    // ================= REMOVE USER FROM BOOK =================
    public ResponseEntity<ResponseStructure<BookDTO>> removeUserFromBook(int bookId) {
        return bookService.removeUserFromBook(bookId);
    }

    // ================= WEEKLY MAIL =================
    @Scheduled(cron = "0 0 10 ? * MON")
    public void notifySuspendedUsersWeekly() {
        List<User> users = userRepo.findAllBySuspendedTrue();

        for (User user : users) {
            emailService.sendSimpleEmail(
                    user.getEmail(),
                    "Weekly Suspension Reminder",
                    "Dear " + user.getUsername() + ",\n\nYour account is still suspended.\n\nPlease contact admin.\n\nRegards,\nAdmin Team"
            );
        }
    }
}
