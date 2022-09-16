package com.example.simdetails.ui.selectserver

import androidx.lifecycle.ViewModel
import com.example.simdetails.data.model.Title

class ServerViewModel : ViewModel() {
    val data: MutableList<Title> =
        mutableListOf(
            Title("Server 1"),
            Title("Server 2"),
            Title("Server 3"),
            Title("Server 4"),
        )
}