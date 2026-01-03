package com.example.refconnect.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.model.User
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf("")

    // Edit profile fields
    var editCompany by mutableStateOf("")
    var editExperience by mutableStateOf("")
    var editTechStackInput by mutableStateOf("")
    var editTechStackList by mutableStateOf<List<String>>(emptyList())
    var editLeetcodeUrl by mutableStateOf("")
    var editCodeforcesUrl by mutableStateOf("")
    var editCodechefUrl by mutableStateOf("")
    var editGithubUrl by mutableStateOf("")
    var editHackathonExperience by mutableStateOf("")
    var editAchievements by mutableStateOf("")
    var editError by mutableStateOf("")
    var isSavingEdit by mutableStateOf(false)

    fun loadCurrentUser() {
        isLoading = true
        viewModelScope.launch {
            try {
                currentUser = repository.getCurrentUser()
            } catch (e: Exception) {
                error = "Failed to load profile: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadForEdit() {
        currentUser?.let { user ->
            editCompany = user.company
            editExperience = user.experience.toString()
            editTechStackList = user.techStack
            editLeetcodeUrl = user.leetcodeUrl
            editCodeforcesUrl = user.codeforcesUrl
            editCodechefUrl = user.codechefUrl
            editGithubUrl = user.githubUrl
            editHackathonExperience = user.hackathonExperience
            editAchievements = user.achievements
        }
    }

    fun addEditTechStack() {
        if (editTechStackInput.isNotEmpty()) {
            editTechStackList = editTechStackList + editTechStackInput.trim()
            editTechStackInput = ""
        }
    }

    fun removeEditTechStack(tech: String) {
        editTechStackList = editTechStackList.filter { it != tech }
    }

    fun canSaveEdit(): Boolean {
        return editCompany.isNotEmpty() &&
                editExperience.isNotEmpty() &&
                editExperience.toIntOrNull() != null &&
                editTechStackList.isNotEmpty() &&
                !isSavingEdit
    }

    fun saveEditedProfile(onSuccess: () -> Unit) {
        editError = ""
        when {
            editCompany.isEmpty() -> editError = "Company name is required"
            editExperience.isEmpty() -> editError = "Experience is required"
            editExperience.toIntOrNull() == null -> editError = "Experience must be a valid number"
            editTechStackList.isEmpty() -> editError = "Add at least one technology"
            else -> {
                isSavingEdit = true
                viewModelScope.launch {
                    try {
                        repository.updateUserProfile(
                            company = editCompany,
                            experience = editExperience.toInt(),
                            techStack = editTechStackList,
                            leetcodeUrl = editLeetcodeUrl,
                            codeforcesUrl = editCodeforcesUrl,
                            codechefUrl = editCodechefUrl,
                            githubUrl = editGithubUrl,
                            hackathonExperience = editHackathonExperience,
                            achievements = editAchievements
                        )
                        loadCurrentUser()
                        onSuccess()
                    } catch (e: Exception) {
                        editError = "Failed to save profile: ${e.message}"
                    } finally {
                        isSavingEdit = false
                    }
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.logout()
                currentUser = null
                onSuccess()
            } catch (e: Exception) {
                error = "Logout failed: ${e.message}"
            }
        }
    }
}