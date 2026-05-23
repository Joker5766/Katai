package com.jokerdev.katai.data.model

data class ChatSession(
    val id: String,
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val selectedPdfName: String? = null,
    val selectedPdfUriString: String? = null,
    val extractedPdfText: String = ""
)
