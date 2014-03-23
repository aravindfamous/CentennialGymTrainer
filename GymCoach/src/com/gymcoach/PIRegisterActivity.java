package com.gymcoach;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.library.DBHandler;
import com.library.UserFunctions;

public class PIRegisterActivity extends Activity {
	private String spinnerArray[];
	UserFunctions userFunctions;
	private Button btnCalculate;
	private Button btnSendInfo;
	
	private EditText etAge;
	private EditText etHeightFeet;
	private EditText etHeightInches;
	private EditText etWeight;
	Spinner spinner;
	private RadioGroup radioGenderGroup;
	private RadioButton radioGenderButton;

	private TextView tvDisplayBMI;
	private TextView tvDisplayBMR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalinforegister);
		
		spinnerArray = new String[5];
	    spinnerArray[0] = "Weight Gain";
	    spinnerArray[1] = "Muscle Gain";
	    spinnerArray[2] = "Fat Loss";
	    spinnerArray[3] = "Weight Loss";
	    spinnerArray[4] = "I dont know";
	    
	    spinner = (Spinner) findViewById(R.id.spinnerWorkoutType);
	    
	    @SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinner.setAdapter(adapter);
		
		userFunctions = new UserFunctions();
		etAge = (EditText) findViewById(R.id.etAge);
		etHeightFeet = (EditText) findViewById(R.id.etHeightFeet);
		etHeightInches = (EditText) findViewById(R.id.etHeightInches);
		etWeight = (EditText) findViewById(R.id.etWeight);
		radioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
		tvDisplayBMI = (TextView) findViewById(R.id.tvDisplayBMI);
		tvDisplayBMR = (TextView) findViewById(R.id.tvDisplayBMR);
		
		btnCalculate = (Button) findViewById(R.id.btnCalculateBMIandBMR);
		btnCalculate.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {			
				int age = Integer.parseInt(etAge.getText().toString());
				int heightFeet = Integer.parseInt(etHeightFeet.getText().toString());
				int heightInches = Integer.parseInt(etHeightInches.getText().toString());
				double weight = Double.parseDouble(etWeight.getText().toString());
				int height = (heightFeet * 12) + heightInches;
				
				double valueBMI = (weight / (height * height)) * 703;
				double bmi = (double) Math.round(valueBMI * 10) / 10;
				double bmr = 0;
				
				int selectedId = radioGenderGroup.getCheckedRadioButtonId();
				radioGenderButton = (RadioButton) findViewById(selectedId);
				
				if(radioGenderButton.getText().equals("Male")) {
					//BMR = 66 + ( 6.23 x weight in pounds ) + ( 12.7 x height in inches ) - ( 6.8 x age in year )
					double valueBMR = 66 + (6.23 * weight) + (12.7 * height) - (6.8 * age);
					bmr = (double) Math.round(valueBMR * 10) / 10;		
				} else if(radioGenderButton.getText().equals("Female")) {
					//BMR = 655 + ( 4.35 x weight in pounds ) + ( 4.7 x height in inches ) - ( 4.7 x age in years )
					double valueBMR = 655 + (4.35 * weight) + (4.7 * height) - (4.7 * age);
					bmr = (double) Math.round(valueBMR * 10) / 10;	
				}
				
				tvDisplayBMI.setText("" + bmi);
				tvDisplayBMR.setText("" + bmr);
			}
		});
		
		btnSendInfo = (Button) findViewById(R.id.btnSendInfo);
		btnSendInfo.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {	
				int age = Integer.parseInt(etAge.getText().toString());
				int heightFeet = Integer.parseInt(etHeightFeet.getText().toString());
				int heightInches = Integer.parseInt(etHeightInches.getText().toString());
				double weight = Double.parseDouble(etWeight.getText().toString());
				int height = (heightFeet * 12) + heightInches;
				String workoutType = spinner.getSelectedItem().toString();
				double bmi = Double.parseDouble(tvDisplayBMI.getText().toString());
				double bmr = Double.parseDouble(tvDisplayBMR.getText().toString());
				
				int selectedId = radioGenderGroup.getCheckedRadioButtonId();
				radioGenderButton = (RadioButton) findViewById(selectedId);
				String gender = "";
				if(radioGenderButton.getText().equals("Male")) {	
					gender = "Male";
				} else if(radioGenderButton.getText().equals("Female")) {
					gender = "Female";
				}
				
				JSONObject json = userFunctions.generateAPlan(getApplicationContext(), height, weight, age, workoutType, gender, bmi, bmr);
				
				try {
					JSONArray json_exercise_array = json.getJSONArray("exercise");
					JSONArray json_diet_array = json.getJSONArray("diet"); 
					DBHandler db = new DBHandler(getApplicationContext());
					db.updatePlanID(Integer.parseInt(json.getString("planID")));
					userFunctions.addPlan(getApplicationContext(), json_exercise_array, json_diet_array);
					Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
				} catch (JSONException e) {
                    e.printStackTrace();
                }
			}
		});
		//Make a Toast for Your account has been made please verify it by clicking the activation link that has been send to your email.
	}
}
