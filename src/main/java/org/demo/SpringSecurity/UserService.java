package org.demo.SpringSecurity;

import java.util.Random;

import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO dao;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtService jwtService;

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public ResponseEntity<ResponseStructure<User>> register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        User saved = dao.register(user);

        ResponseStructure<User> rs = new ResponseStructure<>();
        rs.setStatusCode(201);
        rs.setMessage("Registered");
        rs.setData(saved);
        return ResponseEntity.ok(rs);
    }

    public ResponseEntity<ResponseStructure<String>> login(String email, String password) {
        User user = dao.findByMail(email);

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(email);
        System.out.println(token);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(200);
        rs.setMessage("Login success");
        rs.setData(token);

        return ResponseEntity.ok(rs);
    }

    public ResponseEntity<ResponseStructure<String>> forgotPassword(String email) {
        User user = dao.findByMail(email);

        String otp = generateOtp();
        user.setOtp(otp);
        dao.register(user);
        emailService.sendOtp(email, otp);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(200);
        rs.setMessage("OTP sent");
        rs.setData("Check mail");

        return ResponseEntity.ok(rs);
    }

    public ResponseEntity<ResponseStructure<String>> resetPassword(String email, String otp, String newPassword) {
        User user = dao.findByMail(email);

        if (!otp.equals(user.getOtp())) {
            throw new RuntimeException("Wrong OTP");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setOtp(null);
        dao.register(user);

        ResponseStructure<String> rs = new ResponseStructure<>();
        rs.setStatusCode(200);
        rs.setMessage("Password reset");

        return ResponseEntity.ok(rs);
    }
}
