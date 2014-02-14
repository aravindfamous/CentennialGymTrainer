package com.gymcoach;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.library.DBHandler;
import com.library.UserFunctions;
 
public class RegisterActivity extends Activity {
	private String spinnerArray[];
    Button btnRegister;
    Button btnLinkToLogin;
    Spinner spinner;
    
    EditText etFirstName;
    EditText etLastName;
    EditText etAddress;
    EditText etCity;
    EditText etProvince;
    EditText etPostalCode;
    EditText etDOB;
    EditText etUsername;
    EditText etPassword;
    EditText etCPassword;
    EditText etEmail;
    
    TextView tvRegisterError;
     
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    //private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_FIRSTNAME = "firstname";
    private static String KEY_LASTNAME = "lastname";
    private static String KEY_USERNAME = "username";
    private static String KEY_EMAIL = "email";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        spinnerArray = new String[3];
        spinnerArray[0] = "Student";
        spinnerArray[1] = "Guest";
        spinnerArray[2] = "Resident";
        spinner = (Spinner) findViewById(R.id.spinnerUserType);
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinner.setAdapter(adapter);
        
        // Importing all assets like buttons, text fields
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etCity = (EditText) findViewById(R.id.etCity);
        etProvince = (EditText) findViewById(R.id.etProvince);
        etPostalCode = (EditText) findViewById(R.id.etPostalCode );
        etDOB = (EditText) findViewById(R.id.etDOB);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etCPassword = (EditText) findViewById(R.id.etCPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        tvRegisterError = (TextView) findViewById(R.id.tvRegisterError);
         
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
                String firstname = etFirstName.getText().toString();
                String lastname = etLastName.getText().toString();
                String address = etAddress.getText().toString();
                String city = etCity.getText().toString();
                String province = etProvince.getText().toString();
                String postalcode = etPostalCode.getText().toString();
                String dob = etDOB.getText().toString();
                String usertype = spinner.getSelectedItem().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String cpassword = etCPassword.getText().toString();
                String email = etEmail.getText().toString();

                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.registerUser(firstname, lastname, address, city, province, postalcode, dob, usertype, username, password, email);
                 
                // check for login response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        tvRegisterError.setText("");
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            // user successfully registred
                            // Store user details in SQLite Database
                            DBHandler db = new DBHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("user");
                             
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_FIRSTNAME), json_user.getString(KEY_LASTNAME), json_user.getString(KEY_USERNAME), json_user.getString(KEY_EMAIL));                        
                            // Launch Dashboard Screen
                            Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                            // Close all views before launching Dashboard
                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboard);
                            // Close Registration Screen
                            finish();
                        }else{
                            // Error in registration
                        	tvRegisterError.setText("Error occured in registration");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }
}
