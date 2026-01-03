package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.model.Connection
import com.example.refconnect.model.ConnectionStatus
import com.example.refconnect.viewmodel.ConnectionViewModel
import kotlinx.coroutines.launch

@Composable
fun ConnectionsScreen(
    connectionViewModel: ConnectionViewModel = viewModel(),
    chatViewModel: com.example.refconnect.viewmodel.ChatViewModel = viewModel(),
    onNavigateToChat: (String) -> Unit = {}
) {
    val myConnections by connectionViewModel.myConnections.collectAsState()
    val pendingRequests by connectionViewModel.pendingRequests.collectAsState()
    val currentUserId by connectionViewModel.currentUserId.collectAsState()
    val connectionAcceptedEvent by connectionViewModel.connectionAcceptedEvent.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Show toast when connection is accepted
    androidx.compose.runtime.LaunchedEffect(connectionAcceptedEvent) {
        connectionAcceptedEvent?.let { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            connectionViewModel.clearConnectionAcceptedEvent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Connections",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pendingRequests.isNotEmpty()) {
            Text(
                text = "Pending Requests",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            pendingRequests.forEach { connection ->
                PendingConnectionCard(
                    connection = connection,
                    onAccept = { connectionViewModel.acceptConnection(connection.id) },
                    onReject = { connectionViewModel.rejectConnection(connection.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "All Connections",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (myConnections.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No connections yet",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(myConnections) { connection ->
                    ConnectionCard(
                        connection = connection,
                        currentUserId = currentUserId,
                        onChatClick = {
                            coroutineScope.launch {
                                val otherUserId = if (connection.requesterId == currentUserId) {
                                    connection.referralGiverId
                                } else {
                                    connection.requesterId
                                }
                                val chat = chatViewModel.getChatByUsers(currentUserId ?: "", otherUserId)
                                chat?.let { onNavigateToChat(it.id) }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectionCard(
    connection: Connection,
    currentUserId: String?,
    onChatClick: () -> Unit = {}
) {
    val otherUserName = if (connection.requesterId == currentUserId) {
        connection.referralGiverName
    } else {
        connection.requesterName
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = otherUserName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Role: ${connection.referralRole}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Status: ${connection.status.name}",
                        fontSize = 14.sp,
                        color = when (connection.status) {
                            ConnectionStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
                            ConnectionStatus.PENDING -> MaterialTheme.colorScheme.secondary
                            ConnectionStatus.REJECTED -> MaterialTheme.colorScheme.error
                        }
                    )
                }

                if (connection.status == ConnectionStatus.ACCEPTED) {
                    FilledTonalIconButton(onClick = onChatClick) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingConnectionCard(
    connection: Connection,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = connection.requesterName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Wants referral for: ${connection.referralRole}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept")
                }
            }
        }
    }
}