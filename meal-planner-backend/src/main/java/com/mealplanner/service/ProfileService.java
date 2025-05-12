package com.mealplanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mealplanner.model.UserProfiles;
import com.mealplanner.repository.ProfileRepository;

import jakarta.transaction.Transactional;

@Service
public class ProfileService {
	
	@Autowired
	private ProfileRepository profileRepository;
	
	public UserProfiles getByUserId(Long userId) {
		
		return profileRepository.findByUserId(userId);
	}
	
	
	public UserProfiles upsertProfile(Long userId,UserProfiles dto ) {
       UserProfiles existing = profileRepository.findByUserId(userId);
       if(existing == null) {
    	   dto.setUserId(userId);
    	   return profileRepository.save(dto);
       }
       existing.setFullName(dto.getFullName());
       existing.setAge(dto.getAge());
       existing.setHeight(dto.getHeight());
       existing.setWeight(dto.getWeight());
       
       existing.setCalorieGoal(dto.getCalorieGoal());
       existing.setProteinGoal(dto.getProteinGoal());
       existing.setVegetarian(dto.getVegetarian());
       existing.setMeatEater(dto.getMeatEater());
       
       return profileRepository.save(existing);
    }

}
