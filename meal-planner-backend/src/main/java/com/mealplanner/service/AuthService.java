package com.mealplanner.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mealplanner.model.Users;
import com.mealplanner.repository.UserRepository;
import com.mealplanner.security.JwtTokenProvider;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public Users register(String username,String email,String rawPassword) {
		Users u = new Users();
		u.setUsername(username);
		u.setEmail(email);
		u.setPassword(passwordEncoder.encode(rawPassword));
		u.setRoles(Set.of("ROLE_USER"));
		
		return userRepository.save(u);
	}
	
//	public Users findByUsername(String username) {
//		
//		return userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User Not Found"));
//	}
	
	public String login(String username,String rawPassword) {
		Users u = userRepository.findByUsername(username)
				.orElseThrow(()-> new RuntimeException("使用者不存在"));
		if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
            throw new RuntimeException("密碼不正確");
        }
        return jwtTokenProvider.createToken(u.getId(), u.getRoles());
	}
}
