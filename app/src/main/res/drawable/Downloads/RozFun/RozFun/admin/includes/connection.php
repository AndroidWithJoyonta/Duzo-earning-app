<?php
    error_reporting(0);
 	ob_start();
    session_start();
 	header("Content-Type: text/html;charset=UTF-8");
	if($_SERVER['HTTP_HOST']=="localhost" or $_SERVER['HTTP_HOST']=="192.168.1.125") {	
		//local  
		DEFINE ('DB_USER', 'root');
		DEFINE ('DB_PASSWORD', '');
		DEFINE ('DB_HOST', 'localhost'); //host name depends on server
		DEFINE ('DB_NAME', 'quiz_app');
	} else {
		//local live 
		DEFINE ('DB_USER', 'dailygwn_userfgh');
		DEFINE ('DB_PASSWORD', 'iuZEH;hcP!3Z');
		DEFINE ('DB_HOST', 'localhost'); //host name depends on server
		DEFINE ('DB_NAME', 'dailygwn_spin');
	}
	$mysqli =mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME);
	if ($mysqli->connect_errno) {
    	echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	mysqli_query($mysqli,"SET NAMES 'utf8'");	 
	//Settings
	$setting_qry="SELECT * FROM tbl_setting where id='1'";
    $setting_result=mysqli_query($mysqli,$setting_qry);
    $settings_details=mysqli_fetch_assoc($setting_result);
    define("PAYTM_ID",$settings_details['paytm_id']);
    define("PAYTM_KEY",$settings_details['paytm_key']);
    define("RAZORPAY_ID",$settings_details['razorpay_id']);
    define("RAZORPAY_KEY",$settings_details['razorpay_key']);
    define("CASHFREE_ID",$settings_details['cashfree_id']);
?> 
	 
 