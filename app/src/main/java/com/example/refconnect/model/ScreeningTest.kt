package com.example.refconnect.model

data class ScreeningTest(
    val id: String,
    val referralId: String,
    val questions: List<Question>
)

data class Question(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class TestResult(
    val testId: String,
    val score: Int,
    val totalQuestions: Int,
    val passed: Boolean
)