package com.gymcoach;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.library.DBHandler;
import com.library.ExercisePlan;
import com.library.SimpleSideDrawer;
import com.library.UserFunctions;

@SuppressLint("NewApi")
public class ExerciseActivity extends Activity {

	UserFunctions userFunctions;
	DBHandler db;
	
	TextView tvCurrentDay;
	private int currentDay;
	
	Button btnBehindLogout;
	Button btnDone;
	TextView tvHome;
    TextView tvExerciseCurrentDay;
	
	MyCustomAdapter dataAdapter = null;
	@SuppressWarnings("deprecation")
	private int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
	SimpleSideDrawer nav;
	private int count;
	
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
		setContentView(R.layout.activity_exercise);

		userFunctions = new UserFunctions();
		db = new DBHandler(getApplicationContext());
		
		setTitle(userFunctions.getName(getApplicationContext()));
		currentDay = userFunctions.getCurrentDay(getApplicationContext());
		TextView tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);
		tvCurrentDay.setText("Day " + currentDay);
		btnDone = (Button) findViewById(R.id.btnDone);
		
		displayListView();

		if(apiLevel >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
    		nav = new SimpleSideDrawer(this);
    		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		}
		
		listeners();
	}
	
	private void displayListView() {
		  
		ArrayList<ExercisePlan> exerciseList = db.getExercisePlanByDay(currentDay);
		  
		//create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(this,
		R.layout.listview_layout_exercise, exerciseList);
		ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		
		/* BUTTON DONE */
		btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<ExercisePlan> exerciseList = dataAdapter.exerciseList;
				
				if(count == exerciseList.size()) {
					
					//if(userFunctions.getAllowed(getApplicationContext()) == 0) {
						ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
						
					    if (connec != null && (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||(connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){
					    	userFunctions.updateCurrentDay(getApplicationContext(), currentDay + 1);
					    	new DBHandler(getApplicationContext()).updateAllowed(userFunctions.getUsername(getApplicationContext()), 1);
							Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							finish();
							
					    }else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {                   
					        Toast.makeText(getApplicationContext(), "You must be connected to the internet", Toast.LENGTH_LONG).show();
					    } 
					    
					//} else {
					//	Toast.makeText(getApplicationContext(), "Not allowed today", Toast.LENGTH_SHORT).show();
					//}
					
				} else {
					Toast.makeText(getApplicationContext(), "Not Finished", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		
	}

	private class MyCustomAdapter extends ArrayAdapter<ExercisePlan> {
	  
	private ArrayList<ExercisePlan> exerciseList;
	  
	public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<ExercisePlan> exerciseList) {
		super(context, textViewResourceId, exerciseList);
		this.exerciseList = new ArrayList<ExercisePlan>();
		this.exerciseList.addAll(exerciseList);
	}
	  
	private class ViewHolder {
		TextView exerciseName;
		CheckBox bodyPart;
		TextView numbers;
		TextView viewVideo;
	}
	  
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  
		ViewHolder holder = null;
		Log.v("ConvertView", String.valueOf(position));
	  
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.listview_layout_exercise, null); 
			
			//new holder
			holder = new ViewHolder();
			holder.exerciseName = (TextView) convertView.findViewById(R.id.code);
			holder.bodyPart =(CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.numbers = (TextView) convertView.findViewById(R.id.numbers);
			holder.viewVideo = (TextView) convertView.findViewById(R.id.viewVideo);
			
			convertView.setTag(holder);
			holder.bodyPart.setOnClickListener( new View.OnClickListener() { 
				public void onClick(View v) { 
					CheckBox cb = (CheckBox) v ; 
					ExercisePlan ep = (ExercisePlan) cb.getTag(); 
					ep.setSelected(cb.isChecked());
					
					//to add count and minus when cb is checked or not checked
					if(cb.isChecked())
						count++;
					else
						count--;
					
					//Changes button to non transparent if all checked
					if(count == exerciseList.size())
						btnDone.getBackground().setAlpha(255);
				} 
			}); 
			
			holder.viewVideo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView tv = (TextView) v;
					ExercisePlan ep = (ExercisePlan)  tv.getTag();
					Toast.makeText(getApplicationContext(), "Go to Link of " + ep.getExerciseName(), Toast.LENGTH_SHORT).show();
				}
			});
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		  
		ExercisePlan ep = exerciseList.get(position);
		holder.exerciseName.setText(ep.getExerciseName());
		
		if(ep.getNumOfSets() == 0)
			holder.numbers.setText(ep.getNumOfReps() + " per side");
		else
			holder.numbers.setText(ep.getNumOfSets() + " x " + ep.getNumOfReps());
		
		holder.bodyPart.setText(ep.getBodyPart());
		holder.bodyPart.setChecked(ep.isSelected());
		holder.bodyPart.setTag(ep);
		
		holder.viewVideo.setTag(ep);
		  
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
				nav.toggleLeftDrawer();	
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
				Intent report = new Intent(getApplicationContext(), ReportActivity.class);
				report.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(report); 
            // Closing dashboard screen
            finish();
			}
		});
    }
}
	  



