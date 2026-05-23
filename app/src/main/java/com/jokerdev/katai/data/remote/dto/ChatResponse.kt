package com.jokerdev.katai.data.remote.dto

data class GeminiResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageResponse
)

data class MessageResponse(
    val content: String?
)