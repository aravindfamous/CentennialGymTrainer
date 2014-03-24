package com.library;

public class DietPlan {

	private String dietInfo;
	private String size;
	private String dayOfWeek;
	private String timeOfMeal;
	
	public DietPlan() {}

	public String getDietInfo() {
		return dietInfo;
	}

	public void setDietInfo(String dietInfo) {
		this.dietInfo = dietInfo;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getTimeOfMeal() {
		return timeOfMeal;
	}

	public void setTimeOfMeal(String timeOfMeal) {
		this.timeOfMeal = timeOfMeal;
	}
}
