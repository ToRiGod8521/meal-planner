package com.mealplanner.contorller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealplanner.model.Recipes;
import com.mealplanner.repository.RecipeRepository;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
	
	@Autowired
	private RecipeRepository recipeRepository;
	
	@GetMapping
	public ResponseEntity<List<Recipes>> listAll(){
		List<Recipes> list = recipeRepository.findAll();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<Recipes> getById(@PathVariable Long id) {
        return recipeRepository.findById(id)
                .map(r -> ResponseEntity.ok(r))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
