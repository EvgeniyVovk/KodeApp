package com.example.kodeapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kodeapp.R
import com.example.kodeapp.appComponent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appComponent.inject(this)
    }
}