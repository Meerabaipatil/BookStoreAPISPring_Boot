package org.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.demo.Entity.Admin;
import org.demo.Repository.AdminRepository;
import org.demo.Repository.UserRepository;
import org.demo.Service.AdminService;
import org.demo.Service.BookServiceImpl;
import org.demo.Service.EmailService;
import org.demo.SpringSecurity.JwtUtil;
import org.demo.responseStructure.ResponseStructure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminServiceTest {

	@InjectMocks
	private AdminService adminService;

	@Mock
	private AdminRepository adminRepo;

	@Mock
	private UserRepository userRepo;

	@Mock
	private PasswordEncoder encoder;

	@Mock
	private EmailService emailService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private BookServiceImpl bookService;

	private Admin admin;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		admin = new Admin();
		admin.setId(1L);
		admin.setEmail("admin@test.com");
		admin.setPassword("pass123");
		admin.setRole("ADMIN");
	}

	// ================= REGISTER TEST =================
	@Test
	void testRegisterAdminAlreadyExists() {
		when(adminRepo.existsByEmail(admin.getEmail())).thenReturn(true);

		ResponseEntity<ResponseStructure<Admin>> response = adminService.register(admin);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody().getMessage()).contains(" User already exists");
	}

	@Test
	void testRegisterAdminSuccess() {
		when(adminRepo.existsByEmail(admin.getEmail())).thenReturn(false);
		when(encoder.encode(admin.getPassword())).thenReturn("encodedPass");
		when(adminRepo.save(any(Admin.class))).thenReturn(admin);

		ResponseEntity<ResponseStructure<Admin>> response = adminService.register(admin);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody().getData().getPassword()).isEqualTo("......."); 
	}

	// ================= LOGIN TEST =================
	@Test
	void testLoginInvalidCredentials() {
		when(adminRepo.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
		when(encoder.matches("wrongpass", admin.getPassword())).thenReturn(false);

		ResponseEntity<ResponseStructure<String>> response = adminService.login(admin.getEmail(), "wrongpass");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody().getMessage()).contains("Authentication required,Please Check your Email or password");
	}

	@Test
	void testLoginSuccess() {
		when(adminRepo.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
		when(encoder.matches("pass123", admin.getPassword())).thenReturn(true);
		when(jwtUtil.generateToken(admin.getEmail(), admin.getRole())).thenReturn("token123");

		ResponseEntity<ResponseStructure<String>> response = adminService.login(admin.getEmail(), "pass123");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getData()).isEqualTo("token123");
	}
}
