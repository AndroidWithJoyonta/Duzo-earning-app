package com.makeeasy.rozfun.service

import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class API {

    private val encodedBase = "aHR0cHM6Ly9kYWlseXNoYXlhcmkuZnVuL3NwaW4vYWRtaW4v"
    private val base: String = String(Base64.decode(encodedBase,Base64.DEFAULT))

    fun getBase(): String {
        return base + "api.php"
    }

    fun getPaytm(amt:String,odr:String): String {
        val cast = "c0${Constant.user.id}"
        return base + "init_Transaction.php?code=12345&ORDER_ID=${odr}&AMOUNT=${amt}&CUST=${cast}"
    }

    fun getCashFree(amt:String,odr:String): String {
        return base + "token.php?amt=$amt&odr=$odr"
    }

    fun getHome(): String {
        return base
    }

    fun getChart(): String {
        return "https://quickchart.io/chart?c={type:'bar',data:{labels:[2020,2021,2022,2023,2024],datasets:[{label:'Performance',data:[120,60,50,180,120]}]}}"
    }
}