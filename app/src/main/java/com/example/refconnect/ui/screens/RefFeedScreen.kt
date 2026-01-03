package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refconnect.viewmodel.HomeViewModel

@Composable
fun RefFeedScreen(
    homeViewModel: HomeViewModel,
    onNavigateToReferralDetails: (String) -> Unit
) {
    val referrals by homeViewModel.referrals.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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