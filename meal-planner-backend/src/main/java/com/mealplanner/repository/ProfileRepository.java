package com.mealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mealplanner.model.UserProfiles;

public interface ProfileRepository extends JpaRepository<UserProfiles, Long>{
	UserProfiles findByUserId(Long userId);
}
