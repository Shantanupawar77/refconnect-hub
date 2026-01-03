package com.example.refconnect.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.model.Chat
import com.example.refconnect.model.Message
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    val currentUserId: StateFlow<String?> = repository.currentUserId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val chats: StateFlow<List<Chat>> = repository.getMyChats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadMessagesCount: StateFlow<Int> = repository.getTotalUnreadMessagesCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    var currentChat by mutableStateOf<Chat?>(null)
        private set

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    var messageInput by mutableStateOf("")

    var error by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSending by mutableStateOf(false)

    fun loadChat(chatId: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                currentChat = repository.getChatById(chatId)
                loadMessages(chatId)
                // Mark chat as read when opened
                repository.markChatAsRead(chatId)
            } catch (e: Exception) {
                error = "Failed to load chat: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                messages = repository.getMessagesForChat(chatId).first()
            } catch (e: Exception) {
                error = "Failed to load messages: ${e.message}"
            }
        }
    }

    fun canSendMessage(): Boolean {
        return messageInput.trim().isNotEmpty() && !isSending
    }

    fun sendMessage() {
        error = ""
        if (!canSendMessage()) {
            error = "Message cannot be empty"
            return
        }

        val chatId = currentChat?.id ?: return
        val content = messageInput.trim()
        messageInput = ""

        isSending = true
        viewModelScope.launch {
            try {
                repository.sendMessage(chatId, content)
                loadMessages(chatId)
            } catch (e: Exception) {
                error = "Failed to send message: ${e.message}"
                messageInput = content // Restore message on error
            } finally {
                isSending = false
            }
        }
    }

    fun markChatAsRead(chatId: String) {
        viewModelScope.launch {
            try {
                repository.markChatAsRead(chatId)
            } catch (e: Exception) {
                // Handle error silently for mark as read
            }
        }
    }

    suspend fun getChatByUsers(userId1: String, userId2: String): Chat? {
        return repository.getChatByUsers(userId1, userId2)
    }

    fun reset() {
        currentChat = null
        messages = emptyList()
        messageInput = ""
        error = ""
    }
}