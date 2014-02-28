package com.library;

public class ExercisePlan {

	private String bodyPart;
	private String exerciseName;
	private int numOfReps;
	private int numOfSets;
	private int day;
	private String workoutVideo;
	private boolean selected;
	
	public ExercisePlan() {
	}

	public String getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(String bodyPart) {
		this.bodyPart = bodyPart;
	}

	public String getExerciseName() {
		return exerciseName;
	}

	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
	}

	public int getNumOfReps() {
		return numOfReps;
	}

	public void setNumOfReps(int numOfReps) {
		this.numOfReps = numOfReps;
	}

	public int getNumOfSets() {
		return numOfSets;
	}

	public void setNumOfSets(int numOfSets) {
		this.numOfSets = numOfSets;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getWorkoutVideo() {
		return workoutVideo;
	}

	public void setWorkoutVideo(String workoutVideo) {
		this.workoutVideo = workoutVideo;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
