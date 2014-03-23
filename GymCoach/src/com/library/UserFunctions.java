package com.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
 
public class UserFunctions {
    private JSONParser jsonParser;
     
    private static String URL = "http://wyncoding.t15.org/gymcoach/index.php";
     
    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String generate_tag = "generate";
    private static String verification_tag = "verification";
    private static String update_tag = "update";
    
    //JSON Response node names for exercise
    private static String KEY_BODYPART = "body_part";
    private static String KEY_EXERCISENAME = "exercise_name";
    private static String KEY_NUMOFREPS = "num_of_reps";
    private static String KEY_NUMOFSETS = "num_of_sets";
    private static String KEY_DAY = "day";
    private static String KEY_WORKOUTVIDEO = "workout_video";
     
    //JSON Response node names for diet
    private static final String KEY_DIETINFO = "diet_info";
    private static final String KEY_SIZE = "size";
    private static final String KEY_DAYOFWEEK = "day_of_week";
    private static final String KEY_TIMEOFMEAL = "time_of_meal";
    
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    public JSONObject loginUser(String username, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    public JSONObject registerUser(String firstname, String lastname, String address,
    		String city, String province, String postalcode, String dob, String usertype, String username, String password, String email){
        // Building Parameters
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("firstname", firstname));
        params.add(new BasicNameValuePair("lastname", lastname));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("city", city));
        params.add(new BasicNameValuePair("province", province));
        params.add(new BasicNameValuePair("postalcode", postalcode));
        params.add(new BasicNameValuePair("dob", dob));
        params.add(new BasicNameValuePair("usertype", usertype));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("email", email));

        return jsonParser.getJSONFromUrl(URL, params);
    }
    
    public JSONObject generateAPlan(Context context, int height, double weight, int age, String workoutType, String gender, double bmi, double bmr) {
    	final List<NameValuePair> params = new ArrayList<NameValuePair>();	
    	String username = getUsername(context);
    	
    	params.add(new BasicNameValuePair("tag", generate_tag));
    	params.add(new BasicNameValuePair("username", username));
    	params.add(new BasicNameValuePair("height", "" + height));
    	params.add(new BasicNameValuePair("weight", "" + weight));
    	params.add(new BasicNameValuePair("age", "" + age));
    	params.add(new BasicNameValuePair("workouttype", workoutType));
    	params.add(new BasicNameValuePair("gender", gender));
    	params.add(new BasicNameValuePair("bmi", "" + bmi));
    	params.add(new BasicNameValuePair("bmr", "" + bmr));
    	
    	return jsonParser.getJSONFromUrl(URL, params);
    }
    
    public void verification(String email, int num) {
    	final List<NameValuePair> params = new ArrayList<NameValuePair>();	

    	params.add(new BasicNameValuePair("tag", verification_tag));
    	params.add(new BasicNameValuePair("email", email));
    	params.add(new BasicNameValuePair("number", "" + num));
    	
    	jsonParser.getJSONFromUrl(URL, params);
    }
    
    public void verification(String email, int num, String email2) {
    	final List<NameValuePair> params = new ArrayList<NameValuePair>();	

    	params.add(new BasicNameValuePair("tag", verification_tag));
    	params.add(new BasicNameValuePair("email", email));
    	params.add(new BasicNameValuePair("number", "" + num));
    	params.add(new BasicNameValuePair("email2", email2));
    	
    	jsonParser.getJSONFromUrl(URL, params);
    }
    
    public void addPlan(Context context, JSONArray exercisearray, JSONArray dietarray) {
    	DBHandler db = new DBHandler(context);
    	for (int i=0; i < exercisearray.length(); i++)
        {
            try {
                JSONObject json_exercise = exercisearray.getJSONObject(i);
                int numOfReps = Integer.parseInt(json_exercise.getString(KEY_NUMOFREPS));
                int numOfSets = Integer.parseInt(json_exercise.getString(KEY_NUMOFSETS));
                int day = Integer.parseInt(json_exercise.getString(KEY_DAY));
                db.addExercisePlan(json_exercise.getString(KEY_BODYPART), json_exercise.getString(KEY_EXERCISENAME), 
                		numOfReps, numOfSets, day, json_exercise.getString(KEY_WORKOUTVIDEO));
            } catch (JSONException e) {
                // Oops
            }
        }
    	
    	for(int i = 0; i < dietarray.length(); i++) {
    		try {
				JSONObject json_diet = dietarray.getJSONObject(i);
				db.addDietPlan(json_diet.getString(KEY_DIETINFO), json_diet.getString(KEY_SIZE), 
						json_diet.getString(KEY_DAYOFWEEK), json_diet.getString(KEY_TIMEOFMEAL));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
     
    /*
     * DATABASE FUNCTIONS!!!!
     */

    public boolean isUserLoggedIn(Context context){
        DBHandler db = new DBHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    public boolean logoutUser(Context context){
        DBHandler db = new DBHandler(context);
        db.resetTables();
        return true;
    }
    
    public boolean hasPlan(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	Boolean value = user.get("planID").equals("0");
    	if(!value) {
    		return true;
    	}
    	return false;
    }
    
    public boolean isVerified(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	Boolean value = user.get("verified").equals("1");
    	if(value) {
    		return true;
    	}
    	return false;
    }
    
    public String getName(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	return user.get("firstname") + " " + user.get("lastname");
    }
    
    public String getUsername(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	return user.get("username");
    }    
    
    public int getCurrentDay(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	return Integer.parseInt(user.get("currentday"));
    }
    
    public String getEmail(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	return user.get("email");
    }
    
    public int getAllowed(Context context) {
    	DBHandler db = new DBHandler(context);
    	HashMap<String,String> user = db.getUserDetails();
    	return Integer.parseInt(user.get("allowed"));
    }
    
    public ArrayList<ExercisePlan> getExercisePlanByDay(Context context, int day) {
    	DBHandler db = new DBHandler(context);
    	return db.getExercisePlanByDay(day);
    }
    
    public void updateCurrentDay(Context context, int day) {
    	//Database Update
    	DBHandler db = new DBHandler(context);
    	db.updateCurrentDay(day);
    	
    	//Online Database Update
    	final List<NameValuePair> params = new ArrayList<NameValuePair>();	
    	String username = getUsername(context);
    	
    	params.add(new BasicNameValuePair("tag", update_tag));
    	params.add(new BasicNameValuePair("type", "currentday"));
    	params.add(new BasicNameValuePair("username", username));
    	params.add(new BasicNameValuePair("currentday", "" + day));
    	
    	jsonParser.getJSONFromUrl(URL, params);
    }
    
    
}
