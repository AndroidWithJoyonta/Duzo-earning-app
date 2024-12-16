package com.makeeasy.rozfun.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.makeeasy.rozfun.MainActivity
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.model.Txn
import com.makeeasy.rozfun.model.User
import com.makeeasy.rozfun.service.API
import com.makeeasy.rozfun.service.Constant
import com.makeeasy.rozfun.service.CustomToast
import org.json.JSONArray
import org.json.JSONException

class ListActivity : AppCompatActivity() {

    companion object {
        var listType: Int = 0
    }
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        recyclerView = findViewById(R.id.recyclerView)
        when (listType) {
            0 -> {
                toolbar.title = "User List"
                loadUserList()
            }
            1 -> {
                toolbar.title = "All Transactions"
                loadAllTransactionList(false)
            }
            else -> {
                toolbar.title = "Withdraw Request"
                loadAllTransactionList(true)
            }
        }
    }

    private fun loadUserList() {
        val userList: MutableList<User> = ArrayList()
        Volley.newRequestQueue(this).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                for(i in 0 until resp.length()) {
                    val obj = resp.getJSONObject(i)
                    userList.add(User(obj.getString("id"),obj.getString("name"),obj.getString("email"),obj.getString("deposit"),
                        obj.getString("withdraw"),obj.getString("referby"),obj.getString("refercode"),obj.getString("status")))
                }
                recyclerView.adapter = UserAdapter(this,userList)
                findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
            } catch (e: JSONException) {
                CustomToast(this,e.message, CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
                CustomToast(this,error.message, CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "userlist"
                return params
            }
        })
    }

    private fun loadAllTransactionList(isWithdraw: Boolean) {
        val txnList: MutableList<Txn> = ArrayList()
        Volley.newRequestQueue(this).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                for(i in 0 until resp.length()) {
                    val obj = resp.getJSONObject(i)
                    if(isWithdraw) {
                        if(obj.getString("type") == "1" && obj.getString("status") == "0") {
                            txnList.add(Txn(obj.getString("id"),obj.getString("type"),obj.getString("device"),obj.getString("amount"),
                                obj.getString("detail"),obj.getString("orderid"),obj.getString("txnid"),obj.getString("status")))
                        }
                    } else {
                        txnList.add(Txn(obj.getString("id"),obj.getString("type"),obj.getString("device"),obj.getString("amount"),
                            obj.getString("detail"),obj.getString("orderid"),obj.getString("txnid"),obj.getString("status")))
                    }
                }
                recyclerView.adapter = TxnAdapter(this,txnList,isWithdraw)
                findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
            } catch (e: JSONException) {
                CustomToast(this,e.message, CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            CustomToast(this,error.message, CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "txnlist"
                return params
            }
        })
    }
}