package com.mining.duzo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mygallaray.Admob
import com.example.mygallaray.Constant
import com.google.firebase.auth.FirebaseAuth
import com.mining.duzo.databinding.ActivityWalletBinding
import org.json.JSONException
import org.json.JSONObject


class WalletActivity : AppCompatActivity() {

   private val firebaseAuth : FirebaseAuth =FirebaseAuth.getInstance()

    private val currentUser = firebaseAuth.currentUser


    private var btc_valu : Int = 60000

    private var totalWalletBalance: Double = 0.0

    private var earns: Double = 0.0

    lateinit var binding: ActivityWalletBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)



        Constant.admob = Admob(this,false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadBanner(binding.adView,false)
        Constant.admob!!.loadNative(binding.frame)

        binding.toolbar.setNavigationOnClickListener {



            onBackPressed()

        }





        fetchWallateBalance(currentUser?.email!!)
        fetchUserRaferBalance(currentUser?.email!!)



    }


    private fun saveVisibilityState(isVisible: Boolean) {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putBoolean("isTextViewVisible", isVisible)
        editor.apply()
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



                    val btcBalance =walletBalance.toDouble()
                    val usdtBalance = usdt.toDouble()

                    binding.walletUstBalance.text = String.format("%.3f",usdtBalance)+ "$"

                    binding.walletBalance.text = String.format("%.12f",btcBalance)


                    val btc_to_dollor_convert = btcBalance * btc_valu

                    val inr : Int = 78



                    val totalWalletBalances = btc_to_dollor_convert + usdtBalance + earns

                    val total_inr = totalWalletBalances * inr

                    binding.totalWalletBalanceInr.text = String.format("%.3f",total_inr) +"₹"


                  binding.totalWalletBalance.text= String.format("%.3f" ,totalWalletBalances)+ "$"




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

        val uri = "https://dreamindians.in/get_save_user_data.php?email=$email"

        val stringRequest = StringRequest(
            Request.Method.GET,uri,
            {response ->

                try {

                    val jsonResponse = JSONObject(response)

                    // Extract wallet_balance and user_id
                    val referral_code = jsonResponse.getString("referral_code")
                    val earn = jsonResponse.getString("earn")







                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    //Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }

                // binding.walletBalance.text = response.toString()

            },
            {error ->

                Toast.makeText(this,"error${error.message}",Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }


    fun toolManu(){

    }


    }


