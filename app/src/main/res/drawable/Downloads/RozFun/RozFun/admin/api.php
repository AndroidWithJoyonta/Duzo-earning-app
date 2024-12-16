<?php 
	include("includes/connection.php");
  	if($_POST['method']=="login") {
 		$user = $_POST['user'];
 		$pass = $_POST['pass'];
	    $qry = "SELECT * FROM tbl_setting WHERE user = '$user' and pass = '$pass'";
		$result = mysqli_query($mysqli,$qry);
		$num_rows = mysqli_num_rows($result);
 		$row = mysqli_fetch_assoc($result);
    	if ($num_rows > 0) {
    	    $set[] = array('msg'=>'found','success'=>'1','data'=>$row);
    	    header( 'Content-Type: application/json; charset=utf-8' );
    	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
    	    die();
    	} else {
    	    $set[]= array('msg'=>'invalid details','success'=>'0');
    	    header( 'Content-Type: application/json; charset=utf-8' );
    	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
    	    die();
    	}
 	} else if($_POST['method']=="get_setting") {
 		$jsonObj= array();
		$query="SELECT * FROM tbl_setting WHERE id = '1'";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());
		$data = mysqli_fetch_assoc($sql);
		$set[] = $data;
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	} else if($_POST['method']=="set_setting") {
 	    $po = $_POST['ap'];
 	    $re = $_POST['ar'];
 	    $de = $_POST['ad'];
 	    $ri = $_POST['arid'];
 	    $rk = $_POST['arkey'];
 	    $pi = $_POST['apid'];
 	    $pk = $_POST['apkey'];
 	    $ci = $_POST['acid'];
 	    $ch = $_POST['ac'];
 	    $un = $_POST['aun'];
 	    $ps = $_POST['aps'];
		$user_edit= "UPDATE tbl_setting SET points = '$po', redeem = '$re', deposit = '$de', razorpay_id  = '$ri', razorpay_key  = '$rk', paytm_id  = '$pi', paytm_key  = '$pk',
		cashfree_id  = '$ci', charge  = '$ch', user  = '$un', pass  = '$ps' WHERE id = '1'";	 
   		$user_res = mysqli_query($mysqli,$user_edit);
   		$query="SELECT * FROM tbl_setting WHERE id = '1'";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());
		$data = mysqli_fetch_assoc($sql);
		$set[]=array('msg'=>'Updated','success'=>'1','data'=>$data);
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
	} else if($_POST['method']=="userlist") {
		$query="SELECT * FROM tbl_users ORDER BY id DESC";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());
		while($data = mysqli_fetch_assoc($sql)) {
			$set[] = $data;
		}
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	} else if($_POST['method']=="txnlist") {
		$query="SELECT * FROM tbl_transaction ORDER BY id DESC";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());
		while($data = mysqli_fetch_assoc($sql)) {
			$set[] = $data;
		}
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	} else if($_POST['method']=="updatetxn") {
 	    $id = $_POST['id'];
 	    $st = $_POST['status'];
		$user_edit= "UPDATE tbl_transaction SET status = '$st' WHERE id = '$id'"; 
   		$user_res = mysqli_query($mysqli,$user_edit);	
		$set[]=array('msg'=>'Updated!','success'=>'1');
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
	} else if($_POST['method']=="updateusr") {
 	    $id = $_POST['id'];
 	    $depo = $_POST['depo'];
 	    $with = $_POST['with'];
 	    $st = $_POST['status'];
		$user_edit= "UPDATE tbl_users SET deposit = '$depo', withdraw = '$with', status = '$st' WHERE id = '$id'"; 
   		$user_res = mysqli_query($mysqli,$user_edit);	
		$set[]=array('msg'=>'Updated!','success'=>'1');
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
	} else if($_POST['method']=="register") {
	    $ra = 5;
	    $bonus = "10";
	    $code = $_POST['code'];
	    $name = $_POST['name'];
	    $device = $_POST['device'];
	    $rfr = substr($device, 0, 8);
	    if($code != '') {
	        $qry = "SELECT * FROM tbl_users WHERE refercode = '$code'";
	        $result = mysqli_query($mysqli,$qry);
	        $row = mysqli_fetch_assoc($result);
	        if(mysqli_num_rows($result) > 0) {
	            $cd = $row['deposit'];
	            $ncd = $cd + $ra;
	            $user_edit = "UPDATE tbl_users SET deposit='$ncd' WHERE refercode = '$code'";
	            $user_res = mysqli_query($mysqli,$user_edit);
	            $dv = $row['device'];
	            $ori = 'odr_'.rand(99,999999);
	            $txi = 'txn_'.rand(99,999999);
	            $txn_up = "INSERT INTO `tbl_transaction` (`id`, `type`, `device`, `amount`, `detail`, `orderid`, `txnid`, `status`) VALUES (NULL, '0', '$dv', '$ra', 'Refer Bonus', '$ori', '$txi', '1')";
	            $txn_res = mysqli_query($mysqli,$txn_up);
	            $rnm = $row['name'];
	            $user_in = "INSERT INTO `tbl_users` (`id`, `name`, `email`, `deposit`, `withdraw`, `referby`, `refercode`, `status`) VALUES (NULL, '$name', '$device', '$bonus', '0', '$rnm', '$rfr', '1')";
	            $in_qry = mysqli_query($mysqli,$user_in);
	            $set[]=array('msg' => "Register successfully...!",'success'=>'1');
	            header( 'Content-Type: application/json; charset=utf-8' );
	            echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
	            die();
	        } else {
	            $set[]=array('msg' => "Invalid refer code!",'success'=>'0');
	            header( 'Content-Type: application/json; charset=utf-8' );
	            echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
	            die();
	        }
	    } else {
	        $user_in = "INSERT INTO `tbl_users` (`id`, `name`, `email`, `deposit`, `withdraw`, `referby`, `refercode`, `status`) VALUES (NULL, '$name', '$device', '$bonus', '0', '', '$rfr', '1')";
	        $in_qry = mysqli_query($mysqli,$user_in);
	        $set[] = array('msg' => "Register successfully...!",'success'=>'1');
	        header( 'Content-Type: application/json; charset=utf-8' );
	        echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
	        die();
	    }
	} else if($_POST['method']=="user")	{
	    $device = $_POST['device'];
 		$query="SELECT * FROM tbl_users WHERE email = '$device'";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());
		if(mysqli_num_rows($sql) > 0) {
		    $row = mysqli_fetch_assoc($sql);
		    $set[]=array('msg' => "found!",'success'=>'1','user'=>$row);
		    header( 'Content-Type: application/json; charset=utf-8' );
		    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		    die();
		} else {
		    $set[]=array('msg' => "Invalid user!",'success'=>'0');
		    header( 'Content-Type: application/json; charset=utf-8' );
		    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		    die();
		}
 	} else if($_POST['method']=="addtxn") {
 	    $amt = $_POST['amo'];
 	    $typ = $_POST['typ'];
 	    $dv = $_POST['dev'];
 	    $des = $_POST['des'];
 	    $odr = $_POST['ord'];
 	    $txn = $_POST['tra'];
 	    $sts = $_POST['sta'];
 	    $txn_up = "INSERT INTO `tbl_transaction` (`id`, `type`, `device`, `amount`, `detail`, `orderid`, `txnid`, `status`) VALUES (NULL, '$typ', '$dv', '$amt', '$des', '$odr', '$txn', '$sts')";
 	    $txn_res = mysqli_query($mysqli,$txn_up);
 	    $set[]=array('msg' => "Successful!",'success'=>'1');
 	    header( 'Content-Type: application/json; charset=utf-8' );
 	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
 	    die();
	} else if($_POST['method']=="mytxn") {
	    $dec = $_POST['dev'];
 		$query="SELECT * FROM tbl_transaction WHERE device = '$dec' ORDER BY id DESC";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());
		while($data = mysqli_fetch_assoc($sql)) {
			$set[] = $data;
		}
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
	} else {
	    $set[]=array('msg' => "No such method found!",'success'=>'0');
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
	}
?>