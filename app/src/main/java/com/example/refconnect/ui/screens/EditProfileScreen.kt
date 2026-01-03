package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.ui.components.*
import com.example.refconnect.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.loadCurrentUser()
        profileViewModel.loadForEdit()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Update Your Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            RefConnectTextField(
                value = profileViewModel.editCompany,
                onValueChange = { profileViewModel.editCompany = it },
                label = "Company Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RefConnectTextField(
                value = profileViewModel.editExperience,
                onValueChange = { profileViewModel.editExperience = it },
                label = "Years of Experience",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
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
                    value = profileViewModel.editTechStackInput,
                    onValueChange = { profileViewModel.editTechStackInput = it },
                    label = "Add Technology",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { profileViewModel.addEditTechStack() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (profileViewModel.editTechStackList.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profileViewModel.editTechStackList.forEach { tech ->
                        TechStackChip(
                            text = tech,
                            onRemove = { profileViewModel.removeEditTechStack(tech) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Optional Profile Links",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectTextField(
                value = profileViewModel.editLeetcodeUrl,
                onValueChange = { profileViewModel.editLeetcodeUrl = it },
                label = "LeetCode Profile URL (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectTextField(
                value = profileViewModel.editCodeforcesUrl,
                onValueChange = { profileViewModel.editCodeforcesUrl = it },
                label = "Codeforces Profile URL (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectTextField(
                value = profileViewModel.editCodechefUrl,
                onValueChange = { profileViewModel.editCodechefUrl = it },
                label = "CodeChef Profile URL (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectTextField(
                value = profileViewModel.editGithubUrl,
                onValueChange = { profileViewModel.editGithubUrl = it },
                label = "GitHub Profile URL (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectTextField(
                value = profileViewModel.editHackathonExperience,
                onValueChange = { profileViewModel.editHackathonExperience = it },
                label = "Hackathon Experience (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            RefConnectTextField(
                value = profileViewModel.editAchievements,
                onValueChange = { profileViewModel.editAchievements = it },
                label = "Achievements (Optional)",
                modifier = Modifier.fillMaxWidth()
            )

            ErrorText(text = profileViewModel.editError)

            Spacer(modifier = Modifier.height(32.dp))

            RefConnectButton(
                text = if (profileViewModel.isSavingEdit) "Saving..." else "Save Changes",
                onClick = {
                    profileViewModel.saveEditedProfile {
                        onNavigateBack()
                    }
                },
                enabled = profileViewModel.canSaveEdit(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}