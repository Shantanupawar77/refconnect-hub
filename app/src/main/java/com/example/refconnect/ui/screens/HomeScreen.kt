package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.model.Referral
import com.example.refconnect.model.UserRole
import com.example.refconnect.viewmodel.HomeViewModel
import com.example.refconnect.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToReferralDetails: (String) -> Unit,
    onNavigateToCreateReferral: () -> Unit = {},
    onNavigateToApplicants: (String) -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val referrals by homeViewModel.referrals.collectAsState()
    val myReferrals by homeViewModel.myReferrals.collectAsState()
    val scope = rememberCoroutineScope()
    var userRole by remember { mutableStateOf<UserRole?>(null) }
    var applicantCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(Unit) {
        scope.launch {
            profileViewModel.loadCurrentUser()
            userRole = profileViewModel.currentUser?.role
        }
    }

    LaunchedEffect(myReferrals) {
        scope.launch {
            val counts = mutableMapOf<String, Int>()
            myReferrals.forEach { referral ->
                counts[referral.id] = homeViewModel.getApplicantCount(referral.id)
            }
            applicantCounts = counts
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header based on role - RECRUITER ONLY (no BOTH)
        if (userRole == UserRole.RECRUITER) {
            // Recruiter Home
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
                            onViewApplicants = { onNavigateToApplicants(referral.id) }
                        )
                    }
                }
            }
        } else {
            // Seeker Home - Show public feed
            Text(
                text = "Referral Opportunities",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(referrals) { referral ->
                    ReferralCard(
                        referral = referral,
                        onClick = { onNavigateToReferralDetails(referral.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReferralCard(
    referral: Referral,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = referral.role,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = referral.company,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${referral.experienceRequired}+ years exp",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = referral.location,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tech: ${referral.techStack.joinToString(", ")}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Posted by ${referral.postedByUserName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun RecruiterReferralCard(
    referral: Referral,
    applicantCount: Int,
    onViewApplicants: () -> Unit,
    onDelete: () -> Unit = {}
) {
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
                        text = referral.role,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = referral.company,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$applicantCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = if (applicantCount == 1) "Applicant" else "Applicants",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Opportunity",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${referral.experienceRequired}+ years exp",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = referral.location,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tech: ${referral.techStack.joinToString(", ")}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            if (applicantCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onViewApplicants,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Applicants")
                }
            }
        }
    }
}