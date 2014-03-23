<?php
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];
 
    // include db handler
    require_once 'DB_Functions.php';
    $db = new DB_Functions();
    require_once 'EmailVerification.php';
    $ev = new EmailVerification();
 
    // response Array
    $response = array("tag" => $tag, "success" => 0);
 
    // check for tag type
    if ($tag == 'login') {
        // Request type is check Login
        $username = $_POST['username'];
        $password = $_POST['password'];
 
        // check for user
        $user = $db->getUserByUsernameAndPassword($username, $password);
        $planID = $user[1]['planID'];
        
        $exerciseplan = $db->getExercisePlan($planID);
        $dietplan = $db->getDietPlan($planID);

        if ($user != false) {
            // echo json with success = 1
            $response["success"] = 1;
            $response["user"]["firstname"] = $user[0]["firstname"];
            $response["user"]["lastname"] = $user[0]["lastname"];
            $response["user"]["username"] = $user[1]["username"];
            $response["user"]["email"] = $user[0]["email"];
            $response["user"]["verified"] = $user[0]["verified"];
            $response["user"]["currentday"] = $user[1]["currentday"];
            $response["user"]["planID"] = $user[1]["planID"];
            $response["exercise"] = $exerciseplan;
            $response["diet"] = $dietplan;

            $json = json_encode($response);
            $json = str_replace('"\u0001"', '"1"', $json);
            $json = str_replace('"\u0000"', '"0"', $json);
            echo $json;
        } else if (!$db->isUsernameExisted($username)) {
            $response["error"] = 2;
            //$response["error_msg"] = "Username is not registered";
            echo json_encode($response);
        } else {
            $response["error"] = 1;
            //$response["error_msg"] = "Incorrect email or password!";
            echo json_encode($response);
        }
    } else if ($tag == 'register') {
        // Request type is Register new user
        $firstname = $_POST['firstname'];
        $lastname = $_POST['lastname'];
        $address = $_POST['address'];
        $city = $_POST['city'];
        $province = $_POST['province'];
        $postalcode = $_POST['postalcode'];
        $dob = $_POST['dob'];
        $usertype= $_POST['usertype'];
        $username = $_POST['username']; 
        $password = $_POST['password'];
        $email = $_POST['email'];

        
        if($db->isUsernameExisted($username)) {
            $response["error_msg"] = "Username already existed";
            echo json_encode($response);
        } else if($db->isEmailExisted($email)) {
            $response["error_msg"] = "Email already existed";
            echo json_encode($response);
        } else {
            $response["success"] = 1;
            $user = $db->storeUser($firstname, $lastname, $address, $city, $province, $postalcode, $dob, $usertype, $username, $password, $email);
            $hash = $db->putHash($user[0]["email"]);
            $name = $user[0]["firstname"] + " " + $user[0]["lastname"];
            $ev->sendVerificationEmail($user[0]["email"], $username, $hash, $name);
            echo json_encode($response);
        }

    } else if($tag == 'generate') {
        $username = $_POST['username'];
        $height = $_POST['height'];
        $weight = $_POST['weight'];
        $age = $_POST['age'];
        $workoutType = $_POST['workouttype'];
        $gender = $_POST['gender'];
        $bmi = $_POST['bmi'];
        $bmr = $_POST['bmr'];

        $planID = '1'; // should have a method
        $user = $db->storePersonalInfo($username, $height, $weight, $age, $gender, $bmi, $bmr, $planID);
        $plan = $db->getExercisePlan($planID);
        $response["planID"] = $planID;
        $response["exercise"] = $plan;

        echo json_encode($response);
    } else if($tag == 'verification') {

        $email = $_POST['email'];
        $number = $_POST['number'];

        if($number == '1') {
            //send verification
            $user = $db->getUserByEmail($email);
            $username = $user[1]['username'];
            $hash = $user[0]['hash'];
            $name = $user[0]['firstname'] + " " + $user[0]['lastname'];
            $ev->sendVerificationEmail($email, $username, $hash, $name);

        } else if($number == '2') {
            //send verification with chamged email
            $email2 = $_POST['email2'];
            $user = $db->changeEmail($email, $email2);
            $email3 = $user[0]['email'];
            $username = $user[1]['username'];
            $hash = $user[0]['hash'];
            $name = $user[0]['firstname'] + " " + $user[0]['lastname'];
            $ev->sendVerificationEmail($email3, $username, $hash, $name);
        }

    } else if($tag == 'update') {

        $type = $_POST['type'];
        if($type == "currentday") {
            $username = $_POST['username'];
            $currentday = $_POST['currentday'];

            $db->updateCurrentDay($username, $currenday);
        }

    } 

    else {
        echo "Invalid Request";
    }
} else {
    echo "Access Denied";
}
?>