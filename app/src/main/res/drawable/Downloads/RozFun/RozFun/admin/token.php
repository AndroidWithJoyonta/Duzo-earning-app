<?php 
// Generated by curl-to-PHP: http://incarnate.github.io/curl-to-php/
$amt = $_GET['amt'];
$odr = $_GET['odr'];
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, 'https://api.cashfree.com/api/v2/cftoken/order');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, "{\"orderId\": \"".$odr."\",\"orderAmount\":".$amt.",\"orderCurrency\":\"INR\"}");
$headers = array();
$headers[] = 'Content-Type: application/json';
$headers[] = 'X-Client-Id: 1966116cb693deb2d1fc81051a116691';
$headers[] = 'X-Client-Secret: 51d04d1df793b7f498420899bdac94f0771d87ac';
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
$result = curl_exec($ch);
echo $result;
if (curl_errno($ch)) {
    echo 'Error:' . curl_error($ch);
}
curl_close($ch);
?>