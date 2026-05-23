package com.jokerdev.katai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jokerdev.katai.ui.components.ChatTopBar
import com.jokerdev.katai.ui.screens.ChatScreen
import com.jokerdev.katai.ui.theme.KataiTheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        PDFBoxResourceLoader.init(applicationContext)

        setContent {
            KataiTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ChatScreen(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainPreview() {
    KataiTheme {
        MainScreen()
    }
}