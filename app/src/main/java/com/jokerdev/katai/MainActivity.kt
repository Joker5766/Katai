package com.jokerdev.katai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jokerdev.katai.ui.screens.ChatScreen
import com.jokerdev.katai.ui.screens.SettingsScreen
import com.jokerdev.katai.ui.theme.KataiTheme
import com.jokerdev.katai.viewmodel.SettingsViewModel
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        PDFBoxResourceLoader.init(applicationContext)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()

            val isDarkTheme = when (settingsState.darkThemeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            KataiTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSettings by rememberSaveable { mutableStateOf(false) }

                    AnimatedContent(
                        targetState = showSettings,
                        transitionSpec = {
                            if (targetState) {
                                (slideInHorizontally { it } + fadeIn()) togetherWith
                                        (slideOutHorizontally { -it } + fadeOut())
                            } else {
                                (slideInHorizontally { -it } + fadeIn()) togetherWith
                                        (slideOutHorizontally { it } + fadeOut())
                            }
                        },
                        label = "screen_transition"
                    ) { isSettings ->
                        if (isSettings) {
                            SettingsScreen(
                                onBack = { showSettings = false }
                            )
                        } else {
                            ChatScreen(
                                modifier = Modifier.fillMaxSize(),
                                onSettingsClick = { showSettings = true }
                            )
                        }
                    }
                }
            }
        }
    }
}