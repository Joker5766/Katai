package com.jokerdev.katai.data.model

data class ChatMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean
)