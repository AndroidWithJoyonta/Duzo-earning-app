package com.mining.duzo.fragments

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mygallaray.Admob
import com.example.mygallaray.Constant
import com.google.firebase.auth.FirebaseAuth
import com.mining.duzo.WithdrawActivity
import com.mining.duzo.databinding.FragmentSettingBinding
import org.json.JSONException
import org.json.JSONObject


class SettingFragment : Fragment() {


    lateinit var binding: FragmentSettingBinding

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private val currentUser = firebaseAuth.currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSettingBinding.inflate(inflater, container, false)


        Constant.admob = Admob(requireActivity(),false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadNative(binding.frame)


        binding.shareBtn.setOnClickListener {
            context?.let { it1 -> ShareApp(it1) }
        }

        binding.policyBtn.setOnClickListener {
            privacy_policy()
        }

        binding.rateBtn.setOnClickListener {
            rateUSMethod()
        }

        binding.withdrawBtn.setOnClickListener{

            val intent = Intent(context,WithdrawActivity::class.java)
            startActivity(intent)
        }


        // Set click listener on the Copy button
        binding.rafer.setOnClickListener {
            val textToCopy = binding.raferId.text.toString()

            // Get the Clipboard Manager system service
            val clipboard: ClipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            // Create a ClipData object with the text to copy
            val clip = ClipData.newPlainText("Copied Text", textToCopy)

            // Set the ClipData to the clipboard
            clipboard.setPrimaryClip(clip)

            // Show a toast message to confirm the copy action
            Toast.makeText(requireContext(), "Text copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        currentUser?.email?.let { fetchUserRaferBalance(it) }




        return binding.root
    }




    //Share App code
    private fun ShareApp(context: Context) {
        // code here
        val appPakageName = context.packageName
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Download Now : https://play.google.com/store/apps/details?id=$appPakageName"
        )
        sendIntent.setType("text/plain")
        context.startActivity(sendIntent)
    }

    //privacy_policy_link_open_code
    private fun privacy_policy() {
        try {
            val download_link = "https://sites.google.com/view/privacy-policy--e/home"
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(download_link))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    private fun rateUSMethod() {
        val appName = requireContext().packageName
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "http://play.google.com/store/apps/details?id=$appName"
            )
        )
        requireContext().startActivity(intent)
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

                    val code = referral_code.toInt()

                  /*  val sharedPreferences = requireContext().getSharedPreferences("app", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.putString("save", referral_code)

                    editor.apply()


                    val sharedPreferences1 = requireContext().getSharedPreferences("app", MODE_PRIVATE)

                    val saveRefer =sharedPreferences1.getString("save","no saved")*/

                    //earns = earn.toDouble()


                    binding.raferId.text = referral_code




                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    //Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show()
                }

                // binding.walletBalance.text = response.toString()

            },
            {error ->

                Toast.makeText(context,"error${error.message}",Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(stringRequest)
    }



}