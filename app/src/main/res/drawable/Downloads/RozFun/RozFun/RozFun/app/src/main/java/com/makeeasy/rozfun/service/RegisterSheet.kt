package com.makeeasy.rozfun.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.Settings.Secure
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
import org.json.JSONArray
import org.json.JSONException

class RegisterSheet(private val ctx: Context, val onCompleteListener: OnCompleteListener) : BottomSheetDialogFragment() {

    interface OnCompleteListener {
        fun onCompleted(status: Int)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_register, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        val title = v.findViewById<TextView>(R.id.title)
        title.text = tag
        val user = v.findViewById<EditText>(R.id.user)
        val pass = v.findViewById<EditText>(R.id.pass)
        v.findViewById<LinearLayout>(R.id.button).setOnClickListener {
            if(user.text.toString() != "") {
                v.findViewById<ProgressBar>(R.id.process).visibility = View.VISIBLE
                tryToRegister(user.text.toString(), pass.text.toString())
            } else {
                Toast.makeText(ctx,"Please fill you name",Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }

    @SuppressLint("HardwareIds")
    private fun tryToRegister(user:String, pass: String) {
        val device = Secure.getString(ctx.contentResolver,Secure.ANDROID_ID)
        Volley.newRequestQueue(ctx).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    dismiss()
                    onCompleteListener.onCompleted(1)
                } else {
                    dismiss()
                    onCompleteListener.onCompleted(0)
                }
            } catch (e: JSONException) {
                dismiss()
                onCompleteListener.onCompleted(0)
            }
        },Response.ErrorListener {
            dismiss()
            onCompleteListener.onCompleted(0)
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "register"
                params["name"] = user
                params["code"] = pass
                params["device"] = device
                return params
            }
        })
    }

}