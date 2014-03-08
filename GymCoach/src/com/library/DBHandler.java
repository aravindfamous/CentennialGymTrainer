package com.library;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

	 // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "gym_coach";
 
    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_EXERCISE = "exercise";
 
    // Login Table Columns names
    private static final String KEY_USERID = "gymuserID";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_VERIFIED = "verified";
    private static final String KEY_PLANID = "planID";
    private static final String KEY_CURRENTDAY = "currentday";
    private static final String KEY_ALLOWED = "allowed";
    
    //Exercise Plan Table Column Names
    private static final String KEY_EXERCISEID = "exerciseID";
    private static final String KEY_BODYPART = "body_part";
    private static final String KEY_EXERCISENAME = "exercise_name";
    private static final String KEY_NUMOFREPS = "num_of_reps";
    private static final String KEY_NUMOFSETS = "num_of_sets";
    private static final String KEY_DAY = "day";
    private static final String KEY_WORKOUTVIDEO = "workout_video";
 
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_USERID + " INTEGER PRIMARY KEY,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_USERNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_VERIFIED + " TEXT,"
                + KEY_PLANID + " INTEGER,"
                + KEY_CURRENTDAY + " INTEGER,"
                + KEY_ALLOWED + " INTEGER DEFAULT 1" 
                + ")";
        
        String CREATE_EXERCISE_TABLE = "CREATE TABLE " + TABLE_EXERCISE + "("
        		+ KEY_EXERCISEID + " INTEGER PRIMARY KEY,"
        		+ KEY_BODYPART + " TEXT,"
        		+ KEY_EXERCISENAME + " TEXT,"
        		+ KEY_NUMOFREPS + " INTEGER,"
        		+ KEY_NUMOFSETS + " INTEGER,"
        		+ KEY_DAY + " INTEGER,"
        		+ KEY_WORKOUTVIDEO + " TEXT"
        		+ ")";
        
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        // Create tables again
        onCreate(db);
    }
 
    /**
     * Storing user details in database
     * */
    public void addUser(String firstname, String lastname, String username, String email, String verified, int planID, int currentday) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, firstname); // Name
        values.put(KEY_LASTNAME, lastname);
        values.put(KEY_USERNAME, username);
        values.put(KEY_EMAIL, email);
        values.put(KEY_VERIFIED, verified);
        values.put(KEY_PLANID, planID);
        values.put(KEY_CURRENTDAY, currentday);
 
        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
     
    
    public void addExercisePlan(String bodyPart, String exerciseName, int numOfReps, int numOfSets, int day, String workoutVideo) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(KEY_BODYPART, bodyPart); // Name
        values.put(KEY_EXERCISENAME, exerciseName);
        values.put(KEY_NUMOFREPS, numOfReps);
        values.put(KEY_NUMOFSETS, numOfSets);
        values.put(KEY_DAY, day);
        values.put(KEY_WORKOUTVIDEO, workoutVideo);
 
        // Inserting Row
        db.insert(TABLE_EXERCISE, null, values);
        db.close();
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
          
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("firstname", cursor.getString(1));
            user.put("lastname", cursor.getString(2));
            user.put("username", cursor.getString(3));
            user.put("email", cursor.getString(4));
            user.put("verified", cursor.getString(5));
            user.put("planID", cursor.getString(6));
            user.put("currentday", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }
    
    public ArrayList<ExercisePlan> getExercisePlanByDay(int day) {
    	ArrayList<ExercisePlan> exerciseList = new ArrayList<ExercisePlan>();
    	String query = "SELECT * FROM " + TABLE_EXERCISE + " WHERE day=" + day;
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	
    	if (cursor.moveToFirst()) {
            do {
            	ExercisePlan ep = new ExercisePlan();
            	ep.setBodyPart(cursor.getString(1));
            	ep.setExerciseName(cursor.getString(2));
            	ep.setNumOfReps(cursor.getInt(3));
            	ep.setNumOfSets(cursor.getInt(4));
            	ep.setDay(cursor.getInt(5));
            	ep.setWorkoutVideo(cursor.getString(6));
            	ep.setSelected(false);
                // Adding contact to list
            	exerciseList.add(ep);
            } while (cursor.moveToNext());
        }
    	
    	return exerciseList;
    }
    
    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
         
        // return row count
        return rowCount;
    }
    
    public void updateCurrentDay(int day) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues cv = new ContentValues();
    	cv.put("currentday", day);
    	db.update(TABLE_LOGIN, cv, "gymuserID='1'", null);
    }
    
    public void updatePlanID(int planID) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues cv = new ContentValues();
    	cv.put("planID", planID);
    	db.update(TABLE_LOGIN, cv, "gymuserID='1'", null);
    }
    
    public void updateAllowed(String username) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues cv = new ContentValues();
    	cv.put("allowed", 0);
    	db.update(TABLE_LOGIN, cv, "username='" + username + "'"  , null);
    }

     
    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.delete(TABLE_EXERCISE, null, null);
        db.close();
    }
}
