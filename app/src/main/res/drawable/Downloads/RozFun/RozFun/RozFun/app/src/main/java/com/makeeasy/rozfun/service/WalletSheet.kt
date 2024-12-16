package com.makeeasy.rozfun.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.admin.TxnAdapter
import com.makeeasy.rozfun.model.Txn
import org.json.JSONArray
import org.json.JSONException

class WalletSheet(private val ctx: Context, private val onActionSelectListener: OnActionSelectListener) : BottomSheetDialogFragment() {

    interface OnActionSelectListener {
        fun onSelect(type: Int)
    }
    private lateinit var txnView: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_wallet, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        txnView = v.findViewById(R.id.txnView)
        val totalBal = v.findViewById<TextView>(R.id.totalbal)
        val dBal = v.findViewById<TextView>(R.id.dbal)
        val wBal = v.findViewById<TextView>(R.id.wbal)
        val title = v.findViewById<TextView>(R.id.title)
        title.text =tag
        dBal.text = Constant.user.point
        wBal.text = Constant.user.redeem
        totalBal.text = (Constant.user.point.toInt() + Constant.user.redeem.toInt()).toString()
        v.findViewById<CardView>(R.id.addmoney).setOnClickListener {
            dismiss()
            onActionSelectListener.onSelect(0)
        }
        v.findViewById<CardView>(R.id.withdraw).setOnClickListener {
            dismiss()
            onActionSelectListener.onSelect(1)

        }
        loadTransactions()
        isCancelable = true
        return v
    }

    private fun loadTransactions() {
        val txnList: MutableList<Txn> = ArrayList()
        Volley.newRequestQueue(ctx).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            if(response != null) {
                try {
                    val resp = JSONArray(response)
                    for (i in 0 until resp.length()) {
                        val obj = resp.getJSONObject(i)
                        txnList.add(Txn(obj.getString("id"),obj.getString("type"),obj.getString("device"),obj.getString("amount"),
                            obj.getString("detail"),obj.getString("orderid"),obj.getString("txnid"),obj.getString("status")))
                    }
                    txnView.adapter = TxnAdapter(ctx, txnList, false)
                } catch (e: JSONException) {
                    e.fillInStackTrace()
                }
            }
        },Response.ErrorListener {
            Toast.makeText(ctx,"Server error!",Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "mytxn"
                params["dev"] = Constant.user.device
                return params
            }
        })
    }
}