package com.mealplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="user_profiles")
public class UserProfiles {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_id",nullable = false)
	private Long userId;
	
	@Column(name="full_name",length = 100)
	private String FullName;
	
	private Integer age;
	
	private Double height;
	
	private Double weight;
	
	@Column(name = "calorie_goal")
	private Integer calorieGoal;
	
	@Column(name = "protein_goal")
	private Integer proteinGoal;
	
	@Column(name = "vegetarian", nullable = false)
    private Boolean vegetarian = false;
	
	@Column(name = "meat_eater", nullable = false)
    private Boolean meatEater = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getCalorieGoal() {
		return calorieGoal;
	}

	public void setCalorieGoal(Integer calorieGoal) {
		this.calorieGoal = calorieGoal;
	}

	public Integer getProteinGoal() {
		return proteinGoal;
	}

	public void setProteinGoal(Integer proteinGoal) {
		this.proteinGoal = proteinGoal;
	}

	public Boolean getVegetarian() {
		return vegetarian;
	}

	public void setVegetarian(Boolean vegetarian) {
		this.vegetarian = vegetarian;
	}

	public Boolean getMeatEater() {
		return meatEater;
	}

	public void setMeatEater(Boolean meatEater) {
		this.meatEater = meatEater;
	}
}
