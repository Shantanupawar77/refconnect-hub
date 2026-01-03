package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.ui.components.RefConnectButton
import com.example.refconnect.viewmodel.ScreeningTestViewModel

@Composable
fun TestResultScreen(
    passed: Boolean,
    onNavigateToConnectionRequest: () -> Unit,
    onNavigateBack: () -> Unit,
    screeningTestViewModel: ScreeningTestViewModel = viewModel()
) {
    val result = screeningTestViewModel.testResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (passed) "Congratulations!" else "Test Not Passed",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (result != null) {
            Text(
                text = "Score: ${result.score}/${result.totalQuestions}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (passed)
                    "You have successfully passed the screening test!"
                else
                    "You need at least 60% to pass. Please try again later.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (passed) {
            RefConnectButton(
                text = "Request Referral",
                onClick = onNavigateToConnectionRequest
            )
        } else {
            RefConnectButton(
                text = "Back to Home",
                onClick = onNavigateBack
            )
        }
    }
}