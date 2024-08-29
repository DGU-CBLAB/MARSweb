<?php
	if(isset($_POST['send'])){
		$to = '2eding@dongguk.edu';
		$subject = 'Feedback from my site';
	}
	$message = 'Name: ' . $_POST['name'] . "\r\n\r\n";
	$message = 'Email: ' . $_POST['email'] . "\r\n\r\n";
	$message = 'Comments: ' . $_POST['comments'];
	
	$success = mail($to, $subject, $message);
?>