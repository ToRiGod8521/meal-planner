package com.mealplanner.contorller;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealplanner.model.MealPlans;
import com.mealplanner.repository.MealPlanRepository;
import com.mealplanner.service.MenuGeneratorService;

@RestController
@RequestMapping("/api/mealplan")
public class MealPlanController {
	@Autowired
	private MenuGeneratorService menuGeneratorService;
	@Autowired
	private MealPlanRepository mealPlanRepository;
	
	@PostMapping("/generate")
	public ResponseEntity<List<MealPlans>> generateMealPlan(Principal principal) throws Exception{
		Long userId = Long.valueOf(principal.getName());
		List<MealPlans> plans = menuGeneratorService.generateWeeklyPlan(userId);
		return ResponseEntity.ok(plans);
	}
	
	@GetMapping
	public ResponseEntity<List<MealPlans>> getWeeklyPlan(Principal principal){
		Long userId = Long.valueOf(principal.getName());
		
		LocalDate today  = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
		
		List<MealPlans> plans = mealPlanRepository.findByUserIdAndPlanDateBetween(userId, monday,sunday);
		return ResponseEntity.ok(plans);
	}
}
