package com.ariqhisyamsyahputra0025.mobpro1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.MainScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.theme.Mobpro1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobpro1Theme {
                MainScreen()
            }
        }
    }
}