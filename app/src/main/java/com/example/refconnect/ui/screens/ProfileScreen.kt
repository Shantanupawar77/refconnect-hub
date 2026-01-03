package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.ui.components.RefConnectButton
import com.example.refconnect.ui.components.RefConnectOutlinedButton
import com.example.refconnect.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.loadCurrentUser()
    }

    val user = profileViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp)
    ) {
        Text(
            text = "My Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (user != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Professional Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileDetailRow("Company", user.company)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileDetailRow("Experience", "${user.experience} years")
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileDetailRow("Role", when(user.role) {
                        com.example.refconnect.model.UserRole.RECRUITER -> "Recruiter"
                        com.example.refconnect.model.UserRole.SEEKER -> "Seeker"
                    })

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tech Stack",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    user.techStack.forEach { tech ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ", fontWeight = FontWeight.Bold)
                            Text(tech)
                        }
                    }
                }
            }

            // Optional Profile Links Section
            if (user.leetcodeUrl.isNotEmpty() ||
                user.codeforcesUrl.isNotEmpty() ||
                user.codechefUrl.isNotEmpty() ||
                user.githubUrl.isNotEmpty() ||
                user.hackathonExperience.isNotEmpty() ||
                user.achievements.isNotEmpty()) {

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Additional Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (user.leetcodeUrl.isNotEmpty()) {
                            ProfileDetailRow("LeetCode", user.leetcodeUrl)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (user.codeforcesUrl.isNotEmpty()) {
                            ProfileDetailRow("Codeforces", user.codeforcesUrl)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (user.codechefUrl.isNotEmpty()) {
                            ProfileDetailRow("CodeChef", user.codechefUrl)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (user.githubUrl.isNotEmpty()) {
                            ProfileDetailRow("GitHub", user.githubUrl)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (user.hackathonExperience.isNotEmpty()) {
                            Text(
                                text = "Hackathon Experience",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.hackathonExperience,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (user.achievements.isNotEmpty()) {
                            Text(
                                text = "Achievements",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.achievements,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            RefConnectButton(
                text = "Edit Profile",
                onClick = onNavigateToEditProfile,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectOutlinedButton(
                text = "Settings",
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}