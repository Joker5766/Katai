package com.jokerdev.katai.viewmodel

import androidx.lifecycle.ViewModel
import com.jokerdev.katai.data.model.ChatMessage
import com.jokerdev.katai.data.model.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
                currentMessage = ""
            )
    }

}