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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	TextView tvCurrentDay;
	private int currentDay;
	
	Button btnBehindLogout;
	TextView tvHome;
    TextView tvExerciseCurrentDay;
	
	MyCustomAdapter dataAdapter = null;
	@SuppressWarnings("deprecation")
	private int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
	SimpleSideDrawer nav;
	
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
		setTitle(userFunctions.getName(getApplicationContext()));
		currentDay = userFunctions.getCurrentDay(getApplicationContext());
		TextView tvCurrentDay = (TextView) findViewById(R.id.tvCurrentDay);
		tvCurrentDay.setText("Day " + currentDay);
		
		displayListView();
		
		if(apiLevel >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
    		nav = new SimpleSideDrawer(this);
    		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		}
		
		listeners();
	}
	
	private void displayListView() {
		  
		ArrayList<ExercisePlan> exerciseList = userFunctions.getExercisePlanByDay(getApplicationContext(), currentDay);
		  
		//create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(this,
		R.layout.listview_layout, exerciseList);
		ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    // When clicked, show a toast with the TextView text
		    ExercisePlan ep = (ExercisePlan) parent.getItemAtPosition(position);
		    Toast.makeText(getApplicationContext(),
		      "Clicked on Row: " + ep.getExerciseName(),
		      Toast.LENGTH_LONG).show();
		   }
		});
		
		Button btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int count = 0;
				ArrayList<ExercisePlan> exerciseList = dataAdapter.exerciseList;
				for(int i = 0; i < exerciseList.size(); i++) {
					ExercisePlan ep = exerciseList.get(i);
					if(ep.isSelected()) {
						count++;
					}	
				}
				
				if(count == exerciseList.size() && userFunctions.getAllowed(getApplicationContext()) == 0) {
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
	}
	  
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  
		ViewHolder holder = null;
		Log.v("ConvertView", String.valueOf(position));
	  
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.listview_layout, null);
			  
			holder = new ViewHolder();
			holder.exerciseName = (TextView) convertView.findViewById(R.id.code);
			holder.bodyPart = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.numbers = (TextView) convertView.findViewById(R.id.numbers);
			convertView.setTag(holder);
			holder.bodyPart.setOnClickListener( new View.OnClickListener() { 
				public void onClick(View v) { 
					CheckBox cb = (CheckBox) v ; 
					ExercisePlan ep = (ExercisePlan) cb.getTag(); 
					ep.setSelected(cb.isChecked());
				} 
			}); 
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		  
		ExercisePlan ep = exerciseList.get(position);
		holder.exerciseName.setText(ep.getExerciseName());
		holder.numbers.setText(ep.getNumOfSets() + " x " + ep.getNumOfReps());
		holder.bodyPart.setText(ep.getBodyPart());
		holder.bodyPart.setChecked(ep.isSelected());
		holder.bodyPart.setTag(ep);
		  
		return convertView;
		
		}
	}
	
	private void listeners() {
    	//All Set onClick listeners
        btnBehindLogout = (Button) findViewById(R.id.btnBehindLogout);
        tvHome = (TextView) findViewById(R.id.tvHome);
        tvExerciseCurrentDay = (TextView) findViewById(R.id.tvExerciseCurrentDay);

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
    }
}
	  



