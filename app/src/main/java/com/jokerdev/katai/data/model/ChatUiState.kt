package com.jokerdev.katai.data.model

import android.net.Uri

data class ChatUiState(

    val messages: List<ChatMessage> = emptyList(),

    val currentMessage: String = "",

    val isLoading: Boolean = false,

    val selectedPdfName: String? = null,

    val selectedPdfUri: Uri? = null,

    val extractedPdfText: String = ""
)