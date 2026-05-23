package com.jokerdev.katai.data.remote

import com.jokerdev.katai.BuildConfig
import com.jokerdev.katai.data.remote.dto.GeminiRequest
import com.jokerdev.katai.data.remote.dto.Message

class ChatRepository {
    private val api = RetrofitInstance.api


    suspend fun generateResponse(
        question: String,
        pdfText: String
    ): String {
        val finalPrompt = if (pdfText.trim().isEmpty()) {
            question
        } else {
            """
            You are Katai, a helpful and professional AI PDF Assistant.
            
            Guidelines:
            1. If the user's message is a greeting, courtesy, general chitchat, or appreciation (e.g., "hello", "hi", "thank you", "thanks", "who are you"), reply naturally, politely, and briefly.
            2. For questions seeking information, answer using the PDF content below. If the answer is not present in the PDF content, reply with: "I could not find that in the PDF."
            
            PDF Content:
            $pdfText
            
            Question:
            $question
            """.trimIndent()
        }

        val request = GeminiRequest(
            model = "llama-3.1-8b-instant",
            messages = listOf(
                Message(
                    role = "user",
                    content = finalPrompt
                )
            )
        )

        val response = api.generateContent(
            authorization = "Bearer ${BuildConfig.GROQ_API_KEY}",
            request = request
        )
        return response.choices
            .first()
            .message
            .content
            ?: "Empty response"
    }
}