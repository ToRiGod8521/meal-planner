package com.mealplanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mealplanner.model.Recipes;

public interface RecipeRepository extends JpaRepository<Recipes,Long>{
	List<Recipes> findByCaloriesBetween(int min, int max);
}
