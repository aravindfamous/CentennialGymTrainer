<?php

class EmailVerification {

	function __construct() {}
	function __destruct() {}

	public function sendVerificationEmail($email, $username, $hash, $name) {
		require_once 'phpmailer/class.phpmailer.php';
		$mail = new PHPMailer();

		$to      = $email; // Send email to our user
		$subject = 'Signup | Verification'; // Give the email a subject 
		$message = "
		<br />	
		Thanks for signing up!<br />
		Your account has been created, you can login with the following credentials after you have activated your account by<br />
		pressing the url below.<br />
		<br />
		------------------------<br />
		Username: ".$username."<br />
		Password: password you registered<br />
		------------------------<br />
		<br />
		Please click this link to activate your account:<br />
		<a href='http://www.wyncoding.t15.org/gymcoach/verify.php?email=".$email."&hash=".$hash."'>
		http://www.wyncoding.t15.org/gymcoach/verify.php?email=".$email."&hash=".$hash."</a><br />
		<br />
		"; // Our message above including the link

		/*
		$headers = "Reply-To: Centennial Trainer <gymcoach@wyncoding.t15.org>\r\n";
		$headers .= "Return-Path: Centennial Trainer <gymcoach@wyncoding.t15.org>\r\n";
		$headers .= "From: Centennial Trainer <noreply@wyncoding.t15.org> \r\n";
		$headers .= "Organization: Centennial Trainer\r\n";
		$headers .= "MIME-Version: 1.0\r\n";
		$headers .= "Content-type: text/plain; charset=iso-8859-1\r\n";
		$headers .= "X-Priority: 3\r\n";
		$headers .= "X-Mailer: PHP". phpversion() ."\r\n";

		mail($to, $subject, $message, $headers);
		*/

		$mail->IsSMTP();

		//GMAIL config
		$mail->SMTPAuth   = true;                  // enable SMTP authentication
		$mail->SMTPSecure = "ssl";                 // sets the prefix to the server
		$mail->Host       = "smtp.gmail.com";      // sets GMAIL as the SMTP server
		$mail->Port       = 465;                   // set the SMTP port for the GMAIL server
		$mail->Username   = "centennialtrainer";  // GMAIL username
		$mail->Password   = "gymcoach";            // GMAIL password
		//End Gmail

		$mail->From       = "centennialtrainer@gmail.com";
		$mail->FromName   = "Centennial Trainer";
		$mail->Subject    = $subject;
		$mail->MsgHTML($message);

		$mail->AddReplyTo("romainealdwynherrera@gmail.com","Romaine Herrera");//they answer here, optional
		$mail->AddAddress($to, $name);
		$mail->IsHTML(true); // send as HTML

		$mail->Send();
	}
}

?>