package com.bluelock.simdetails.ui


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bluelock.simdetails.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding



    private lateinit var appUpdateManager: AppUpdateManager
    private val updateAvailable = MutableLiveData<Boolean>().apply { value = false }
    private var updateInfo: AppUpdateInfo? = null
    private var updateListener = InstallStateUpdatedListener { state: InstallState ->
        Log.d("jeje_state", "update01:$state")
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdateSnackbar()
        }
    }


    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                updateInfo = it
                updateAvailable.value = true
                Log.d(
                    "jeje_available_version",
                    "update01:Version Code available:${it.availableVersionCode()}"
                )
                startForInAppUpdate(updateInfo)
            } else {
                updateAvailable.value = false
            }
        }
    }

    private fun startForInAppUpdate(it: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(it!!, AppUpdateType.FLEXIBLE, this, 1101)
    }

    private fun showUpdateSnackbar() {
        try {
            val snackbar = Snackbar.make(
                binding.coordinator,
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("RESTART") { appUpdateManager.completeUpdate() }
            //snackbar.anchorView = binding.appBarMain.contentMain.bottomNav
            snackbar.setActionTextColor(Color.parseColor("#ffff4444"))
            snackbar.show()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun commonLog(message: String) {
        Log.d("tag001", message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            //.......................................................................
            appUpdateManager = AppUpdateManagerFactory.create(this)
            appUpdateManager.registerListener(updateListener)
            checkForUpdate()
        } catch (e: Exception) {
            commonLog("update01:Update e1 ${e.message}")
        }
    }


    override fun onBackPressed() {
        try {
            appUpdateManager.unregisterListener(updateListener)
        } catch (e: Exception) {
            commonLog("update01:Update e2 ${e.message}")
        }
    }


}


