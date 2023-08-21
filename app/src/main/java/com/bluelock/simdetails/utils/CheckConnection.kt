package com.bluelock.simdetails.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.fragment.app.Fragment
import com.bluelock.simdetails.R
import com.bluelock.simdetails.databinding.DialogNetworkAlertBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.showBottomSheetDialog() {
    BottomSheetDialog(requireContext(), R.style.SheetDialog).also { dialog ->
        val binding = DialogNetworkAlertBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.behavior.isDraggable = false
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            tvTitle.text = "No internet"
            tvMsg.text = "Check your internet connection and try again"
            btnClose.setOnClickListener {
                dialog.dismiss()
//                action.invoke()
            }
        }
        dialog.show()
    }
}

fun checkForInternet(context: Context): Boolean {
    // register activity with the connectivity manager service
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    // if the android version is equal to M
    // or greater we need to use the
    // NetworkCapabilities to check what type of
    // network has the internet connection
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false
        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    } else {
        // if the android version is below M
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}
