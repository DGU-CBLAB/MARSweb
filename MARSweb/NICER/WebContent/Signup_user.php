<?php
	header("Content-Type: text/html;charset=UTF-8");
	$conn = mysqli_connect("127.0.0.1","root","apmsetup","Login");   
	$data_stream = "'".$_GET['Id']."','".$_GET['Password']."','".$_GET['Name']."','".$_GET['Email']."'";
	$query = "insert into user(`Id`, `Password`, `Name`, `Email`) values (".$data_stream.")";
	$result = mysqli_query($conn, $query);
     
	if($result)
		echo "1";
	else
		echo "-1";
     
	mysqli_close($conn);
?>