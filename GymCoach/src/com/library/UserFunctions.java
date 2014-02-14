package com.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
 
public class UserFunctions {
     
    private JSONParser jsonParser;
     
    // Testing in localhost using wamp or xampp 
    private static String loginURL = "http://wyncoding.t15.org/gymcoach/index.php";
    private static String registerURL = "http://wyncoding.t15.org/gymcoach/index.php";
     
    private static String login_tag = "login";
    private static String register_tag = "register";
     
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String username, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
     
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
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

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
				
		// return json
        return json;
    }
     
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DBHandler db = new DBHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DBHandler db = new DBHandler(context);
        db.resetTables();
        return true;
    }
     
}
