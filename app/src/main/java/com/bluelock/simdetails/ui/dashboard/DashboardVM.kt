package com.bluelock.simdetails.ui.dashboard


import androidx.lifecycle.ViewModel
import com.bluelock.simdetails.data.model.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardVM @Inject constructor() : ViewModel() {
    val data: MutableList<Title> =
        mutableListOf(
            Title("Sim Details"),
            Title("Location Information"),
            Title("Cnic Information"),
            Title("Disclaimer"),
            Title("Contact Us"),
        )


}