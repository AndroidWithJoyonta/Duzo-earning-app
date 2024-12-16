package com.mining.duzo

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mygallaray.Admob
import com.example.mygallaray.Constant
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mining.duzo.databinding.ActivityLoginBinding
import kotlin.random.Random


class LoginActivity : AppCompatActivity() {


    lateinit var  user : FirebaseUser


  private lateinit var auth: FirebaseAuth
  private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in
            navigateToMainScreen()
        } else {
            // Not signed in, proceed with Google sign-in

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.googleSignInButton.setOnClickListener {
            googleSingIn()
        }

        binding.termsOfService.setOnClickListener {

            termsCondition()

        }


    }


    //privacy_policy_link_open_code
    private fun termsCondition() {
        try {
            val download_link = "https://sites.google.com/view/terms-and-conditions09/home"
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(download_link))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this, "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }


    private fun googleSingIn(){

        val singInClient = googleSignInClient.signInIntent
        launcher.launch(singInClient)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        result ->

        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            
            manageResulta(task)
        }
    }

    private fun manageResulta(task: Task<GoogleSignInAccount>) {

        val account :GoogleSignInAccount? = task.result

        if (account != null){

            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            auth.signInWithCredential(credential).addOnCompleteListener {


                if (task.isSuccessful){



                     user = auth.currentUser!!


                    if (user != null) {
                        if (user.metadata?.creationTimestamp == user.metadata?.lastSignInTimestamp) {
                            // New user, show "Account Created"
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()

                            referDialoge()
                        } else {
                            // Returning user, show "Login Successful"
                            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
                            navigateToMainScreen()
                        }

                        sendUserDataToServer(user?.displayName,user?.email)

                    }
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()





            }
            }


        } else{
            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()

        }


    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()  // Close this activity
    }



    private fun sendUserDataToServer(name: String?, email: String?) {
        val url = "https://dreamindians.in/save_user.php"

       var refer = genaraterafercode()


        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(this, "Server Response: $response", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Server Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name ?: ""
                params["email"] = email ?: ""
                params["referral_code"] = refer
                return params
            }
        }

        queue.add(stringRequest)
    }

    fun genaraterafercode () : String{
        return (100000..999999).random().toString()
    }

   fun referDialoge (){

       val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialoge, null)

       // Get references to UI components in the dialog
       val referCode: EditText = dialogView.findViewById(R.id.referCode)
       val skip: Button = dialogView.findViewById(R.id.skip)
       val claim: Button = dialogView.findViewById(R.id.claim)


       // Create the AlertDialog
       val alertDialog = AlertDialog.Builder(this)
           .setView(dialogView)
           .create()



       claim.setOnClickListener {

           val ref = referCode.text.toString()


           val url = "https://dreamindians.in/refer.php"  // Replace with your PHP file URL

           val stringRequest = object : StringRequest(
               Request.Method.POST, url,
               Response.Listener { response ->
                   // Handle response
                   if (response.contains("User referrer  successfully")) {
                       // Referral code is valid, move to the next activity
                       Toast.makeText(this, "User referrer  successfully", Toast.LENGTH_SHORT).show()

                      navigateToMainScreen()
                   } else {
                       // Referral code is invalid
                       Toast.makeText(this, "Invalid Referral Code", Toast.LENGTH_SHORT).show()

                   }
               },
               Response.ErrorListener { error ->
                  // Toast.makeText(this, "Request failed: ${error.message}", Toast.LENGTH_SHORT).show()
               }) {

               override fun getParams(): Map<String, String> {
                   val params = HashMap<String, String>()

                   params["referral_code"] = ref
                   return params
               }
           }

           // Add the request to the request queue
           val requestQueue = Volley.newRequestQueue(this)
           requestQueue.add(stringRequest)

       }

       sendReferDataToServer(user?.displayName,user?.email)


       // Set the click listener for the OK button
       skip.setOnClickListener {

           navigateToMainScreen()

           alertDialog.dismiss()
       }

       alertDialog.show()
   }


    private fun sendReferDataToServer(name: String?, email: String?) {
        val url = "https://dreamindians.in/earning/save_user.php"

        var refer = genaraterafercode()


        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(this, "Server Response: $response", Toast.LENGTH_SHORT).show()

            },
            { error ->
                Toast.makeText(this, "Server Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["referral_code"] = refer
                return params
            }
        }

        queue.add(stringRequest)
    }
   }
