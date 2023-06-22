package com.bluelock.simdetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelock.simdetails.R
import com.example.ads.GoogleManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var googleManager: GoogleManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
    }
}


