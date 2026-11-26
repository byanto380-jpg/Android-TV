package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.data.model.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkStatusMonitor(private val context: Context) {

    val statusFlow: Flow<NetworkStatus> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun readCurrent(): NetworkStatus {
            val active = cm.activeNetwork
            val caps = active?.let { cm.getNetworkCapabilities(it) }
            if (caps == null || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                return NetworkStatus(isConnected = false, connectionLabel = "Offline")
            }
            val label = when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "LAN / Ethernet"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Aktif"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Seluler"
                else -> "Terhubung"
            }
            return NetworkStatus(isConnected = true, connectionLabel = label)
        }

        trySend(readCurrent())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(readCurrent())
            }

            override fun onLost(network: Network) {
                trySend(readCurrent())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(readCurrent())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        runCatching { cm.registerNetworkCallback(request, callback) }
        awaitClose { runCatching { cm.unregisterNetworkCallback(callback) } }
    }
}
