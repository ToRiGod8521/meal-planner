package com.mealplanner.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mealplanner.model.MealPlans;

public interface MealPlanRepository extends JpaRepository<MealPlans, Long>{
	List<MealPlans> findByUserIdAndPlanDateBetween(Long userId, LocalDate start, LocalDate end);
	
	void deleteByUserIdAndPlanDateBetween(Long userId, LocalDate start, LocalDate end);
}
