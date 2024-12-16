package com.mining.duzo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mining.duzo.databinding.ActivityMainBinding
import com.mining.duzo.fragments.MiningFragment
import com.mining.duzo.fragments.SettingFragment
import com.mining.duzo.fragments.SpinFragment


class MainActivity : AppCompatActivity() {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = firebaseAuth.currentUser
    private var userBalance: Double = 0.0
    private var mybalance : Int = 0
    private var mybalance1 : Int = 0

    private var walletBalance: Double = 0.0

    private var isMining = false
    private var click = false
    private var earnedBitcoin = 0.0
    private var bitcoinPrice = 60000.0  // Price per Bitcoin in USD
    private val handler = Handler()// Bitcoin per second (example rate)


    lateinit var  fragmentManager: FragmentManager

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

     /*   if (currentUser == null) {
            // Redirect to login if user is not authenticated
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show()
            // Start login activity or show login UI
            return
        }else{
            currentUser?.email?.let { insertUserWallet(it,walletBalance) }

        }*/




      binding.walletIcon.setOnClickListener {

          val intent = Intent(this,WalletActivity::class.java)
          startActivity(intent)

      }


        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item->



            when(item.itemId){
                R.id.mining ->openFragment(MiningFragment())
                R.id.spin -> openFragment(SpinFragment())
                R.id.setting -> openFragment(SettingFragment())

            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(MiningFragment())
    }





  private fun openFragment(fragment: Fragment){

    val fragmentTransaction :FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frameLayout,fragment)
    fragmentTransaction.commit()

  }




    private fun insertUserWallet(userId: String, walletBalance: Double) {
        val url = "http://192.168.0.104/earning/insert_wallet.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                // Handle the server response
                if (response.contains("successfully")) {
                    println("Wallet created successfully!")
                } else {
                    println("Server Response: $response")
                }
            },
            { error ->
                // Handle the error
                println("Error: ${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "user_id" to userId,
                    "wallet_balance" to mybalance.toString(),
                    "usdt" to mybalance1.toString()
                )
            }
        }

        requestQueue.add(stringRequest)
    }


    // Send wallet balance to PHP server
    private fun saveWalletBalanceToServer(balance: Double) {

      /*  earnedBitcoin +=balance
        binding.balanceTextView.text = String.format(earnedBitcoin.toString())

            earnedBitcoin.toString()*/


        val url = "https://dreamindians.in/update_wallet_balance.php"
        val requestQueue = Volley.newRequestQueue(this)



        val params = HashMap<String, String>()
        params["user_id"] = currentUser?.email!! // You need to get the user ID
        params["wallet_balance"] = balance.toString()





        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                // Handle response (success or failure)
                Toast.makeText(this,"succes",Toast.LENGTH_SHORT).show()
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



    private fun fetchWalletBalance(userId: String) {



        val url = "https://dreamindians.in/get_balance.php?user_id=$userId"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Display the wallet balance in a TextView or use it as needed

                binding.balanceTextView.text = response.toString()



            },
            { error ->
                // Handle errors, e.g., show a Toast message
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }




}






