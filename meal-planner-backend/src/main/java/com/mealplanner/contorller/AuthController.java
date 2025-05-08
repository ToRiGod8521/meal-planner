package com.mealplanner.contorller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealplanner.dto.LoginRequest;
import com.mealplanner.model.Users;
import com.mealplanner.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired	
	private AuthService authService;
	
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Map<String,String> body){
		Users  u = authService.register(
				body.get("username"),
				body.get("email"),
				body.get("password")
				);
		return ResponseEntity.status(201).body(Map.of("id",u.getId()));
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req){
		String token = authService.login(req.getUsername(), req.getPassword());
		return ResponseEntity.ok(Map.of("token",token));
	}
	
}
