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

class SettingSheet(private val ctx: Context, val onCompleteListener: OnCompleteListener) : BottomSheetDialogFragment() {

    private lateinit var points: String
    private lateinit var user: String
    private lateinit var pass: String
    interface OnCompleteListener {
        fun onCompleted(data: JSONObject)
        fun onError(msg: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_setting, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        val title = v.findViewById<TextView>(R.id.title)
        title.text = tag
        user = HomeActivity.setting.getString("user")
        pass = HomeActivity.setting.getString("pass")
        points = HomeActivity.setting.getString("points")
        val mina = v.findViewById<EditText>(R.id.minadd)
        val minw = v.findViewById<EditText>(R.id.minwith)
        val ptmi = v.findViewById<EditText>(R.id.pid)
        val ptmk = v.findViewById<EditText>(R.id.pkey)
        val rzri = v.findViewById<EditText>(R.id.rid)
        val rzrk = v.findViewById<EditText>(R.id.rkey)
        val csfi = v.findViewById<EditText>(R.id.cid)
        val chrg = v.findViewById<EditText>(R.id.charge)
        mina.setText(HomeActivity.setting.getString("deposit"))
        minw.setText(HomeActivity.setting.getString("redeem"))
        ptmi.setText(HomeActivity.setting.getString("paytm_id"))
        ptmk.setText(HomeActivity.setting.getString("paytm_key"))
        rzri.setText(HomeActivity.setting.getString("razorpay_id"))
        rzrk.setText(HomeActivity.setting.getString("razorpay_key"))
        csfi.setText(HomeActivity.setting.getString("cashfree_id"))
        chrg.setText(HomeActivity.setting.getString("charge"))
        v.findViewById<LinearLayout>(R.id.button).setOnClickListener {
            if(mina.text.toString() != "" && minw.text.toString() != "" && chrg.text.toString() != "") {
                v.findViewById<ProgressBar>(R.id.process).visibility = View.VISIBLE
                updateSettings(mina.text.toString(),minw.text.toString(),ptmi.text.toString(),ptmk.text.toString(),
                    rzri.text.toString(),rzrk.text.toString(),csfi.text.toString(),chrg.text.toString())
            } else {
                Toast.makeText(ctx,"Please fill the details",Toast.LENGTH_SHORT).show()
            }
        }
        isCancelable = true
        return v
    }

    private fun updateSettings(mina:String,minw:String,ptmi:String,ptmk:String,rzri:String,rzrk:String,csfi:String,chrg:String) {
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