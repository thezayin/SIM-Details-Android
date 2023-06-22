package com.bluelock.simdetails.ui.selectserver

import androidx.lifecycle.ViewModel
import com.bluelock.simdetails.data.model.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor() : ViewModel() {
    val data: MutableList<Title> =
        mutableListOf(
            Title("Server 1"),
            Title("Server 2"),
            Title("Server 3"),
            Title("Server 4"),
        )
}