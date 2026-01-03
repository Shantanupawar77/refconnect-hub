package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refconnect.ui.components.ErrorText
import com.example.refconnect.ui.components.RefConnectButton
import com.example.refconnect.ui.components.RefConnectTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReferralScreen(
    onNavigateBack: () -> Unit,
    homeViewModel: com.example.refconnect.viewmodel.HomeViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var company by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var techStack by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Referral Opportunity") },
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
                text = "Post a New Opportunity",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            RefConnectTextField(
                value = company,
                onValueChange = { company = it },
                label = "Company Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RefConnectTextField(
                value = role,
                onValueChange = { role = it },
                label = "Role/Position",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            RefConnectTextField(
                value = techStack,
                onValueChange = { techStack = it },
                label = "Tech Stack (comma-separated)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RefConnectTextField(
                value = experience,
                onValueChange = { experience = it },
                label = "Years of Experience Required",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RefConnectTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location",
                modifier = Modifier.fillMaxWidth()
            )

            ErrorText(text = error)

            Spacer(modifier = Modifier.height(32.dp))

            if (isCreating) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Creating opportunity and generating screening test...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            RefConnectButton(
                text = "Create Opportunity",
                onClick = {
                    when {
                        company.isEmpty() -> error = "Company name is required"
                        role.isEmpty() -> error = "Role is required"
                        description.isEmpty() -> error = "Description is required"
                        techStack.isEmpty() -> error = "Tech stack is required"
                        experience.isEmpty() -> error = "Experience is required"
                        experience.toIntOrNull() == null -> error = "Experience must be a number"
                        location.isEmpty() -> error = "Location is required"
                        else -> {
                            isCreating = true
                            error = ""
                            scope.launch {
                                try {
                                    homeViewModel.createReferral(
                                        company = company,
                                        role = role,
                                        description = description,
                                        techStack = techStack.split(",").map { it.trim() },
                                        experienceRequired = experience.toInt(),
                                        location = location
                                    )
                                    android.widget.Toast.makeText(
                                        context,
                                        "Opportunity and screening test created successfully!",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                    onNavigateBack()
                                } catch (e: Exception) {
                                    error = "Failed to create opportunity: ${e.message}"
                                    isCreating = false
                                }
                            }
                        }
                    }
                },
                enabled = !isCreating,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
