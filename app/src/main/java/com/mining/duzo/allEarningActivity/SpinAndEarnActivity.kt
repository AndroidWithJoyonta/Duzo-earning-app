package com.mining.duzo.allEarningActivity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
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
import com.mining.duzo.databinding.ActivitySpinAndEarnBinding
import org.json.JSONException
import org.json.JSONObject

class SpinAndEarnActivity : AppCompatActivity() {


    // Prizes on the wheel
    val labels = listOf("0.00$", "0.00003$", "0.0$", "0.0$","0.0003$","0.003$","0.0$", "0.0$")



    lateinit var binding: ActivitySpinAndEarnBinding
    lateinit var spinWheel: View

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = firebaseAuth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinAndEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Initialize the spinWheel view
        spinWheel = binding.customSpinWheel

        binding.backBtn.setOnClickListener {

            onBackPressed()
        }

        Constant.admob = Admob(this,false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadBanner(binding.adView,false)
        Constant.admob!!.loadNative(binding.frame)


        // Set up the spin button click listener
        binding.spinButton.setOnClickListener {
            val randomAngle = (720..1440).random() // Random angle for multiple rotations
            val animator = ObjectAnimator.ofFloat(spinWheel, "rotation", 0f, randomAngle.toFloat())
            animator.duration = 3000 // Duration in milliseconds
            animator.interpolator = DecelerateInterpolator()

            // Attach the listener to handle spin results
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    val finalAngle = (spinWheel.rotation % 360) // Get the final angle
                    val segment = (finalAngle / (360f / labels.size)).toInt() // Calculate the segment
                    val prize = labels[segment]// Get the prize based on the segment
                    Toast.makeText(this@SpinAndEarnActivity, "You won: $prize!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SpinAndEarnActivity,SpinAndEarnActivity::class.java)
                    finish()
                    startActivity(intent)

                    saveWalletBalanceToServer(prize)

                }

                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })

            animator.start() // Start the animation
        }
        fetchWallateBalance(currentUser?.email!!)

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