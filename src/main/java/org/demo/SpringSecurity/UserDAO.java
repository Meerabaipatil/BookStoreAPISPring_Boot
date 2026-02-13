package org.demo.SpringSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDAO {

	@Autowired
	private UserRepository repository;
	
	public User register(User user) {
		return repository.save(user);
	}
	
	public User findByMail(String email) {
		return repository.findByEmail(email);
	}
	
}
