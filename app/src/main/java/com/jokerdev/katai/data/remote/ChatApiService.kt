package com.jokerdev.katai.data.remote

import com.jokerdev.katai.data.remote.dto.GeminiRequest
import com.jokerdev.katai.data.remote.dto.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ChatApiService {

    @POST("v1/chat/completions")
    suspend fun generateContent(
        @Header("Authorization") authorization: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}