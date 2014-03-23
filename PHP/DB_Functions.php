<?php
 
class DB_Functions {
 
    private $db;
 
    //put your code here
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($firstname, $lastname, $address, $city, $province, $postalcode, $dob, $usertype,
     $username, $password, $email) {
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
        $dobt = date('Y-m-d', strtotime($dob));
        //insert into the user table
        //$this->mysql_fix_aigap('user', 'userID');
        $result = mysql_query("INSERT INTO user(firstname, lastname, address, city, province, postalcode, email, DOB) 
            VALUES('$firstname', '$lastname', '$address', '$city', '$province', '$postalcode', '$email', '$dobt')");
        //get that user again
        $temp = mysql_query("SELECT * FROM user WHERE email = '$email'");   
        $user = mysql_fetch_array($temp);  
        //to get the id of the newly created user
        $id = $user['userID'];
        //insert into gymuser with newly create user id
        //$this->mysql_fix_aigap('gymuser', 'gymuserID');
        $result2 = mysql_query("INSERT INTO gymuser(userID, user_type, username, encrypted_password, salt) 
            VALUES($id, '$usertype', '$username', '$encrypted_password', '$salt')");     
        // check for successful store
        if ($result && $result2) {
            // get gymuser details 
            $temp2 = mysql_query("SELECT * FROM gymuser WHERE userID = $id");
            $array = mysql_fetch_array($temp2);
            // return user details
            return array($user, $array);
        } else {
            return false;
        }
    }

    public function storePersonalInfo($username, $height, $weight, $age, $gender, $bmi, $bmr, $planID) {
        $result = mysql_query("UPDATE gymuser SET height='$height', weight='$weight', age='$age',
        gender='$gender', BMI='$bmi', BMR='$bmr', planID='$planID' WHERE username='$username'");
        if($result) {
            $array = mysql_fetch_array(mysql_query("SELECT * FROM gymuser WHERE username='$username'"));
            $userID = $array['userID'];
            $user = mysql_fetch_array(mysql_query("SELECT * FROM user WHERE userID='$userID'"));
            return array($user, $array);
        } else {
            return false;
        }
    }

    /*
     * Updates
     */

    public function updateCurrentDay($username, $currentday) {
        mysql_query("UPDATE gymuser SET currentday='$currentday' WHERE username='$username'");
    }
 
    /**
     * Get user by email and password
     */
    public function getUserByUsernameAndPassword($username, $password) {
        $result = mysql_query("SELECT * FROM gymuser WHERE username = '$username'") or die(mysql_error());
        // check for result 
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // gymuser information
            $result = mysql_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $id = $result['userID'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user information
                $result2 = mysql_fetch_array(mysql_query("SELECT * FROM user WHERE userID = $id"));
                return array($result2, $result);
            }
        } else {
            // user not found
            return false;
        }
    }

    public function getUserByUsername($username) {
        $result = mysql_query("SELECT * FROM gymuser WHERE username = '$username'") or die(mysql_error());
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            $id = $result['userID'];
            $result2 = mysql_fetch_array(mysql_query("SELECT * FROM user WHERE userID = $id"));
            return array($result2, $result);
        } else {
            return false;
        }
    }

    public function getUserByEmail($email) {
        $result = mysql_query("SELECT * FROM user WHERE email = '$email'") or die(mysql_error());
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            $id = $result['userID'];
            $result2 = mysql_fetch_array(mysql_query("SELECT * FROM gymuser WHERE userID = $id"));
            return array($result, $result2);
        } else {
            return false;
        }
    }

    public function getExercisePlan($planID) {
        $query = "SELECT * FROM exercise WHERE planID='$planID'";
        $result = mysql_query($query);
        $rows = array();
        while($r = mysql_fetch_assoc($result)) {
            //$rows[] = array('plan' => $r);
            $rows[] = $r;
        }
        return $rows;
    }

    public function getDietPlan($planID) {
        $query = "SELECT * FROM diet WHERE planID='$planID'";
        $result = mysql_query($query);
        $rows = array();
        while($r = mysql_fetch_assoc($result)) {
            $rows[] = $r;
        }
        return $rows;
    }

    public function putHash($email) {
        //UPDATE names SET title = 'New Name' WHERE record_id = '12';
        $hash = md5(rand(0,1000));
        $result = mysql_query("UPDATE user SET hash='$hash' WHERE email='$email'");
        return $hash;
    }

    public function changeEmail($email, $email2) {
        $update = mysql_query("UPDATE user SET email='$email2' WHERE email='$email'");
        $result = mysql_query("SELECT * FROM user WHERE email = '$email2'") or die(mysql_error());
        $result = mysql_fetch_array($result);
        $id = $result['userID'];
        if($update) {
            $result2 = mysql_fetch_array(mysql_query("SELECT * FROM gymuser WHERE userID = $id"));
            return array($result, $result2);
        } else {
            return false;
        }
    }

    public function isEmailExisted($email) {
        $result = mysql_query("SELECT email from user WHERE email='$email'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }
    public function isUsernameExisted($username) {
        $result = mysql_query("SELECT username from gymuser WHERE username='$username'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
        return $hash;
    }

    private function mysql_fix_aigap($table, $column) {
        $fix_aigap = 1;
        $query_results = mysql_query("SELECT * from $table");

        while($row = mysql_fetch_array($query_results)){
            mysql_query("UPDATE $table set `$column`='$fix_aigap' where `$column` like {$row[$column]};");
            $fix_aigap=$fix_aigap+1;
        }
        mysql_query("ALTER TABLE `$table` AUTO_INCREMENT=$fix_aigap");
    }
 
}
 
?>