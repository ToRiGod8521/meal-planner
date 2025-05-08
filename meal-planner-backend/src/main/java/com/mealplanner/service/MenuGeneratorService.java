package com.mealplanner.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanner.model.MealPlans;
import com.mealplanner.model.MealType;
import com.mealplanner.model.Recipes;
import com.mealplanner.model.UserProfiles;
import com.mealplanner.repository.MealPlanRepository;
import com.mealplanner.repository.ProfileRepository;
import com.mealplanner.repository.RecipeRepository;



@Service
public class MenuGeneratorService {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private MealPlanRepository mealPlanRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Transactional
	public List<MealPlans> generateWeeklyPlan(Long userId) throws Exception{
		
		LocalDate today = LocalDate.now();
		LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate sunday = monday.plusDays(6);
		
		mealPlanRepository.deleteByUserIdAndPlanDateBetween(userId, monday, sunday);
		
		UserProfiles profile = profileRepository.findByUserId(userId);
        Map<String, Integer> prefs = objectMapper.readValue(
            profile.getDietPreferences(),
            new TypeReference<Map<String, Integer>>() {}
        );
        
        List<Recipes> all = recipeRepository.findAll();
        List<MealPlans> plans = new ArrayList<>();
        Random rand = new Random();
		
        for (int d = 0; d < 7; d++) {
            LocalDate date = monday.plusDays(d);
            for (MealType mt : MealType.values()) {
                Recipes pick = all.get(rand.nextInt(all.size()));
                MealPlans mp = new MealPlans();
                mp.setUserId(userId);
                mp.setPlanDate(date);
                mp.setMealType(mt);
                mp.setRecipeId(pick.getId());
                plans.add(mp);
            }
        }
		
        return mealPlanRepository.saveAll(plans);
	
//		UserProfiles profile = profileRepository.findByUserId(userId);
//		Map<String, Integer> prefs = objectMapper.readValue(
//	            profile.getDietPreferences(), new TypeReference<Map<String, Integer>>() {}
//	        );
//		List<Recipes> all = recipeRepository.findAll();
//		List<MealPlans> plans = new ArrayList<MealPlans>();
//		LocalDate start = LocalDate.now();
//		Random rand = new Random();
//		for(int d = 0 ; d < 7; d++ ) {
//			LocalDate date = start.plusDays(d);
//			for(MealType mt:MealType.values()) {
//				Recipes pick = all.get(rand.nextInt(all.size()));
//				MealPlans mp = new MealPlans();
//				mp.setUserId(userId);
//                mp.setPlanDate(date);
//                mp.setMealType(mt);
//                mp.setRecipeId(pick.getId());
//                plans.add(mp);
//			}
//		}
//		return mealPlanRepository.saveAll(plans);
	}
}
