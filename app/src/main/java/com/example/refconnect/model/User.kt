package com.example.refconnect.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val company: String = "",
    val experience: Int = 0,
    val techStack: List<String> = emptyList(),
    val leetcodeUrl: String = "",
    val codeforcesUrl: String = "",
    val codechefUrl: String = "",
    val githubUrl: String = "",
    val hackathonExperience: String = "",
    val achievements: String = "",
    val profileSetupComplete: Boolean = false
)

enum class UserRole {
    RECRUITER,
    SEEKER
}
