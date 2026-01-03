package com.example.refconnect.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refconnect.model.ScreeningTest
import com.example.refconnect.model.TestResult
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.launch

class ScreeningTestViewModel(
    private val repository: RefConnectRepository
) : ViewModel() {

    var currentTest by mutableStateOf<ScreeningTest?>(null)
        private set

    var selectedAnswers by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    var testResult by mutableStateOf<TestResult?>(null)
        private set

    var error by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSubmitting by mutableStateOf(false)

    fun loadTest(referralId: String) {
        Log.d("VM_STATE", "=== loadTest Called ===")
        Log.d("VM_STATE", "Referral ID: $referralId")
        Log.d("VM_STATE", "Initial state: isLoading=false, currentTest=${currentTest != null}, error='$error'")

        isLoading = true
        Log.d("VM_STATE", "State updated: isLoading=true")

        viewModelScope.launch {
            try {
                Log.d("VM_EVENT", "Calling repository.getScreeningTest($referralId)")
                currentTest = repository.getScreeningTest(referralId)

                if (currentTest == null) {
                    Log.e("VM_STATE", "Repository returned NULL for screening test")
                } else {
                    Log.d("VM_STATE", "Screening test loaded successfully")
                    Log.d("VM_STATE", "Test ID: ${currentTest?.id}")
                    Log.d("VM_STATE", "Number of questions: ${currentTest?.questions?.size}")
                    currentTest?.questions?.forEachIndexed { index, q ->
                        if (index < 2) {
                            Log.d("VM_STATE", "Question ${index + 1}: ${q.questionText.take(50)}...")
                        }
                    }
                }

                selectedAnswers = emptyMap()
                testResult = null
                error = ""
                Log.d("VM_STATE", "State cleared: selectedAnswers=empty, testResult=null, error=''")
            } catch (e: Exception) {
                Log.e("VM_STATE", "Exception in loadTest: ${e.message}", e)
                error = "Failed to load test: ${e.message}"
                Log.d("VM_STATE", "Error state set: error='$error'")
            } finally {
                isLoading = false
                Log.d("VM_STATE", "Final state: isLoading=false, currentTest=${currentTest != null}, error='$error'")
            }
        }
    }

    fun selectAnswer(questionIndex: Int, answerIndex: Int) {
        selectedAnswers = selectedAnswers + (questionIndex to answerIndex)
    }

    fun canSubmitTest(): Boolean {
        val test = currentTest ?: return false
        return selectedAnswers.size == test.questions.size && !isSubmitting
    }

    fun submitTest(onSuccess: (TestResult) -> Unit) {
        error = ""
        if (!canSubmitTest()) {
            error = "Please answer all questions before submitting"
            return
        }

        val test = currentTest ?: return
        val answersList = (0 until test.questions.size).map { selectedAnswers[it] ?: -1 }

        isSubmitting = true
        viewModelScope.launch {
            try {
                val result = repository.submitTest(test.id, answersList)
                testResult = result
                onSuccess(result)
            } catch (e: Exception) {
                error = "Failed to submit test: ${e.message}"
            } finally {
                isSubmitting = false
            }
        }
    }

    fun reset() {
        currentTest = null
        selectedAnswers = emptyMap()
        testResult = null
        error = ""
    }
}