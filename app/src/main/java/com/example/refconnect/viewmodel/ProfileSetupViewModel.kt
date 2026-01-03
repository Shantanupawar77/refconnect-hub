package com.example.refconnect.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.launch

class ProfileSetupViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    var company by mutableStateOf("")
    var experience by mutableStateOf("")
    var techStackInput by mutableStateOf("")
    var techStackList by mutableStateOf<List<String>>(emptyList())
    var leetcodeUrl by mutableStateOf("")
    var codeforcesUrl by mutableStateOf("")
    var codechefUrl by mutableStateOf("")
    var githubUrl by mutableStateOf("")
    var hackathonExperience by mutableStateOf("")
    var achievements by mutableStateOf("")
    var error by mutableStateOf("")
    var isSaving by mutableStateOf(false)

    fun addTechStack() {
        if (techStackInput.isNotEmpty()) {
            techStackList = techStackList + techStackInput.trim()
            techStackInput = ""
        }
    }

    fun removeTechStack(tech: String) {
        techStackList = techStackList.filter { it != tech }
    }

    fun canSaveProfile(): Boolean {
        return company.isNotEmpty() &&
                experience.isNotEmpty() &&
                experience.toIntOrNull() != null &&
                techStackList.isNotEmpty() &&
                !isSaving
    }

    fun saveProfile(onSuccess: () -> Unit) {
        error = ""
        when {
            company.isEmpty() -> error = "Company name is required"
            experience.isEmpty() -> error = "Experience is required"
            experience.toIntOrNull() == null -> error = "Experience must be a valid number"
            techStackList.isEmpty() -> error = "Add at least one technology to your tech stack"
            else -> {
                isSaving = true
                viewModelScope.launch {
                    try {
                        repository.updateUserProfile(
                            company = company,
                            experience = experience.toInt(),
                            techStack = techStackList,
                            leetcodeUrl = leetcodeUrl,
                            codeforcesUrl = codeforcesUrl,
                            codechefUrl = codechefUrl,
                            githubUrl = githubUrl,
                            hackathonExperience = hackathonExperience,
                            achievements = achievements
                        )
                        onSuccess()
                    } catch (e: Exception) {
                        error = "Failed to save profile: ${e.message}"
                    } finally {
                        isSaving = false
                    }
                }
            }
        }
    }

    fun reset() {
        company = ""
        experience = ""
        techStackInput = ""
        techStackList = emptyList()
        leetcodeUrl = ""
        codeforcesUrl = ""
        codechefUrl = ""
        githubUrl = ""
        hackathonExperience = ""
        achievements = ""
        error = ""
    }
}