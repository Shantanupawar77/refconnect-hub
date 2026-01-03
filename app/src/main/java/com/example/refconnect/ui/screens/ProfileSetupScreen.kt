package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.ui.components.ErrorText
import com.example.refconnect.ui.components.RefConnectButton
import com.example.refconnect.ui.components.RefConnectTextField
import com.example.refconnect.ui.components.TechStackChip
import com.example.refconnect.viewmodel.ProfileSetupViewModel

@Composable
fun ProfileSetupScreen(
    onNavigateToHome: () -> Unit,
    profileSetupViewModel: ProfileSetupViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Setup Your Profile",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tell us about yourself",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        RefConnectTextField(
            value = profileSetupViewModel.company,
            onValueChange = { profileSetupViewModel.company = it },
            label = "Company Name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        RefConnectTextField(
            value = profileSetupViewModel.experience,
            onValueChange = { profileSetupViewModel.experience = it },
            label = "Years of Experience",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tech Stack",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RefConnectTextField(
                value = profileSetupViewModel.techStackInput,
                onValueChange = { profileSetupViewModel.techStackInput = it },
                label = "Add Technology",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = { profileSetupViewModel.addTechStack() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (profileSetupViewModel.techStackList.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profileSetupViewModel.techStackList.forEach { tech ->
                    TechStackChip(
                        text = tech,
                        onRemove = { profileSetupViewModel.removeTechStack(tech) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Optional Profile Links (Add to stand out!)",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        RefConnectTextField(
            value = profileSetupViewModel.leetcodeUrl,
            onValueChange = { profileSetupViewModel.leetcodeUrl = it },
            label = "LeetCode Profile URL (Optional)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        RefConnectTextField(
            value = profileSetupViewModel.codeforcesUrl,
            onValueChange = { profileSetupViewModel.codeforcesUrl = it },
            label = "Codeforces Profile URL (Optional)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        RefConnectTextField(
            value = profileSetupViewModel.codechefUrl,
            onValueChange = { profileSetupViewModel.codechefUrl = it },
            label = "CodeChef Profile URL (Optional)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        RefConnectTextField(
            value = profileSetupViewModel.githubUrl,
            onValueChange = { profileSetupViewModel.githubUrl = it },
            label = "GitHub Profile URL (Optional)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        RefConnectTextField(
            value = profileSetupViewModel.hackathonExperience,
            onValueChange = { profileSetupViewModel.hackathonExperience = it },
            label = "Hackathon Experience (Optional)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        RefConnectTextField(
            value = profileSetupViewModel.achievements,
            onValueChange = { profileSetupViewModel.achievements = it },
            label = "Achievements (Optional)"
        )

        ErrorText(text = profileSetupViewModel.error)

        Spacer(modifier = Modifier.height(32.dp))

        RefConnectButton(
            text = if (profileSetupViewModel.isSaving) "Saving..." else "Save Profile",
            onClick = {
                profileSetupViewModel.saveProfile {
                    onNavigateToHome()
                }
            },
            enabled = profileSetupViewModel.canSaveProfile()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}