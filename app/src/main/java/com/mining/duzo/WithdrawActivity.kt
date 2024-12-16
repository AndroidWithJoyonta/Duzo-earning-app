package com.mining.duzo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mygallaray.Admob
import com.example.mygallaray.Constant
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.FirebaseAuth
import com.mining.duzo.databinding.ActivityWithdrawBinding
import org.json.JSONException
import org.json.JSONObject
import java.time.temporal.TemporalAmount

class WithdrawActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val currentUser = firebaseAuth.currentUser



    lateinit var upiAmount : String

    lateinit var upiAddesh : String

    lateinit var pypalAmount : String
    lateinit var pypalAddesh : String

    lateinit var usdtAddesh : String
    lateinit var usdtAmount : String


    lateinit var btcAddesh : String
    lateinit var btcAmount : String




    var btcBalance : Double = 0.0
    private var btc_valu : Int = 60000

    private var earns: Double = 0.0

    var  totalWalletBalances : Double = 0.0

    var total_inr : Double = 0.0

    val dollorRate : Int = 80

    lateinit var binding: ActivityWithdrawBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()
        }

        Constant.admob = Admob(this,false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadBanner(binding.adView,false)
        Constant.admob!!.loadNative(binding.frame)

        binding.paypalPay.setOnClickListener {

            binding.payMethodPaypal.visibility = View.VISIBLE
            binding.payMethodUpi.visibility = View.GONE
            binding.payMethodBtc.visibility = View.GONE
            binding.payMethodDollor.visibility = View.GONE
        }

        binding.upiPay.setOnClickListener {

            binding.payMethodPaypal.visibility = View.GONE
            binding.payMethodUpi.visibility = View.VISIBLE
            binding.payMethodBtc.visibility = View.GONE
            binding.payMethodDollor.visibility = View.GONE
        }

        binding.criptoPay.setOnClickListener {

            binding.payMethodPaypal.visibility = View.GONE
            binding.payMethodUpi.visibility = View.GONE
            binding.payMethodBtc.visibility = View.VISIBLE
            binding.payMethodDollor.visibility = View.GONE
        }

        binding.dollorPay.setOnClickListener {

            binding.payMethodPaypal.visibility = View.GONE
            binding.payMethodUpi.visibility = View.GONE
            binding.payMethodBtc.visibility = View.GONE
            binding.payMethodDollor.visibility = View.VISIBLE
        }

        binding.paypalBtn.setOnClickListener {

            pypalAddesh = binding.paypalId.text.toString()
            pypalAmount = binding.paypalAmountId.text.toString()
            if (pypalAmount.isNotEmpty() && pypalAmount.toDoubleOrNull() != null && pypalAmount.toDouble() >= 5 && pypalAddesh.isNotEmpty()  ){

                if ( totalWalletBalances >=5){

                    val pypalamount = pypalAmount.toDouble()

                    val dollor = pypalamount

                    currentUser?.email?.let { it1 -> withdrawalUsdt(dollor, it1) }
                    paymentRequest("paypal")
                }else
                    Toast.makeText(this,"You have no enough balance",Toast.LENGTH_SHORT).show()


            }else
                binding.paypalAmountId.setError("Input minimum amount 5$")
            binding.paypalId.setError("Input address")


        }

        binding.upiBtn.setOnClickListener {

             upiAddesh = binding.upiId.text.toString()
              upiAmount = binding.upiAmountId.text.toString()
            if (upiAmount.isNotEmpty() && upiAmount.toDoubleOrNull() != null && upiAmount.toDouble() >= 100 && upiAddesh.isNotEmpty()  ){

                if ( total_inr >=100){

                    val upi = upiAmount.toDouble()

                    val dollor = upi / dollorRate

                    currentUser?.email?.let { it1 -> withdrawalUsdt(dollor, it1) }
                    paymentRequest("upi")
                }else
                    Toast.makeText(this,"You have no enough balance",Toast.LENGTH_SHORT).show()


            }else
                binding.upiAmountId.setError("Input minimum amount 100 rs")
               binding.upiId.setError("Input address")
        }




        binding.btcBtn.setOnClickListener {


            btcAddesh = binding.btcId.text.toString()
            btcAmount = binding.btcAmountId.text.toString()
            if (btcAmount.isNotEmpty() && btcAmount.toDoubleOrNull() != null && btcAmount.toDouble() >= 0.00005 && btcAddesh.isNotEmpty()  ){

                if ( btcBalance >=0.00005){

                    val btcamount = btcAmount.toDouble()

                    val btc = btcamount

                    currentUser?.email?.let { it1 -> withdrawalBtc(btc, it1) }
                    paymentRequest("btc")
                }else
                    Toast.makeText(this,"You have no enough balance",Toast.LENGTH_SHORT).show()


            }else

                binding.btcAmountId.setError("Input minimum amount 0.00005 ")
            binding.btcId.setError("Input address")
        }



        binding.usdtBtn.setOnClickListener {


             usdtAddesh = binding.usdtId.text.toString()
             usdtAmount = binding.usdtAmountId.text.toString()
            if (usdtAmount.isNotEmpty() && usdtAmount.toDoubleOrNull() != null && usdtAmount.toDouble() >= 5 && usdtAddesh.isNotEmpty()  ){

                if ( totalWalletBalances >=5){

                    val usdtamount = usdtAmount.toDouble()

                    val dollor = usdtamount

                    currentUser?.email?.let { it1 -> withdrawalUsdt(dollor, it1) }
                    paymentRequest("usdt")
                }else
                    Toast.makeText(this,"You have no enough balance",Toast.LENGTH_SHORT).show()


            }else

                binding.usdtAmountId.setError("Input minimum amount 5$")
            binding.usdtId.setError("Input address")
        }

        currentUser?.email?.let { checkStatus(it) }
        currentUser?.email?.let { fetchWallateBalance(it) }

    }

    fun paymentRequest(paymnetMethod : String) {



        val url = "https://dreamindians.in/payment_request.php"

        val params = HashMap<String, String>()
        params["user_id"] = currentUser?.email!!

       when(paymnetMethod){
           "paypal" ->{


               params["method"] = "Paypal"
               params["amount"] = pypalAmount
               params["address"] = pypalAddesh
           }
           "upi" ->{


               params["method"] = "Upi"
               params["amount"] = upiAmount
               params["address"] = upiAddesh



           }
           "btc" -> {


               params["method"] = "Btc"
               params["amount"] = btcAmount
               params["address"] = btcAddesh
           }
           "usdt" ->{



               params["method"] = "USDT"
               params["amount"] = usdtAmount
               params["address"] = usdtAddesh
           }
       }



        val jsonObjectRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Request sent successfully!", Toast.LENGTH_SHORT).show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): MutableMap<String, String> {
                return params

            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }


    //withdrawal

    fun withdrawalUsdt (amount: Double , userId: String){

        val url = "https://dreamindians.in/withdrawal.php"

        val requestQueue = Volley.newRequestQueue(this)


        val params = HashMap<String, String>()
        params["user_id"] = userId // You need to get the user ID
        params["amount"] = amount.toString()



        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response (success or failure)
                Toast.makeText(this,"Balance Update success",Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        requestQueue.add(request)

    }

    fun withdrawalBtc (amount: Double , userId: String){

        val url = "https://dreamindians.in/btc_withdrawal.php"

        val requestQueue = Volley.newRequestQueue(this)


        val params = HashMap<String, String>()
        params["user_id"] = userId // You need to get the user ID
        params["wallet_balance"] = amount.toString()



        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response (success or failure)
                Toast.makeText(this,"Balance Update success",Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        requestQueue.add(request)

    }

    fun checkStatus (userId : String){

        val url = "https://dreamindians.in/payment_reject.php?user_id=$userId"

        val statusRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val status = jsonResponse.getString("status")
                if (status == "approved") {
                    Toast.makeText(this, "Payment Approved!", Toast.LENGTH_SHORT).show()
                } else if (status == "rejected") {
                    Toast.makeText(this, "Payment Rejected.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error checking status.", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(statusRequest)

    }


    private fun fetchWallateBalance (userId: String){

        var uri = "https://dreamindians.in/get_balance.php?user_id=$userId"

        val stringRequest = StringRequest(
            Request.Method.GET,uri,
            {response ->

                try {

                    val jsonResponse = JSONObject(response)

                    // Extract wallet_balance and user_id
                    val walletBalance = jsonResponse.getString("wallet_balance")
                    val userId = jsonResponse.getString("user_id")
                    val usdt = jsonResponse.getString("usdt")

                    // Update the TextViews with wallet_balance and user_id
                   /* binding.walletBalance.text = "$walletBalance"
                    binding.walletUstBalance.text = "$usdt$"*/

                     btcBalance =walletBalance.toDouble()
                    val usdtBalance = usdt.toDouble()

                    val btc_to_dollor_convert = btcBalance * btc_valu

                    val inr : Int = 80



                     totalWalletBalances = btc_to_dollor_convert + usdtBalance + earns

                     total_inr = totalWalletBalances * inr

                   /* binding.totalWalletBalanceInr.text = String.format("%.1f",total_inr) +" inr"


                    binding.totalWalletBalance.text= String.format("%.1f" ,totalWalletBalances)+ "$"*/




                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }

                // binding.walletBalance.text = response.toString()

            },
            {error ->

                Toast.makeText(this,"error${error.message}",Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun fetchUserRaferBalance (email: String){

        var uri = "https://dreamindians.in/get_save_user_data.php?email=$email"

        val stringRequest = StringRequest(
            Request.Method.GET,uri,
            {response ->

                try {

                    val jsonResponse = JSONObject(response)

                    // Extract wallet_balance and user_id
                    val referral_code = jsonResponse.getString("referral_code")
                    val earn = jsonResponse.getString("earn")


                    earns = earn.toDouble()





                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }

                // binding.walletBalance.text = response.toString()

            },
            {error ->

                Toast.makeText(this,"error${error.message}",Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }
}