package com.example.refconnect.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.model.Referral
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    val referrals: StateFlow<List<Referral>> = repository.getAllReferrals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val myReferrals: StateFlow<List<Referral>> = repository.getMyReferrals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getReferralById(id: String): Referral? {
        return repository.getReferralById(id)
    }

    suspend fun getApplicantCount(referralId: String): Int {
        return repository.getApplicantsForReferral(referralId).size
    }

    suspend fun createReferral(
        company: String,
        role: String,
        description: String,
        techStack: List<String>,
        experienceRequired: Int,
        location: String
    ): Referral {
        Log.d("VM_EVENT", "=== Creating Referral ===")
        Log.d("VM_EVENT", "Company: $company")
        Log.d("VM_EVENT", "Role: $role")
        Log.d("VM_EVENT", "Tech Stack: ${techStack.joinToString(", ")}")
        Log.d("VM_EVENT", "Description Length: ${description.length}")

        val referral = repository.createReferral(
            company,
            role,
            description,
            techStack,
            experienceRequired,
            location
        )

        Log.d("VM_EVENT", "Referral created with ID: ${referral.id}")
        return referral
    }

    suspend fun deleteReferral(referralId: String) {
        repository.deleteReferral(referralId)
    }

    suspend fun getApplicants(referralId: String): List<com.example.refconnect.model.Connection> {
        return repository.getApplicantsForReferral(referralId)
    }

    suspend fun getApplicantProfile(userId: String): com.example.refconnect.model.User? {
        return repository.getApplicantProfile(userId)
    }

    suspend fun getApplicantTestResult(userId: String, referralId: String): com.example.refconnect.model.TestResult? {
        return repository.getApplicantTestResult(userId, referralId)
    }
}