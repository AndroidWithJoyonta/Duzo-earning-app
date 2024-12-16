package com.mining.duzo.allEarningActivity

import android.os.Bundle
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
import com.example.mygallaray.Admob.RewardListener
import com.example.mygallaray.Constant
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.FirebaseAuth
import com.mining.duzo.R
import com.mining.duzo.databinding.ActivityWatchAndEarnBinding
import org.json.JSONException
import org.json.JSONObject

class WatchAndEarnActivity : AppCompatActivity() {


    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private val currentUser = firebaseAuth.currentUser

    lateinit var binding: ActivityWatchAndEarnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchAndEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Constant.admob = Admob(this,false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadBanner(binding.adView,false)
        Constant.admob!!.loadNative(binding.frame)

        binding.backBtn.setOnClickListener {

            onBackPressed()
        }

        binding.watchAndEarn.setOnClickListener {

            rewardAds()
        }

        fetchWallateBalance(currentUser?.email!!)

    }



       private  fun rewardAds(){

           Constant.admob!!.showRewarded(object : RewardListener {
               override fun onComplete() {


                   Toast.makeText(this@WatchAndEarnActivity, "complete", Toast.LENGTH_SHORT).show()
                   //saveWalletBalanceToServer(rewords.toString())
               }

               override fun onClick() {
                   Toast.makeText(this@WatchAndEarnActivity, "click", Toast.LENGTH_SHORT).show()
               }

               override fun onFailed(reason: String?) {
                   Toast.makeText(this@WatchAndEarnActivity, reason, Toast.LENGTH_SHORT).show()
               }
           })

       }

    private fun saveWalletBalanceToServer(newUSDTBalance: String){

        val url = "https://dreamindians.in/usdt.php"

        val requestQueue = Volley.newRequestQueue(this)


        val params = HashMap<String,String>()

        params["user_id"] = currentUser?.email!! // You need to get the user ID

        params["usdt"] = newUSDTBalance

        val request = object : StringRequest(Method.POST,url,

            Response.Listener { response ->

                Toast.makeText(this,"Balance update successfully",Toast.LENGTH_SHORT).show()


            },Response.ErrorListener {
              error ->

                error.printStackTrace()

            }){


            override fun getParams(): Map<String, String> {
                return params
            }
        }

                requestQueue.add(request)


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

                    binding.balanceTextView.text = String.format("%.3f",usdtBalance)+"$"

                    val inr : Int = 80








                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }

                // binding.walletBalance.text = response.toString()

            },
            {error ->

                Toast.makeText(this,"error${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }
}