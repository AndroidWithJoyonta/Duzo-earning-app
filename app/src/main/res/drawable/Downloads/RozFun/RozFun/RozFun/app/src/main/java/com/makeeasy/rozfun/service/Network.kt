package com.makeeasy.rozfun.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class Network(private val ctx: Context) {

    fun isOnline(): Boolean? {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}