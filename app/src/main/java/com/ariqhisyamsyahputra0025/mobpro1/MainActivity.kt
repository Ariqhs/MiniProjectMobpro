package com.ariqhisyamsyahputra0025.mobpro1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ariqhisyamsyahputra0025.mobpro1.navigation.SetupNavGraph
import com.ariqhisyamsyahputra0025.mobpro1.ui.theme.Mobpro1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val pref = remember { SettingPreferences(context) }
            val isDarkMode by pref.getThemeSetting.collectAsState(initial = false)

            Mobpro1Theme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                SetupNavGraph()
            }
        }
    }
}