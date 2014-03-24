package com.gymcoach;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.library.DBHandler;
import com.library.UserFunctions;

public class LoginActivity extends Activity {
	Button btnLogin;
    Button btnLinkToRegister;
    EditText etUsername;
    EditText etPassword;
    TextView tvLoginError;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    
    private static String KEY_FIRSTNAME = "firstname";
    private static String KEY_LASTNAME = "lastname";
    private static String KEY_USERNAME = "username";
    private static String KEY_EMAIL = "email";
    private static String KEY_VERIFIED = "verified";
    private static String KEY_PLANID = "planID";
    private static String KEY_CURRENTDAY = "currentday";
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// Importing all assets like buttons, text fields
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        tvLoginError = (TextView) findViewById(R.id.tvLoginError);
        
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.loginUser(username, password);
 
                
                
                // check for login response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                    	tvLoginError.setText("");
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            // user successfully logged in
                            DBHandler db = new DBHandler(getApplicationContext());
                            //getJson object and arrays
                            JSONObject json_user = json.getJSONObject("user");
                            JSONArray json_exercise_array = json.getJSONArray("exercise");
                            JSONArray json_diet_array = json.getJSONArray("diet"); 
                            
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());                    
                            db.addUser(json_user.getString(KEY_FIRSTNAME), json_user.getString(KEY_LASTNAME), 
                            		json_user.getString(KEY_USERNAME), json_user.getString(KEY_EMAIL), 
                            		json_user.getString(KEY_VERIFIED), Integer.parseInt(json_user.getString(KEY_PLANID)), 
                            		Integer.parseInt(json_user.getString(KEY_CURRENTDAY)));
                            
                            userFunction.addPlan(getApplicationContext(), json_exercise_array, json_diet_array);

                            // Launch Dashboard Screen
                            Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                             
                            // Close all views before launching Dashboard
                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboard);
                             
                            // Close Login Screen
                            finish();
                        }else{
                        	int error = Integer.parseInt(json.getString("error"));
                            if(error == 1)
                            	tvLoginError.setText("Incorrect email or password!");
                            else if(error == 2)
                            	tvLoginError.setText("Username is not registered");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
	}

}
