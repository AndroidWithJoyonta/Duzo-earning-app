package com.mining.duzo.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mygallaray.Admob
import com.example.mygallaray.Admob.RewardListener
import com.example.mygallaray.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mining.duzo.databinding.FragmentMiningBinding
import kotlin.random.Random

class MiningFragment : Fragment() {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = firebaseAuth.currentUser
    private var userBalance: Double = 0.0
    private var mybalance : Int = 0

    private var walletBalance: Double = 0.0

    private var isMining = false
    private var click = false
    private var earnedBitcoin = 0.0
    private var bitcoinPrice = 60000.0  // Price per Bitcoin in USD
    private val handler = Handler()// Bitcoin per second (example rate)

    lateinit var binding: FragmentMiningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMiningBinding. inflate(inflater, container, false)


        Constant.admob = Admob(requireActivity(),false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")


        Constant.admob!!.loadBanner(binding.adView,false)
        Constant.admob!!.loadNative(binding.frame)


     askNotificationPermission()

        if (currentUser == null) {
            // Redirect to login if user is not authenticated
            Toast.makeText(context, "Please log in", Toast.LENGTH_SHORT).show()
            // Start login activity or show login UI

        }else{
            currentUser?.email?.let { insertUserWallet(it,walletBalance) }

        }

        binding.mineButton.setOnClickListener {

            if (isMining) {
                stopMining()


            } else {
                startMining()
            }

        }



        return binding.root
    }

    private fun startMining() {
        isMining = true
        earnedBitcoin = 0.0

        Toast.makeText(context, "Mining Started", Toast.LENGTH_SHORT).show()
        binding.mineButton.text = "Stop Mining"

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isMining) {
                    // Generate random Bitcoin earnings (simulate mining)
                    val randomEarningPerSecond = Random.nextDouble(0.0000000001, 0.00000000012)  // Between 0.000001 BTC to 0.000005 BTC
                    earnedBitcoin += randomEarningPerSecond
                    val usdEarning = earnedBitcoin * bitcoinPrice

                    // Update UI
                    binding.mining.text = String.format(
                        " %.12f",
                        earnedBitcoin, usdEarning
                    )

                    // Call handler again after 1 second
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun stopMining() {
        isMining = false
        binding.mineButton.text = "Start Mining"

        Toast.makeText(context, "Mining Stop", Toast.LENGTH_SHORT).show()
        rewardAds()

        // Show final earnings
        val finalUsdEarning = earnedBitcoin * bitcoinPrice
        binding.mining.text = String.format(
            "%.12f",
            earnedBitcoin, finalUsdEarning


        )

        //val btc = earnedBitcoin+earnedBitcoin


        currentUser?.email?.let { saveWalletBalanceToServer(it,earnedBitcoin) }
        // Save to server

    }
    private  fun rewardAds(){

        Constant.admob!!.showRewarded(object : RewardListener {
            override fun onComplete() {
                Toast.makeText(requireContext(), "complete", Toast.LENGTH_SHORT).show()
            }

            override fun onClick() {
                Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
            }

            override fun onFailed(reason: String?) {
                Toast.makeText(requireContext(), reason, Toast.LENGTH_SHORT).show()
            }
        })

    }



    private fun insertUserWallet(userId: String, walletBalance: Double) {
        val url = "https://dreamindians.in/insert_wallet.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
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
                    "usdt" to mybalance.toString()
                )
            }
        }

        requestQueue.add(stringRequest)
    }


    // Send wallet balance to PHP server
    private fun saveWalletBalanceToServer(userId: String,balance: Double) {


        val url = "https://dreamindians.in/update_wallet_balance.php"
        val requestQueue = Volley.newRequestQueue(context)



        val params = HashMap<String, String>()
        params["user_id"] = currentUser?.email!! // You need to get the user ID
        params["wallet_balance"] = balance.toString()



        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response (success or failure)
                Toast.makeText(context,"Balance Update success",Toast.LENGTH_SHORT).show()
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




            },
            { error ->
                // Handle errors, e.g., show a Toast message
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(stringRequest)
    }



    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }



}