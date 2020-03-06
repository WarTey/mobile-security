package fr.isen.guillaume.mobilesecurity.tools

import android.content.Context
import android.net.ConnectivityManager

class Connectivity  {

    fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }
}