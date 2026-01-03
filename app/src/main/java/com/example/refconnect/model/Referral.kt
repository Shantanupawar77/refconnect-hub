package com.example.refconnect.model

data class Referral(
    val id: String,
    val postedByUserId: String,
    val postedByUserName: String,
    val company: String,
    val role: String,
    val description: String,
    val techStack: List<String>,
    val experienceRequired: Int,
    val location: String = "Remote"
)
