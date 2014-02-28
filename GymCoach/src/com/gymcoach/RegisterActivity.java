package com.gymcoach;

import java.util.regex.Pattern;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        );
     
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR_MSG = "error_msg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        spinnerArray = new String[3];
        spinnerArray[0] = "Student";
        spinnerArray[1] = "Guest";
        spinnerArray[2] = "Faculty";
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
                
                if(firstname.isEmpty() || lastname.isEmpty() || address.isEmpty() || city.isEmpty()
                		|| province.isEmpty() || postalcode.isEmpty() || dob.isEmpty() || username.isEmpty()
                		|| password.isEmpty() || cpassword.isEmpty() || email.isEmpty()) {
                	
                	String text = "Fill up the Empty Field";
                	tvRegisterError.setText(text);
                	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                	
                } else {
                	
                	if(!password.matches(cpassword)) {
                		
	                	String text = "Password and Confirm Password does not match";
	                	tvRegisterError.setText(text);
	                	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	                	
                	} else if(!checkEmail(email)){
                		
                		String text = "Email is not valid";
	                	tvRegisterError.setText(text);
	                	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	                	
                	} else {

		                UserFunctions userFunction = new UserFunctions();
		                JSONObject json = userFunction.registerUser(firstname, lastname, address, city, province, postalcode, dob, usertype, username, password, email);
		                
						try {
							if(json.getString(KEY_SUCCESS) != null) {
								int success = Integer.parseInt(json.getString(KEY_SUCCESS));
								if(success == 1) {
									tvRegisterError.setText(""); 
									//reset tables
					                userFunction.logoutUser(getApplicationContext());
					                
					                //go to verify screen for 5 secs then logout
									Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					                startActivity(intent);
					                finish();
					                
								} else {
									tvRegisterError.setText(json.getString(KEY_ERROR_MSG)); 
								}
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
						} 
						
                	}
                }
            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
                
            }
        });
    }
    
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}
