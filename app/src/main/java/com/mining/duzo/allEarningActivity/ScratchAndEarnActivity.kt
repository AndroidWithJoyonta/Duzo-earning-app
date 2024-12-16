package com.mining.duzo.allEarningActivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mygallaray.Admob
import com.example.mygallaray.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mining.duzo.R
import com.mining.duzo.custom.ScratchView
import com.mining.duzo.databinding.ActivityScratchAndEarnBinding
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random

class ScratchAndEarnActivity : AppCompatActivity() {

    lateinit var binding: ActivityScratchAndEarnBinding

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    var value : Int = 0


    var isButtonClick = false

    val winPrice = listOf("0.00$", "0.00003$", "0.0$", "0.0$","0.0003$","0.003$","0.0$", "0.0$")
    var weights = arrayOf(1,9,1,1,1)
    val prize = winPrice[Random.nextInt(winPrice.size)]

   lateinit var  randomWinprice :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScratchAndEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Constant.admob = Admob(this,false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadBanner(binding.adView,false)
        Constant.admob!!.loadNative(binding.frame)

        binding.backBtn.setOnClickListener {

            onBackPressed()
        }

       var weightList = mutableListOf<String>()

      /*  for (i in winPrice.indices){
            repeat(weights[i]){
                weightList.add(winPrice[1])
            }
        }*/


         binding.claimRewad1.visibility = View.GONE

        // randomWinprice = weightList.random()


        binding.scratchCard.setOnScratchStartListener {

            binding.tvPrize.text = prize
            binding.claimRewad1.visibility = View.VISIBLE


        }

        binding.scratchCard.setOnScratchCompleteListener {
            // Show the claim button when scratching is completed
            binding.claimRewad.visibility = View.VISIBLE


        }

        binding.claimRewad1.setOnClickListener {

            if (!isButtonClick){
                award()
               binding.claimRewad1.text ="Claimed"

                binding.scratchCard.resetScratch()

                refreshPage(0)

            }else{

                Toast.makeText(this,"already been Claimed.",Toast.LENGTH_SHORT).show()

            }



        }

        binding.scratchCard.setOnTouchListener { _, _ ->



            if (binding.scratchCard.visibility == View.GONE) {

            }
            false

        }

        fetchWallateBalance(currentUser?.email!!)


    }

    fun award(){
        Toast.makeText(this,"You won $prize",Toast.LENGTH_SHORT).show()
        saveWalletBalanceToServer(prize)

    }

    private fun refreshPage(delayMillis :Long){

        Handler().postDelayed({
            finish()
            startActivity(intent)
        },delayMillis)
    }

    private fun saveWalletBalanceToServer(newUSDTBalance: String) {



        val url = "https://dreamindians.in/usdt.php"
        val requestQueue = Volley.newRequestQueue(this)



        val params = HashMap<String, String>()
        params["user_id"] = currentUser?.email!! // You need to get the user ID

        params["usdt"] = newUSDTBalance





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