package com.mealplanner.contorller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealplanner.model.UserProfiles;
import com.mealplanner.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
	
	@Autowired
	private ProfileService profileService;
	
	@GetMapping
	public ResponseEntity<?> getProfile(Authentication auth){
		Long userId = Long.valueOf(auth.getName());
		return ResponseEntity.ok(profileService.getByUserId(userId));
	}
	
	@PutMapping
	public ResponseEntity<?> updateProfile(Authentication auth,@RequestBody UserProfiles body){
		body.setUserId(Long.valueOf(auth.getName()));
		Long id = Long.valueOf(auth.getName());
		UserProfiles saved = profileService.upsertProfile(id, body);
		return ResponseEntity.ok(saved);
	}
}
