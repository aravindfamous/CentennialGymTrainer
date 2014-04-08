package com.gymcoach;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.library.DBHandler;
import com.library.DietPlan;
import com.library.SimpleSideDrawer;
import com.library.UserFunctions;

@SuppressLint("NewApi")
public class DietActivity extends Activity {
	UserFunctions userFunctions;
	DBHandler db;
	SimpleSideDrawer nav;
	
	TextView tvCurrentDay;
	
	MyCustomAdapter dataAdapter = null;

	@SuppressWarnings("deprecation")
	private int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diet);
		
		
		userFunctions = new UserFunctions();
		
		setTitle(userFunctions.getName(getApplicationContext()));
		db = new DBHandler(getApplicationContext());
		tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);
		
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

		//Set The Current Day in The TextView CurrentDay
        Calendar calendar = Calendar.getInstance();
        String day = dayFormat.format(calendar.getTime());
        tvCurrentDay.setText(day);
        
        displayListView(day);
		
		if(apiLevel >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
    		nav = new SimpleSideDrawer(this);
    		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		}
		
		listeners();
	}
	
	private void displayListView(String day) {
		ArrayList<DietPlan> dietList = db.getDietPlanByDay(day);
		
		dataAdapter = new MyCustomAdapter(this, R.layout.listview_layout_diet, dietList);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(dataAdapter);
	}
	
	private class MyCustomAdapter extends ArrayAdapter<DietPlan> {
		  
		private ArrayList<DietPlan> dietList;
		  
		public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<DietPlan> exerciseList) {
			super(context, textViewResourceId, exerciseList);
			this.dietList = new ArrayList<DietPlan>();
			this.dietList.addAll(exerciseList);
		}
		  
		private class ViewHolder {
			TextView dietInfo;
			TextView size;
			TextView timeOfMeal;
		}
		  
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		  
			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));
		  
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.listview_layout_diet, null); 
				
				//new holder
				holder = new ViewHolder();
				holder.dietInfo = (TextView) convertView.findViewById(R.id.info);
				holder.size = (TextView) convertView.findViewById(R.id.size);
				holder.timeOfMeal = (TextView) convertView.findViewById(R.id.time);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			  
			DietPlan ep = dietList.get(position);
			holder.dietInfo.setText(ep.getDietInfo());
			holder.size.setText("Size: " + ep.getSize());
			holder.timeOfMeal.setText(ep.getTimeOfMeal());
			  
			return convertView;
			
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
				nav.toggleLeftDrawer();	
			}
		});
        
        TextView tvReport = (TextView) findViewById(R.id.tvReport);
        tvReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent report = new Intent(getApplicationContext(), ReportActivity.class);
				report.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(report); 
            // Closing dashboard screen
            finish();
			}
		});
    }
}
