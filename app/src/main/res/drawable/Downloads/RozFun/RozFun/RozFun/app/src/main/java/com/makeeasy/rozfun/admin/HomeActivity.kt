package com.makeeasy.rozfun.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.service.API
import com.makeeasy.rozfun.service.CustomToast
import com.squareup.picasso.Picasso
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    companion object {
        var isLogin: Boolean = false
        lateinit var setting: JSONObject
    }
    private var type = 0
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Handler(Looper.myLooper()!!).postDelayed({
            findViewById<RelativeLayout>(R.id.splashScreen).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.dashboardScreen).visibility = View.VISIBLE
            LoginSheet(this,object: LoginSheet.OnCompleteListener {
                override fun onCompleted(data: JSONObject) {
                    isLogin = true
                    setting = data
                    CustomToast(this@HomeActivity,"Login success!",CustomToast.GREEN).show()
                }
                override fun onError(msg: String) {
                    CustomToast(this@HomeActivity,msg).show()
                }
            }).show(supportFragmentManager,"Admin Login")
        },3000)
        val charts = findViewById<ImageView>(R.id.chartView)
        Picasso.get().load(API().getChart()).into(charts)
        val switch = findViewById<View>(R.id.type)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        setRecyclerViewAdapter()
        switch.setOnClickListener {
            if(type == 0) {
                type = 1
                switch.setBackgroundResource(R.drawable.grid)
                recyclerView.layoutManager = GridLayoutManager(this,2)
            } else {
                type = 0
                switch.setBackgroundResource(R.drawable.list)
                recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            }
            setRecyclerViewAdapter()
        }
    }

    private fun setRecyclerViewAdapter() {
        recyclerView.adapter = HomeAdapter(type,object: HomeAdapter.OnSelectListener {
            override fun onSelect(position: Int, title: String) {
                when (position) {
                    0 -> {
                        LoginSheet(this@HomeActivity,object: LoginSheet.OnCompleteListener {
                            override fun onCompleted(data: JSONObject) {
                                isLogin = true
                                setting = data
                                CustomToast(this@HomeActivity,"Update success!",CustomToast.GREEN).show()
                            }
                            override fun onError(msg: String) {
                                CustomToast(this@HomeActivity,msg).show()
                            }
                        }).show(supportFragmentManager,title)
                    }
                    1 -> {
                        SpinSheet(this@HomeActivity,object: SpinSheet.OnCompleteListener{
                            override fun onCompleted(data: JSONObject) {
                                setting = data
                                CustomToast(this@HomeActivity,"Update success!",CustomToast.GREEN).show()
                            }
                            override fun onError(msg: String) {
                                CustomToast(this@HomeActivity,msg,CustomToast.RED).show()
                            }
                        }).show(supportFragmentManager,title)
                    }
                    2 -> {
                        ListActivity.listType = 0
                        startActivity(Intent(this@HomeActivity,ListActivity::class.java))
                    }
                    3 -> {
                        ListActivity.listType = 1
                        startActivity(Intent(this@HomeActivity,ListActivity::class.java))
                    }
                    4 -> {
                        ListActivity.listType = 2
                        startActivity(Intent(this@HomeActivity,ListActivity::class.java))
                    }
                    5 -> {
                        SettingSheet(this@HomeActivity,object:SettingSheet.OnCompleteListener {
                            override fun onCompleted(data: JSONObject) {
                                setting = data
                                CustomToast(this@HomeActivity,"Update success!",CustomToast.GREEN).show()
                            }
                            override fun onError(msg: String) {
                                CustomToast(this@HomeActivity,msg,CustomToast.RED).show()
                            }
                        }).show(supportFragmentManager,title)
                    }
                }
            }
        })
    }
}