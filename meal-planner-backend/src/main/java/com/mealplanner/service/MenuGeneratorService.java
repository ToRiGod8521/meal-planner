package com.mealplanner.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
	
	@Transactional
	public List<MealPlans> generateWeeklyPlan(Long userId) throws Exception{
		
		LocalDate today = LocalDate.now();
		LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate sunday = monday.plusDays(6);
		
		mealPlanRepository.deleteByUserIdAndPlanDateBetween(userId, monday, sunday);
		
		List<MealPlans> leftovers = mealPlanRepository.findByUserIdAndPlanDateBetween(userId, monday, sunday);
		System.out.println("Deleted week leftovers: " + leftovers.size());  // 应该打印 0
		
		UserProfiles profile = profileRepository.findByUserId(userId);
        
		int calorieGoal = profile.getCalorieGoal();
		int proteinGoal = profile.getProteinGoal();
		boolean isVegetarian = profile.getVegetarian();
		boolean isMeatEater = profile.getMeatEater();
		
		List<Recipes> all = recipeRepository.findAll();
		List<Recipes> candidates = all.stream()
//	            .filter(r -> r.getCalories() <= calorieGoal)
//	            .filter(r -> r.getProtein()  >= proteinGoal)
	            .filter(r -> !isVegetarian || Boolean.TRUE.equals(r.getVegetarian()))
	            // 如果用户选葷食者，就只要 meatEater = true，否则不过滤
	            .filter(r -> !isMeatEater  || Boolean.TRUE.equals(r.getMeatEater()))
	            .collect(Collectors.toList());
		if (candidates.isEmpty()) {
	        candidates = all;
	    }
        
        List<MealPlans> plans = new ArrayList<>();
        Random rand = new Random();
		
        for (int d = 0; d < 7; d++) {
            LocalDate date = monday.plusDays(d);
            for (MealType mt : MealType.values()) {
                Recipes pick = candidates.get(rand.nextInt(candidates.size()));
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
