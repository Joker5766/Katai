package com.jokerdev.katai.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jokerdev.katai.data.local.ChatStorage
import com.jokerdev.katai.data.local.SettingsStorage
import com.jokerdev.katai.data.model.ChatMessage
import com.jokerdev.katai.data.model.ChatSession
import com.jokerdev.katai.data.model.ChatUiState
import com.jokerdev.katai.data.remote.ChatRepository
import com.jokerdev.katai.data.repository.PdfRepository
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatRepository = ChatRepository()
    private val pdfRepository = PdfRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadSavedSessions()
    }

    fun renameSession(
        sessionId: String,
        newTitle: String
    ) {

        if (newTitle.isBlank()) return


        val updatedSessions = _uiState.value.sessions.map {
            if (it.id == sessionId) {
                it.copy(title = newTitle.trim())
            } else it
        }

        _uiState.value = _uiState.value.copy(
            sessions = updatedSessions
        )

        syncCurrentSessionState()
        saveSessions()
    }
    private fun loadSavedSessions() {
        viewModelScope.launch {

            val savedSessions = ChatStorage.loadChats(getApplication())

            val temporarySession = ChatSession(
                id = UUID.randomUUID().toString(),
                title = "New Chat"
            )

            _uiState.value = _uiState.value.copy(
                sessions = savedSessions + temporarySession,
                currentSessionId = temporarySession.id
            )

            syncCurrentSessionState()
        }
    }

    private fun saveSessions() {
        val currentSessions = _uiState.value.sessions
        ChatStorage.saveChats(getApplication(), currentSessions)
    }

    fun createNewSession(title: String = "New Chat") {
        val newSessionId = UUID.randomUUID().toString()
        val newSession = ChatSession(
            id = newSessionId,
            title = title
        )
        val updatedSessions = _uiState.value.sessions + newSession
        _uiState.value = _uiState.value.copy(
            sessions = updatedSessions,
            currentSessionId = newSessionId
        )
        syncCurrentSessionState()
    }

    fun selectSession(sessionId: String) {
        if (sessionId == _uiState.value.currentSessionId) return
        
        _uiState.value = _uiState.value.copy(
            currentSessionId = sessionId,
            currentMessage = ""
        )
        syncCurrentSessionState()
    }

    fun deleteSession(sessionId: String) {
        val remainingSessions = _uiState.value.sessions.filter { it.id != sessionId }
        val activeSessionId = if (sessionId == _uiState.value.currentSessionId) {
            if (remainingSessions.isNotEmpty()) remainingSessions.first().id else ""
        } else {
            _uiState.value.currentSessionId
        }

        _uiState.value = _uiState.value.copy(
            sessions = remainingSessions,
            currentSessionId = activeSessionId
        )

        if (remainingSessions.isEmpty()) {
            createNewSession()
        } else {
            syncCurrentSessionState()
            saveSessions()
        }
    }

    fun clearCurrentSession() {
        val activeId = _uiState.value.currentSessionId
        val updatedSessions = _uiState.value.sessions.map {
            if (it.id == activeId) {
                it.copy(
                    title = "New Chat",
                    messages = emptyList(),
                    selectedPdfName = null,
                    selectedPdfUriString = null,
                    extractedPdfText = ""
                )
            } else it
        }

        _uiState.value = _uiState.value.copy(
            sessions = updatedSessions
        )
        syncCurrentSessionState()
        saveSessions()
    }

    private fun syncCurrentSessionState() {
        val activeSession = _uiState.value.sessions.find { it.id == _uiState.value.currentSessionId }
        activeSession?.let {
            val pdfUri = it.selectedPdfUriString?.let { uriStr -> Uri.parse(uriStr) }
            _uiState.value = _uiState.value.copy(
                messages = it.messages,
                selectedPdfName = it.selectedPdfName,
                selectedPdfUri = pdfUri,
                extractedPdfText = it.extractedPdfText
            )
        }
    }

    fun onMessageChange(newMessage: String) {
        _uiState.value = _uiState.value.copy(
            currentMessage = newMessage
        )
    }

    fun sendMessage() {
        if (_uiState.value.isLoading) return
        val messageText = _uiState.value.currentMessage.trim()
        if (messageText.isEmpty()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toInt(),
            text = messageText,
            isUser = true
        )

        val activeId = _uiState.value.currentSessionId
        val currentSession =
            _uiState.value.sessions.find { it.id == activeId }

        val shouldPersistSession =
            currentSession?.messages?.isEmpty() == true

        val updatedSessions = _uiState.value.sessions.map {
            if (it.id == activeId) {
                val newTitle = if (it.title == "New Chat" && it.messages.isEmpty()) {
                    if (messageText.length > 22) messageText.take(20) + "..." else messageText
                } else {
                    it.title
                }
                it.copy(
                    title = newTitle,
                    messages = it.messages + userMessage
                )
            } else it
        }

        _uiState.value = _uiState.value.copy(
            sessions = updatedSessions,
            currentMessage = "",
            isLoading = true
        )
        syncCurrentSessionState()
        saveSessions()

        viewModelScope.launch {
            try {
                val responseLanguage = SettingsStorage.getResponseLanguage(getApplication())
                val isTypingAnimationEnabled = SettingsStorage.getTypingAnimation(getApplication())

                if (!com.jokerdev.katai.utils.NetworkUtils.isNetworkAvailable(getApplication())) {
                    throw java.io.IOException("No internet connection. Please check your network and try again.")
                }

                val aiResponse = chatRepository.generateResponse(
                    question = messageText,
                    pdfText = _uiState.value.extractedPdfText,
                    history = _uiState.value.messages,
                    language = responseLanguage
                )

                val botMessageId = System.currentTimeMillis().toInt()
                val initialBotMessage = ChatMessage(
                    id = botMessageId,
                    text = if (isTypingAnimationEnabled) "" else aiResponse,
                    isUser = false
                )

                val withBotMessageSessions = _uiState.value.sessions.map {
                    if (it.id == activeId) {
                        it.copy(messages = it.messages + initialBotMessage)
                    } else it
                }

                _uiState.value = _uiState.value.copy(
                    sessions = withBotMessageSessions,
                    isLoading = false
                )
                syncCurrentSessionState()

                if (isTypingAnimationEnabled) {
                    val delayTime = when {
                        aiResponse.length > 1000 -> 2L
                        aiResponse.length > 500 -> 6L
                        else -> 12L
                    }

                    var typedText = ""
                    for (char in aiResponse) {
                        typedText += char
                        
                        val typingSessions = _uiState.value.sessions.map { session ->
                            if (session.id == activeId) {
                                session.copy(
                                    messages = session.messages.map { msg ->
                                        if (msg.id == botMessageId) msg.copy(text = typedText) else msg
                                    }
                                )
                            } else session
                        }

                        _uiState.value = _uiState.value.copy(
                            sessions = typingSessions
                        )
                        syncCurrentSessionState()
                        kotlinx.coroutines.delay(delayTime)
                    }
                }

                saveSessions()

            } catch (e: Exception) {
                val userFriendlyError = when {
                    e is java.net.UnknownHostException || e.message?.contains("No internet connection") == true || e is java.io.IOException ->
                        "No internet connection. Please check your network and try again."
                    e.message?.contains("401") == true ->
                        "Unauthorized: Invalid Groq API key configuration. Please contact the developer."
                    e.message?.contains("429") == true ->
                        "Rate limit exceeded. Please wait a moment and try again."
                    e.message?.contains("503") == true ->
                        "Groq API service is currently unavailable. Please try again later."
                    else -> e.localizedMessage ?: "Something went wrong"
                }

                val errorMessage = ChatMessage(
                    id = System.currentTimeMillis().toInt(),
                    text = userFriendlyError,
                    isUser = false
                )

                val errorSessions = _uiState.value.sessions.map {
                    if (it.id == activeId) {
                        it.copy(messages = it.messages + errorMessage)
                    } else it
                }

                _uiState.value = _uiState.value.copy(
                    sessions = errorSessions,
                    isLoading = false
                )
                syncCurrentSessionState()
                saveSessions()
            }
        }
    }

    fun onPdfSelected(
        context: Context,
        pdfName: String,
        pdfUri: Uri
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch {
            try {
                val extractedText = pdfRepository.extractTextFromPdf(
                    context = context,
                    pdfUri = pdfUri
                )

                if (extractedText.isBlank()) {
                    throw Exception("The PDF contains no readable text. It might be scanned, encrypted, or empty.")
                }

                val activeId = _uiState.value.currentSessionId
                val updatedSessions = _uiState.value.sessions.map {
                    if (it.id == activeId) {
                        val newTitle = if (it.title == "New Chat") {
                            val cleanedName = pdfName.replace(".pdf", "", ignoreCase = true)
                            if (cleanedName.length > 22) cleanedName.take(20) + "..." else cleanedName
                        } else {
                            it.title
                        }
                        it.copy(
                            title = newTitle,
                            selectedPdfName = pdfName,
                            selectedPdfUriString = pdfUri.toString(),
                            extractedPdfText = extractedText
                        )
                    } else it
                }

                _uiState.value = _uiState.value.copy(
                    sessions = updatedSessions,
                    isLoading = false
                )
                syncCurrentSessionState()
                saveSessions()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF successfully attached!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
                syncCurrentSessionState()
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Failed to load PDF: ${e.localizedMessage ?: "Unsupported file format"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}