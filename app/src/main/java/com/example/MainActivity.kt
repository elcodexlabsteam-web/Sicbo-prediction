package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.CasinoBg
import com.example.ui.screens.SicBoPredictionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SicBoViewModel
import com.example.ui.viewmodel.SicBoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable EdgeToEdge full transparent navigation
        enableEdgeToEdge()

        val app = application as SicBoApplication
        val viewModel = ViewModelProvider(
            this,
            SicBoViewModelFactory(app.repository)
        )[SicBoViewModel::class.java]

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CasinoBg
                ) { innerPadding ->
                    SicBoPredictionScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
