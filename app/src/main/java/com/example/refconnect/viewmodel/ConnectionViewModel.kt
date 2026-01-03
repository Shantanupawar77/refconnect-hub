package com.example.refconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.model.Connection
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    val currentUserId: StateFlow<String?> = repository.currentUserId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val myConnections: StateFlow<List<Connection>> = repository.getMyConnections()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingRequests: StateFlow<List<Connection>> = repository.getPendingConnectionRequests()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadCount: StateFlow<Int> = repository.getUnreadConnectionsCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _connectionAcceptedEvent = MutableStateFlow<String?>(null)
    val connectionAcceptedEvent: StateFlow<String?> = _connectionAcceptedEvent.asStateFlow()

    var requestSubmitted = false
        private set

    fun requestConnection(referralId: String) {
        viewModelScope.launch {
            try {
                val connection = repository.requestConnection(referralId)
                if (connection != null) {
                    requestSubmitted = true
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun acceptConnection(connectionId: String) {
        viewModelScope.launch {
            try {
                repository.acceptConnection(connectionId)
                _connectionAcceptedEvent.value = "Connection accepted"
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearConnectionAcceptedEvent() {
        _connectionAcceptedEvent.value = null
    }

    fun rejectConnection(connectionId: String) {
        viewModelScope.launch {
            try {
                repository.rejectConnection(connectionId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun markConnectionsAsRead() {
        viewModelScope.launch {
            try {
                repository.markConnectionsAsRead()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun resetRequestState() {
        requestSubmitted = false
    }
}