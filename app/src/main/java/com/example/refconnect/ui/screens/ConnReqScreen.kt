package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.ui.components.RefConnectButton
import com.example.refconnect.viewmodel.ConnectionViewModel

@Composable
fun ConnReqScreen(
    referralId: String,
    connectionViewModel: ConnectionViewModel = viewModel(),
    onNavigateToConnections: () -> Unit,
    onNavigateToHome: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var requestSent by remember { mutableStateOf(false) }

    LaunchedEffect(connectionViewModel.requestSubmitted) {
        if (connectionViewModel.requestSubmitted) {
            requestSent = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (requestSent) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Request Sent!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your referral request has been sent successfully. You will be notified once it's accepted.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            RefConnectButton(
                text = "View Connections",
                onClick = {
                    connectionViewModel.resetRequestState()
                    onNavigateToConnections()
                }
            )
        } else {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Send Referral Request",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You're about to send a referral request. The referral giver will review your profile and test results.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            RefConnectButton(
                text = "Send Request",
                onClick = {
                    connectionViewModel.requestConnection(referralId)
                    android.widget.Toast.makeText(
                        context,
                        "Request Sent Successfully!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    onNavigateToHome()
                }
            )
        }
    }
}