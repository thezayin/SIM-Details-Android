package com.example.simdetails.ui.dashboard


import androidx.lifecycle.ViewModel
import com.example.simdetails.data.model.Title

class DashboardVM : ViewModel() {
    val data: MutableList<Title> =
        mutableListOf(
            Title("Sim Details"),
            Title("Location Information"),
            Title("Cnic Information"),
            Title("Disclaimer"),
            Title("Contact Us"),
        )


}