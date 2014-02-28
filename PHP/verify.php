<?php

require_once 'DB_Connect.php';
$db = new DB_Connect();
$db->connect();

if(isset($_GET['email']) && !empty($_GET['email']) 
	AND isset($_GET['hash']) && !empty($_GET['hash'])){

    $email = mysql_escape_string($_GET['email']); // Set email variable
    $hash = mysql_escape_string($_GET['hash']);

    $search = mysql_query("SELECT email, hash FROM user WHERE email='".$email."' AND hash='".$hash."'") or die(mysql_error()); 
	$match  = mysql_num_rows($search);

	$result = mysql_query("SELECT * FROM user WHERE email='$email' AND verified='1'");
	$resultmatch = mysql_num_rows($result);

	if($resultmatch == 0) {
		if($match > 0) {
			mysql_query("UPDATE user SET verified='1' WHERE email='$email' AND verified='0'") or die(mysql_error());
			echo 'You have verified your account!! :) go to the app and login again';
		}
	} else {
		echo 'You have already verified your account!! :) go to the app and login again';
	}
}else{
    // Invalid approach
}

?>