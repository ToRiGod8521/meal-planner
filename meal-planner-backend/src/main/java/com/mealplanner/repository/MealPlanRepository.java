package com.mealplanner.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.mealplanner.model.MealPlans;

public interface MealPlanRepository extends JpaRepository<MealPlans, Long>{
	
	
	
	List<MealPlans> findByUserIdAndPlanDateBetween(Long userId, LocalDate start, LocalDate end);
	
	@Modifying
	@Transactional
	void deleteByUserIdAndPlanDateBetween(Long userId, LocalDate start, LocalDate end);
}
