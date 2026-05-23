package com.jokerdev.katai.data.remote.dto

data class GeminiRequest(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)