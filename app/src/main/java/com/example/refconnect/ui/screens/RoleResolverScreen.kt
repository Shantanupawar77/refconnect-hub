package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.refconnect.model.UserRole
import com.example.refconnect.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun RoleResolverScreen(
    profileViewModel: ProfileViewModel,
    onNavigateToMyOpportunities: () -> Unit,
    onNavigateToReferralFeed: () -> Unit
) {
    LaunchedEffect(Unit) {
        // Load current user from database/datastore
        profileViewModel.loadCurrentUser()

        // Small delay to ensure user is loaded
        delay(100)

        // Determine navigation based on role
        val userRole = profileViewModel.currentUser?.role

        if (userRole == UserRole.RECRUITER) {
            onNavigateToMyOpportunities()
        } else {
            onNavigateToReferralFeed()
        }
    }

    // Show loading indicator while resolving role
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}