package com.jokerdev.katai.data.remote

import android.util.Log
import com.jokerdev.katai.data.remote.dto.GeminiRequest
import com.jokerdev.katai.data.remote.dto.Message
import com.jokerdev.katai.BuildConfig

class ChatRepository {
    private val api = RetrofitInstance.api


    suspend fun generateResponse(
        question: String,
        pdfText: String
    ): String {
        val finalPrompt = """
Answer the question ONLY using the PDF content below.

If the answer is not present in the PDF, say:
"I could not find that in the PDF."

PDF Content:
$pdfText

Question:
$question
""".trimIndent()

        val request = GeminiRequest(
            model = "llama-3.1-8b-instant",
            messages = listOf(
                Message(
                    role = "user",
                    content = finalPrompt
                )
            )
        )
        Log.d("API_KEY", BuildConfig.GROQ_API_KEY)
        println(BuildConfig.GROQ_API_KEY)

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