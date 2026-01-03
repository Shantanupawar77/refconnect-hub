package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refconnect.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun MyOpportuniScreen(
    homeViewModel: HomeViewModel,
    onNavigateToCreateReferral: () -> Unit,
    onNavigateToApplicants: (String) -> Unit
) {
    val myReferrals by homeViewModel.myReferrals.collectAsState()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var applicantCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var referralToDelete by remember { mutableStateOf<com.example.refconnect.model.Referral?>(null) }

    LaunchedEffect(myReferrals) {
        scope.launch {
            val counts = mutableMapOf<String, Int>()
            myReferrals.forEach { referral ->
                counts[referral.id] = homeViewModel.getApplicantCount(referral.id)
            }
            applicantCounts = counts
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && referralToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                referralToDelete = null
            },
            title = { Text("Delete Opportunity") },
            text = {
                Text("Are you sure you want to delete \"${referralToDelete?.role}\" at \"${referralToDelete?.company}\"?\n\nThis will permanently delete:\n• The opportunity\n• All applications\n• All test results\n\nThis action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            referralToDelete?.let { referral ->
                                homeViewModel.deleteReferral(referral.id)
                                showDeleteDialog = false
                                referralToDelete = null
                                // Show toast
                                android.widget.Toast.makeText(
                                    context,
                                    "Opportunity deleted",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    referralToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Opportunities",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            FloatingActionButton(
                onClick = onNavigateToCreateReferral,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create referral opportunities and manage applications",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show recruiter's posted opportunities
        if (myReferrals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No opportunities created yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap the + button to create your first opportunity",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myReferrals) { referral ->
                    RecruiterReferralCard(
                        referral = referral,
                        applicantCount = applicantCounts[referral.id] ?: 0,
                        onViewApplicants = { onNavigateToApplicants(referral.id) },
                        onDelete = {
                            referralToDelete = referral
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}