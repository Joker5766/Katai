package com.jokerdev.katai.data.remote

import com.jokerdev.katai.BuildConfig
import com.jokerdev.katai.data.model.ChatMessage
import com.jokerdev.katai.data.remote.dto.GeminiRequest
import com.jokerdev.katai.data.remote.dto.Message
import com.jokerdev.katai.utils.ChunkRetriever
import com.jokerdev.katai.utils.PdfChunker

class ChatRepository {
    private val api = RetrofitInstance.api

    suspend fun generateResponse(
        question: String,
        pdfText: String,
        history: List<ChatMessage>,
        language: String
    ): String {
        val systemInstruction = """
            You are Katai, a helpful and professional AI PDF Assistant.
            
            Guidelines:
            1. If the user's message is a greeting, courtesy, general chitchat, or appreciation (e.g., "hello", "hi", "thank you", "thanks", "who are you"), reply naturally, politely, and briefly.
            2. For questions seeking information, answer using the PDF content below. If the answer is not present in the PDF content, reply with: "I could not find that in the PDF."
            3. You MUST respond in $language.
        """.trimIndent()

        val messagesList = mutableListOf<Message>()

        messagesList.add(Message(role = "system", content = systemInstruction))

        val pastMessages = if (history.isNotEmpty() && history.last().text.trim() == question.trim()) {
            history.dropLast(1).takeLast(6)
        } else {
            history.takeLast(6)
        }

        for (msg in pastMessages) {
            messagesList.add(
                Message(
                    role = if (msg.isUser) "user" else "assistant",
                    content = msg.text
                )
            )
        }

        val relevantPdfContext = if (pdfText.trim().isNotEmpty()) {
            val chunks = PdfChunker.chunkText(pdfText)
            val relevantChunks = ChunkRetriever.getRelevantChunks(question, chunks, topK = 3)
            relevantChunks.joinToString("\n\n---\n\n")
        } else {
            ""
        }

        val finalPrompt = if (relevantPdfContext.isEmpty()) {
            question
        } else {
            """
            Relevant PDF Content:
            $relevantPdfContext
            
            Question:
            $question
            """.trimIndent()
        }

        messagesList.add(Message(role = "user", content = finalPrompt))

        val request = GeminiRequest(
            model = "llama-3.1-8b-instant",
            messages = messagesList
        )

        val response = api.generateContent(
            authorization = "Bearer ${BuildConfig.GROQ_API_KEY}",
            request = request
        )
        return response.choices
            .first()
            .message
            .content
            ?.trim()
            ?: "Empty response"
    }
}