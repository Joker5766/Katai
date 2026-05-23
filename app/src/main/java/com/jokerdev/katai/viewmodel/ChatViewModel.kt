package com.jokerdev.katai.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jokerdev.katai.data.model.ChatMessage
import com.jokerdev.katai.data.model.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.jokerdev.katai.data.remote.ChatRepository
import com.jokerdev.katai.data.repository.PdfRepository
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    private val chatRepository = ChatRepository()
    private val pdfRepository = PdfRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onMessageChange(newMessage: String) {

        _uiState.value =
            _uiState.value.copy(
                currentMessage = newMessage
            )
    }

    fun sendMessage() {

        val messageText =
            _uiState.value.currentMessage.trim()

        if (messageText.isEmpty()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toInt(),
            text = messageText,
            isUser = true
        )

        val updatedMessages =
            _uiState.value.messages + userMessage

        _uiState.value =
            _uiState.value.copy(
                messages = updatedMessages,
                currentMessage = "",
                isLoading = true
            )

        viewModelScope.launch {
            try {

                val aiResponse =
                    chatRepository.generateResponse(
                        question = messageText,
                        pdfText = _uiState.value.extractedPdfText
                    )

                val botMessage = ChatMessage(
                    id = System.currentTimeMillis().toInt(),
                    text = aiResponse,
                    isUser = false
                )

                val finalMessages =
                    _uiState.value.messages + botMessage

                _uiState.value =
                    _uiState.value.copy(
                        messages = finalMessages,
                        isLoading = false
                    )

            } catch (e: Exception) {

                val errorMessage = ChatMessage(
                    id = System.currentTimeMillis().toInt(),
                    text = e.message ?: "Something went wrong",
                    isUser = false
                )

                _uiState.value =
                    _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isLoading = false
                    )
            }
        }
    }

    fun onPdfSelected(
        context: Context,
        pdfName: String,
        pdfUri: Uri
    ) {

        _uiState.value =
            _uiState.value.copy(
                selectedPdfName = pdfName,
                selectedPdfUri = pdfUri,
                isLoading = true
            )

        viewModelScope.launch {
            val extractedText =
                pdfRepository.extractTextFromPdf(
                    context = context,
                    pdfUri = pdfUri
                )
            _uiState.value =
                _uiState.value.copy(
                    extractedPdfText = extractedText,
                    isLoading = false
                )
        }
    }

}