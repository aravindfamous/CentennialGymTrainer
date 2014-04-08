package com.gymcoach;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.library.SimpleSideDrawer;
import com.library.UserFunctions;

public class ReportActivity extends Activity {
	SimpleSideDrawer nav;
	UserFunctions userFunctions;
	Button btnReport;
	
	@SuppressWarnings("deprecation")
	private int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
	private String username;
	private EditText etTopic;
	private EditText etQuestion;
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
            nav.toggleLeftDrawer();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		userFunctions = new UserFunctions();

		if(apiLevel >= 11) {
			setTitle(userFunctions.getName(getApplicationContext()));
			getActionBar().setDisplayHomeAsUpEnabled(true);
    		nav = new SimpleSideDrawer(this);
    		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
    		listeners();
    		
    		username = userFunctions.getUsername(getApplicationContext());
    		etTopic = (EditText) findViewById(R.id.etTopic);
    		etQuestion = (EditText) findViewById(R.id.etQuestion);
    		btnReport = (Button) findViewById(R.id.btnReport);
    		
    		btnReport.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
			
		    		JSONObject json = userFunctions.sendReport(username, etTopic.getText().toString(), etQuestion.getText().toString());
		    		
		    		try {
		    			if (json.getString("success") != null) {
							if(Integer.parseInt(json.getString("success")) == 1)
								Toast.makeText(getApplicationContext(), "Report Sent wait for the answer in your email", Toast.LENGTH_LONG).show();
		    			}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

	}
	
	private void listeners() {
    	//All Set onClick listeners
		Button btnBehindLogout = (Button) findViewById(R.id.btnBehindLogout);
        TextView tvHome = (TextView) findViewById(R.id.tvHome);
        TextView tvExerciseCurrentDay = (TextView) findViewById(R.id.tvExerciseCurrentDay);
        TextView tvDiet = (TextView) findViewById(R.id.tvDiet);

        btnBehindLogout.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View arg0) {
                userFunctions.logoutUser(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login); 
                // Closing dashboard screen
                finish();
            }
        });
        
        
        tvHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent home = new Intent(getApplicationContext(), DashboardActivity.class);
				home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home); 
				finish();
			}
		});
         
        tvExerciseCurrentDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent exercise = new Intent(getApplicationContext(), ExerciseActivity.class);
				exercise.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(exercise); 
				// Closing dashboard screen
				finish();
			}
		});
        
        tvDiet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent diet = new Intent(getApplicationContext(), DietActivity.class);
				diet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(diet); 
				// Closing dashboard screen
				finish();
			}
		});
        
        TextView tvReport = (TextView) findViewById(R.id.tvReport);
        tvReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nav.toggleLeftDrawer();	
			}
		});
    }

}
