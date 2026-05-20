package com.jokerdev.katai.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jokerdev.katai.data.model.ChatMessage
import com.jokerdev.katai.data.model.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
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
            delay(1500)

            val botMessage = ChatMessage(
                id = System.currentTimeMillis().toInt(),
                text = "This is a fake AI response for: $messageText",
                isUser = false
            )

            val finalMessages =
                _uiState.value.messages + botMessage

            _uiState.value =
                _uiState.value.copy(
                    messages = finalMessages,
                    isLoading = false
                )
        }
    }

    fun onPdfSelected(
        pdfName: String,
        pdfUri: Uri
    ) {

        _uiState.value =
            _uiState.value.copy(
                selectedPdfName = pdfName,
                selectedPdfUri = pdfUri
            )
    }

}