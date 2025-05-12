package com.mealplanner.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanner.model.MealPlans;
import com.mealplanner.model.Recipes;
import com.mealplanner.repository.MealPlanRepository;
import com.mealplanner.repository.RecipeRepository;

@Service
public class ShoppingListService {
		@Autowired
		private MealPlanRepository mealPlamRepo;
		@Autowired
		private RecipeRepository recipeRepo;
		@Autowired
		private ObjectMapper objectMapper;
		
		public static class Item{
			public final String name;
			public final int count;
			public Item(String name,int count) {this.name=name; this.count=count;}
		}
		
		public List<Item> buildWeekList(Long userId) throws Exception{
			
			 // 1. 算本週週一～週日
	        LocalDate today  = LocalDate.now();
	        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	        LocalDate sunday = monday.plusDays(6);

	        // 2. 拿出本週的 MealPlans
	        List<MealPlans> plans = mealPlamRepo.findByUserIdAndPlanDateBetween(userId, monday, sunday);

	        // 3. 聚合 ingredients 出現次數
	        Map<String,Integer> agg = new HashMap<>();
	        for (MealPlans p : plans) {
	            Recipes r = recipeRepo.findById(p.getRecipeId()).orElse(null);
	            if (r == null) continue;

	            // 假設 ingredients 存成 ["番茄","雞蛋",...]
	            List<String> ingredients = objectMapper.readValue(
	                r.getIngredients(),
	                new TypeReference<List<String>>() {}
	            );

	            for (String ing : ingredients) {
	                agg.merge(ing, 1, Integer::sum);
	            }
	        }

	        // 4. 轉成 List<Item> 並排序回傳
	        return agg.entrySet().stream()
	            .map(e -> new Item(e.getKey(), e.getValue()))
	            .sorted(Comparator.comparing(item -> item.name))
	            .collect(Collectors.toList());
	    }
}
