package com.makeeasy.rozfun.admin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.model.User
import com.makeeasy.rozfun.service.API
import com.makeeasy.rozfun.service.Constant
import com.makeeasy.rozfun.service.CustomToast
import org.json.JSONArray
import org.json.JSONException

class UserSheet(private val ctx: Context, val user: User) : BottomSheetDialogFragment() {

    private var options = arrayOf("Block", "Active")

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_user, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        val title = v.findViewById<TextView>(R.id.title)
        val deposit = v.findViewById<EditText>(R.id.deposit)
        val withdraw = v.findViewById<EditText>(R.id.withdraw)
        title.text = tag
        deposit.setText(user.point)
        withdraw.setText(user.redeem)
        val points = v.findViewById<Spinner>(R.id.count)
        val ad = ArrayAdapter(ctx,android.R.layout.simple_spinner_item,options)
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        points.adapter = ad
        points.setSelection(Constant.user.status.toInt())
        v.findViewById<LinearLayout>(R.id.button).setOnClickListener {
            if(points.selectedItem.toString() != "" && deposit.text.toString() != "" && withdraw.text.toString() != "") {
                v.findViewById<ProgressBar>(R.id.process).visibility = View.VISIBLE
                updateUsers(deposit.text.toString(),withdraw.text.toString(),points.selectedItemPosition.toString())
            } else {
                Toast.makeText(ctx,"Fill all fields!",Toast.LENGTH_SHORT).show()
            }
        }
        isCancelable = true
        return v
    }

    private fun updateUsers(de: String,wi: String,st: String) {
        Volley.newRequestQueue(ctx).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    dismiss()
                    CustomToast(ctx,"Updated!",CustomToast.GREEN).show()
                } else {
                    dismiss()
                    CustomToast(ctx,"Error!",CustomToast.RED).show()
                }
            } catch (e: JSONException) {
                dismiss()
                CustomToast(ctx,e.message,CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            dismiss()
            CustomToast(ctx,error.message,CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "updateusr"
                params["id"] = user.id
                params["depo"] = de
                params["with"] = wi
                params["status"] = st
                return params
            }
        })
    }
}