package com.makeeasy.rozfun.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.service.API
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SpinSheet(private val ctx: Context,val onCompleteListener: OnCompleteListener) : BottomSheetDialogFragment() {

    interface OnCompleteListener {
        fun onCompleted(data: JSONObject)
        fun onError(msg: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_spin, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        val title = v.findViewById<TextView>(R.id.title)
        title.text = tag
        val points = v.findViewById<EditText>(R.id.count)
        points.setText(HomeActivity.setting.getString("points"))
        v.findViewById<LinearLayout>(R.id.button).setOnClickListener {
            if(points.text.toString() != "" && points.text.toString().split(",").size > 3) {
                v.findViewById<ProgressBar>(R.id.process).visibility = View.VISIBLE
                updateSettings(points.text.toString())
            } else {
                Toast.makeText(ctx,"Minimum pie count is 4",Toast.LENGTH_SHORT).show()
            }
        }
        isCancelable = true
        return v
    }

    private fun updateSettings(points:String) {
        val mina = HomeActivity.setting.getString("deposit")
        val minw = HomeActivity.setting.getString("redeem")
        val ptmi = HomeActivity.setting.getString("paytm_id")
        val ptmk = HomeActivity.setting.getString("paytm_key")
        val rzri = HomeActivity.setting.getString("razorpay_id")
        val rzrk = HomeActivity.setting.getString("razorpay_key")
        val csfi = HomeActivity.setting.getString("cashfree_id")
        val chrg = HomeActivity.setting.getString("charge")
        val user = HomeActivity.setting.getString("user")
        val pass = HomeActivity.setting.getString("pass")
        Volley.newRequestQueue(ctx).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    dismiss()
                    onCompleteListener.onCompleted(obj.getJSONObject("data"))
                } else {
                    dismiss()
                    onCompleteListener.onError("Error!")
                }
            } catch (e: JSONException) {
                dismiss()
                onCompleteListener.onError(e.message!!)
            }
        },Response.ErrorListener { error ->
            dismiss()
            onCompleteListener.onError(error.message!!)
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "set_setting"
                params["ap"] = points
                params["ar"] = minw
                params["ad"] = mina
                params["arid"] = rzri
                params["arkey"] = rzrk
                params["apid"] = ptmi
                params["apkey"] = ptmk
                params["acid"] = csfi
                params["ac"] = chrg
                params["aun"] = user
                params["aps"] = pass
                return params
            }
        })
    }
}