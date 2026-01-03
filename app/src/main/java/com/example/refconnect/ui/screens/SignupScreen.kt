package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.model.UserRole
import com.example.refconnect.ui.components.*
import com.example.refconnect.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onNavigateToProfileSetup: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        AppLogo(size = LogoSize.MEDIUM)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign up to get started",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        RefConnectTextField(
            value = authViewModel.signupName,
            onValueChange = { authViewModel.signupName = it },
            label = "Full Name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        RefConnectTextField(
            value = authViewModel.signupEmail,
            onValueChange = { authViewModel.signupEmail = it },
            label = "Email",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        RefConnectPasswordField(
            value = authViewModel.signupPassword,
            onValueChange = { authViewModel.signupPassword = it },
            label = "Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        RefConnectPasswordField(
            value = authViewModel.signupConfirmPassword,
            onValueChange = { authViewModel.signupConfirmPassword = it },
            label = "Confirm Password"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Role Dropdown
        var roleExpanded by remember { mutableStateOf(false) }
        val roles = UserRole.values().toList()

        ExposedDropdownMenuBox(
            expanded = roleExpanded,
            onExpandedChange = { roleExpanded = !roleExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = when (authViewModel.signupRole) {
                    UserRole.RECRUITER -> "Recruiter"
                    UserRole.SEEKER -> "Seeker"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Your Role") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = roleExpanded,
                onDismissRequest = { roleExpanded = false }
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = {
                            Text(when (role) {
                                UserRole.RECRUITER -> "Recruiter"
                                UserRole.SEEKER -> "Seeker"
                            })
                        },
                        onClick = {
                            authViewModel.signupRole = role
                            roleExpanded = false
                        }
                    )
                }
            }
        }

        ErrorText(text = authViewModel.signupError)

        Spacer(modifier = Modifier.height(24.dp))

        RefConnectButton(
            text = if (authViewModel.isSigningUp) "Creating Account..." else "Sign Up",
            onClick = {
                authViewModel.signup { user ->
                    onNavigateToProfileSetup()
                }
            },
            enabled = authViewModel.canSignup()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}