package com.makeeasy.rozfun

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.makeeasy.rozfun.model.User
import com.makeeasy.rozfun.service.API
import com.makeeasy.rozfun.service.Constant
import com.makeeasy.rozfun.service.CustomToast
import com.makeeasy.rozfun.service.Network
import com.makeeasy.rozfun.service.RegisterSheet
import org.json.JSONArray
import org.json.JSONException

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if(Network(this).isOnline() == true) {
            loadSettings()
        } else {
            CustomToast(this,"No internet found!",CustomToast.RED).show()
        }
    }

    private fun loadSettings() {
        Volley.newRequestQueue(this).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                Constant.points = obj.getString("points")
                Constant.minimumAdd = obj.getString("deposit")
                Constant.minimumRedeem = obj.getString("redeem")
                Constant.paytmMID = obj.getString("paytm_id")
                Constant.paytmMKEY = obj.getString("paytm_key")
                Constant.razorpayID = obj.getString("razorpay_id")
                Constant.razorpayKEY = obj.getString("razorpay_key")
                Constant.cashfreeID = obj.getString("cashfree_id")
                Constant.charge = obj.getString("charge")
                checkUserAvailability()
            } catch (e: JSONException) {
                CustomToast(this,e.message, CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            CustomToast(this,error.message, CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "get_setting"
                return params
            }
        })
    }

    @SuppressLint("HardwareIds")
    private fun checkUserAvailability() {
        val device = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
        Volley.newRequestQueue(this).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    val usr = obj.getJSONObject("user")
                    if(usr.getString("status") == "1") {
                        Constant.user = User(usr.getString("id"),usr.getString("name"),usr.getString("email"),
                            usr.getString("deposit"),usr.getString("withdraw"),usr.getString("referby"),
                            usr.getString("refercode"),usr.getString("status"))
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        CustomToast(this@SplashActivity,"Account Blocked!",CustomToast.RED).show()
                    }
                } else {
                    RegisterSheet(this,object: RegisterSheet.OnCompleteListener{
                        override fun onCompleted(status: Int) {
                            if(status == 1) {
                                checkUserAvailability()
                            } else {
                                CustomToast(this@SplashActivity,"Server Error!",CustomToast.RED).show()
                            }
                        }
                    }).show(supportFragmentManager,"Registration")
                }
            } catch (e: JSONException) {
                CustomToast(this,e.message, CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            CustomToast(this,error.message, CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "user"
                params["device"] = device
                return params
            }
        })
    }

}