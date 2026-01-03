package com.example.refconnect.model

data class Connection(
    val id: String,
    val requesterId: String,
    val requesterName: String,
    val referralGiverId: String,
    val referralGiverName: String,
    val referralId: String,
    val referralRole: String,
    val status: ConnectionStatus,
    val requestedAt: Long = System.currentTimeMillis()
)

enum class ConnectionStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
