package com.makeeasy.rozfun

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cashfree.pg.CFPaymentService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.makeeasy.rozfun.admin.HomeActivity
import com.makeeasy.rozfun.custom.LuckyItem
import com.makeeasy.rozfun.custom.LuckyWheelView
import com.makeeasy.rozfun.model.User
import com.makeeasy.rozfun.service.API
import com.makeeasy.rozfun.service.Constant
import com.makeeasy.rozfun.service.CustomToast
import com.makeeasy.rozfun.service.DepositSheet
import com.makeeasy.rozfun.service.ResultSheet
import com.makeeasy.rozfun.service.WalletSheet
import com.makeeasy.rozfun.service.WithdrawSheet
import com.paytm.pgsdk.PaytmConstants
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Random

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(),PaymentResultListener {

    private var developer = 0
    private val PAYMENT_CODE = 939
    private var addedAmount = 0
    private lateinit var orderId: String
    private var isPaymentProcessed = false
    private lateinit var balance: TextView
    private lateinit var btnPlay: CardView
    private lateinit var anim: LottieAnimationView
    private lateinit var luckyWheelView: LuckyWheelView
    private val data: MutableList<LuckyItem> = ArrayList()
    private lateinit var winMediaPlayer: MediaPlayer
    private lateinit var looseMediaPlayer: MediaPlayer
    private lateinit var wheelSpinMediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Checkout.preload(applicationContext)
        anim = findViewById(R.id.win)
        balance = findViewById(R.id.point)
        val charge = findViewById<TextView>(R.id.chr)
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val navigation = findViewById<NavigationView>(R.id.navigation)
        charge.text = "Per Spin ${Constant.charge}"
        winMediaPlayer = MediaPlayer.create(this,R.raw.gained)
        looseMediaPlayer = MediaPlayer.create(this,R.raw.click)
        wheelSpinMediaPlayer = MediaPlayer.create(this,R.raw.music)
        luckyWheelView = findViewById(R.id.luckyWheel)
        findViewById<View>(R.id.menu).setOnClickListener {
            if(drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }
        var pieColor = 0
        val points = Constant.points.split(",")
        if(points.size > 3) {
            for(i in points.indices) {
                pieColor = if(pieColor == 0) {
                    data.add(LuckyItem("₹ " + points[i], R.drawable.coins,Color.parseColor("#1a579a")))
                    1
                } else {
                    data.add(LuckyItem("₹ " + points[i], R.drawable.coins,Color.parseColor("#000000")))
                    0
                }
            }
            luckyWheelView.setData(data)
            luckyWheelView.setRound(9)
        }
        btnPlay = findViewById(R.id.button)
        btnPlay.setOnClickListener {
            if (Constant.user.point.toInt() >= 10) {
                playSound(wheelSpinMediaPlayer)
                btnPlay.isClickable = false
                val indexPosition = Random().nextInt(data.size)
                luckyWheelView.startLuckyWheelWithTargetIndex(indexPosition)
            } else {
                CustomToast(this, "Not Enough Deposit Money").show()
            }
        }
        luckyWheelView.setLuckyRoundItemSelectedListener(object: LuckyWheelView.LuckyRoundItemSelectedListener{
            override fun LuckyRoundItemSelected(index: Int) {
                val position = if (index != 0) {
                    index - 1
                } else {
                    0
                }
                val pointAdd = data[position].text.replace("₹ ", "").toInt()
                if (pointAdd != 0) {
                    anim.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            anim.visibility = View.GONE
                        }
                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                    anim.visibility = View.VISIBLE
                    anim.playAnimation()
                    val dm = Constant.user.point.toInt() - Constant.charge.toInt()
                    val wm = Constant.user.redeem.toInt() + pointAdd
                    updateUsers(dm.toString(),wm.toString(),"Ready for Next Spin!")
                    ResultSheet().show(supportFragmentManager,"win")
                    playSound(winMediaPlayer)
                } else {
                    val dm = Constant.user.point.toInt() - Constant.charge.toInt()
                    val wm = Constant.user.redeem.toInt()
                    updateUsers(dm.toString(),wm.toString(),"Ready for Next Spin!")
                    ResultSheet().show(supportFragmentManager,"loose")
                    playSound(looseMediaPlayer)
                }
            }
        })
        navigation.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            drawer.closeDrawer(GravityCompat.START)
            when (id) {
                R.id.rate -> {
                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
                R.id.contact -> {
                    try {
                        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND)
                            .setDataAndType(Uri.parse("mailto:"),"text/plain")
                            .putExtra(Intent.EXTRA_EMAIL,arrayOf(getString(R.string.mail_id)))
                            .putExtra(Intent.EXTRA_CC,arrayOf(getString(R.string.mail_id)))
                            .putExtra(Intent.EXTRA_SUBJECT,"Your subject")
                            .putExtra(Intent.EXTRA_TEXT,"Email message goes here"),"Send mail..."))
                    } catch (ex: ActivityNotFoundException) {
                        CustomToast(this@MainActivity,ex.message).show()
                    }
                }
                R.id.policy -> {
                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("${API().getHome()}policy.php")))
                }
                R.id.refund -> {
                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("${API().getHome()}refund.php")))
                }
                R.id.disclaimer -> {
                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("${API().getHome()}disclaimer.php")))
                }
                R.id.share -> {
                    val text = getString(R.string.shareTxt).replace("package",packageName).replace("refercode",Constant.user.referCode)
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND)
                        .setType("text/plain").putExtra(Intent.EXTRA_TEXT,text),"Share By"))
                }
                R.id.exit -> {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setBackground(AppCompatResources.getDrawable(this@MainActivity,R.drawable.bg))
                        .setMessage("Are you sure you want to exit?").setCancelable(false)
                        .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            finish()
                        }.setNegativeButton("No") { d: DialogInterface, _: Int -> d.dismiss() }
                        .show()
                }
            }
            true
        }
        navigation.getHeaderView(0).setOnClickListener {
            developer += 1
            if(developer == 1) {
                Handler(Looper.myLooper()!!).postDelayed({
                    developer = 0
                },3000)
            } else {
                if(developer > 9) {
                    startActivity(Intent(this,HomeActivity::class.java))
                }
            }
        }
        findViewById<LinearLayout>(R.id.wallet).setOnClickListener {
            WalletSheet(this,object: WalletSheet.OnActionSelectListener {
                override fun onSelect(type: Int) {
                    if(type == 0) {
                        DepositSheet(this@MainActivity,object: DepositSheet.OnContinueListener {
                            override fun onContinue(amt: String, gateway: Int) {
                                isPaymentProcessed = false
                                orderId = "odr_${Random().nextInt(999999)}"
                                addedAmount = amt.toInt()
                                when (gateway) {
                                    DepositSheet.RAZORPAY -> {
                                        razorpayPaymentFlow()
                                    }
                                    DepositSheet.PAYTM -> {
                                        paytmPaymentFlow()
                                    }
                                    else -> {
                                        cashFreePaymentFlow()
                                    }
                                }
                            }
                        }).show(supportFragmentManager,"Deposit")
                    } else {
                        WithdrawSheet(this@MainActivity,object:WithdrawSheet.OnRequestCompleteListener {
                            override fun onComplete() {
                                balance.text = (Constant.user.point.toInt() +  Constant.user.redeem.toInt()).toString()
                                CustomToast(this@MainActivity,"Request successful!",CustomToast.GREEN).show()
                            }
                        }).show(supportFragmentManager,"Withdraw")
                    }
                }
            }).show(supportFragmentManager,"My Wallet")
        }
    }

    override fun onResume() {
        balance.text = (Constant.user.point.toInt() +  Constant.user.redeem.toInt()).toString()
        super.onResume()
    }

    private fun playSound(mp: MediaPlayer) {
        try {
            if (mp.isPlaying) {
                mp.pause()
                mp.seekTo(0)
            }
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUsers(de: String,wi: String,msg: String) {
        Volley.newRequestQueue(this).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    Constant.user = User(Constant.user.id,Constant.user.name,Constant.user.device,
                        de,wi,Constant.user.referBy, Constant.user.referCode,Constant.user.status)
                    CustomToast(this,msg,CustomToast.GREEN).show()
                    balance.text = (Constant.user.point.toInt() +  Constant.user.redeem.toInt()).toString()
                } else {
                    CustomToast(this,"Error!",CustomToast.RED).show()
                }
                btnPlay.isClickable = true
            } catch (e: JSONException) {
                btnPlay.isClickable = true
                CustomToast(this,e.message,CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            btnPlay.isClickable = true
            CustomToast(this,error.message,CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "updateusr"
                params["id"] = Constant.user.id
                params["depo"] = de
                params["with"] = wi
                params["status"] = Constant.user.status
                return params
            }
        })
    }

    private fun addTransaction() {
        val ord = "odr_${Random().nextInt(999999)}"
        val tra = "txn_${Random().nextInt(999999)}"
        Volley.newRequestQueue(this).add(object: StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    CustomToast(this,"Completed!",CustomToast.RED).show()
                } else {
                    CustomToast(this,"Error!",CustomToast.RED).show()
                }
            } catch (e: JSONException) {
                CustomToast(this,e.message,CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            CustomToast(this,error.message,CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "addtxn"
                params["amo"] = addedAmount.toString()
                params["typ"] = "0"
                params["dev"] = Constant.user.device
                params["des"] = "Add Money"
                params["ord"] = ord
                params["tra"] = tra
                params["sta"] = "1"
                return params
            }
        })
    }

    private fun razorpayPaymentFlow() {
        val co = Checkout()
        co.setKeyID(Constant.razorpayID)
        co.setImage(R.mipmap.logo)
        try {
            val options = JSONObject()
            options.put("name",getString(R.string.app_name))
            options.put("description", "Add Money")
            options.put("send_sms_hash", true)
            options.put("allow_rotation", true)
            options.put("currency", "INR")
            options.put("amount", addedAmount * 100)
            val preFill = JSONObject()
            preFill.put("email","")
            preFill.put("contact", "8906368080")
            options.put("prefill", preFill)
            co.open(this, options)
        } catch (e: Exception) {
            CustomToast(this,e.message).show()
        }
    }

    private fun cashFreePaymentFlow() {
        Volley.newRequestQueue(this).add(JsonObjectRequest(API().getCashFree(addedAmount.toString(),orderId), { jsonObject: JSONObject ->
                try {
                    val token = jsonObject.getString("cftoken")
                    val cfPaymentService = CFPaymentService.getCFPaymentServiceInstance()
                    cfPaymentService.setOrientation(0)
                    cfPaymentService.upiPayment(this,myParams(),token,"PROD")
                } catch (e: JSONException) {
                    CustomToast(this,e.message,CustomToast.RED).show()
                }
            }
        ) { error: VolleyError ->
            CustomToast(this,error.message,CustomToast.RED).show()
        })
    }

    private fun myParams(): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params[CFPaymentService.PARAM_APP_ID] = Constant.cashfreeID
        params[CFPaymentService.PARAM_ORDER_ID] = orderId
        params[CFPaymentService.PARAM_ORDER_AMOUNT] = addedAmount.toString()
        params[CFPaymentService.PARAM_ORDER_NOTE] = "Add Money"
        params[CFPaymentService.PARAM_CUSTOMER_NAME] = Constant.user.name
        params[CFPaymentService.PARAM_CUSTOMER_PHONE] = "8906368080"
        params[CFPaymentService.PARAM_CUSTOMER_EMAIL] = ""
        params[CFPaymentService.PARAM_ORDER_CURRENCY] = "INR"
        return params
    }

    private fun paytmPaymentFlow() {
        Volley.newRequestQueue(this).add(JsonObjectRequest(API().getPaytm(addedAmount.toString(),orderId), { response: JSONObject ->
            try {
                val obj = response.getJSONObject("body")
                val token = obj.getString("txnToken")
                initiatePayment(token)
            } catch (e: JSONException) {
                CustomToast(this,e.message,CustomToast.RED).show()
            }
        }) { error: VolleyError ->
            CustomToast(this,error.message,CustomToast.RED).show()
        })
    }

    private fun initiatePayment(token: String) {
        val callBackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=$orderId"
        val paytmOrder = PaytmOrder(orderId,Constant.paytmMID,token,addedAmount.toString(),callBackUrl)
        val transactionManager = TransactionManager(paytmOrder,object : PaytmPaymentTransactionCallback {
            override fun onTransactionResponse(bundle: Bundle?) {
                if (bundle!!.getString(PaytmConstants.STATUS) == "TXN_SUCCESS") {
                    if(!isPaymentProcessed) {
                        val dm = Constant.user.point.toInt() + addedAmount
                        updateUsers(dm.toString(),Constant.user.redeem,"Payment success!")
                        isPaymentProcessed = true
                        addTransaction()
                    }
                } else {
                    CustomToast(this@MainActivity,"Payment failed!",CustomToast.RED).show()
                }
            }
            override fun networkNotAvailable() {
                CustomToast(this@MainActivity,"Network not available!",CustomToast.RED).show()
            }
            override fun onErrorProceed(s: String) {
                CustomToast(this@MainActivity,s,CustomToast.RED).show()
            }
            override fun clientAuthenticationFailed(s: String) {
                CustomToast(this@MainActivity,s,CustomToast.RED).show()
            }
            override fun someUIErrorOccurred(s: String) {
                CustomToast(this@MainActivity,s,CustomToast.RED).show()
            }
            override fun onErrorLoadingWebPage(i: Int, s: String, s1: String) {
                CustomToast(this@MainActivity,s,CustomToast.RED).show()
            }
            override fun onBackPressedCancelTransaction() {
                CustomToast(this@MainActivity,"Payment canceled!",CustomToast.RED).show()
            }
            override fun onTransactionCancel(s: String, bundle: Bundle) {
                CustomToast(this@MainActivity,s,CustomToast.RED).show()
            }
        })
        transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage")
        transactionManager.startTransaction(this,PAYMENT_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == PAYMENT_CODE) {
                try {
                    val bundle = data.getStringExtra("response")?.let { JSONObject(it) }
                    if (bundle!!.getString(PaytmConstants.STATUS) == "TXN_SUCCESS") {
                        if (!isPaymentProcessed) {
                            val dm = Constant.user.point.toInt() + addedAmount
                            updateUsers(dm.toString(),Constant.user.redeem,"Payment success!")
                            isPaymentProcessed = true
                            addTransaction()
                        }
                    } else {
                        CustomToast(this,"Payment failed!",CustomToast.RED).show()
                    }
                } catch (e: JSONException) {
                    CustomToast(this,e.message,CustomToast.RED).show()
                }
            } else {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getString("txStatus") != null) {
                        if(bundle.getString("txStatus").equals("SUCCESS", ignoreCase = true)) {
                            val dm = Constant.user.point.toInt() + addedAmount
                            updateUsers(dm.toString(),Constant.user.redeem,"Payment success!")
                            addTransaction()
                        } else {
                            CustomToast(this,bundle.getString("txStatus"),CustomToast.RED).show()
                        }
                    } else {
                        CustomToast(this,"Error Response",CustomToast.RED).show()
                    }
                }
            }
        }
    }

    override fun onPaymentSuccess(s: String?) {
        val dm = Constant.user.point.toInt() + addedAmount
        updateUsers(dm.toString(),Constant.user.redeem,"Payment success!")
        addTransaction()
    }

    override fun onPaymentError(i: Int, s: String?) {
        CustomToast(this,"Payment canceled!",CustomToast.RED).show()
    }

}