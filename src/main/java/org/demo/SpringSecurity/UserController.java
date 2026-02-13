package org.demo.SpringSecurity;

import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserController {

   @Autowired
   private UserService service;
   
   @PostMapping("/register")
   public ResponseEntity<ResponseStructure<User>> register(@RequestBody User user) {
       return service.register(user);
   }

   @PostMapping("/login")
   public ResponseEntity<ResponseStructure<String>> login(@RequestParam String email,
                                                         @RequestParam String password) {
       return service.login(email, password);
   }

   @PostMapping("/forgot-password")
   public ResponseEntity<ResponseStructure<String>> forgotPassword(@RequestParam String email) {
       return service.forgotPassword(email);
   }
   @PostMapping("/reset-password")
   public ResponseEntity<ResponseStructure<String>> resetPassword(@RequestParam String email,
                                                                  @RequestParam String otp,
                                                                  @RequestParam String newPassword) {
       return service.resetPassword(email, otp, newPassword);
   }
}
