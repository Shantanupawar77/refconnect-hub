package com.example.refconnect.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.refconnect.model.Question
import com.example.refconnect.ui.components.ErrorText
import com.example.refconnect.ui.components.RefConnectButton
import com.example.refconnect.viewmodel.ScreeningTestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreeningTestScreen(
    referralId: String,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Boolean) -> Unit,
    screeningTestViewModel: ScreeningTestViewModel = viewModel()
) {
    Log.d("UI_TEST_SCREEN", "=== ScreeningTestScreen Composable Entered ===")
    Log.d("UI_TEST_SCREEN", "Referral ID: $referralId")

    LaunchedEffect(referralId) {
        Log.d("UI_TEST_SCREEN", "LaunchedEffect triggered for referralId: $referralId")
        screeningTestViewModel.loadTest(referralId)
    }

    val test = screeningTestViewModel.currentTest
    val isLoading = screeningTestViewModel.isLoading
    val error = screeningTestViewModel.error

    Log.d("UI_TEST_SCREEN", "Current UI State:")
    Log.d("UI_TEST_SCREEN", "  isLoading: $isLoading")
    Log.d("UI_TEST_SCREEN", "  test: ${if (test != null) "NOT NULL (${test.questions.size} questions)" else "NULL"}")
    Log.d("UI_TEST_SCREEN", "  error: '$error'")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Screening Test") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (test != null) {
            Log.d("UI_TEST_SCREEN", "=== Rendering Test Content ===")
            Log.d("UI_TEST_SCREEN", "Number of questions: ${test.questions.size}")
            test.questions.forEachIndexed { index, q ->
                if (index < 2) {
                    Log.d("UI_TEST_SCREEN", "Question ${index + 1}: ${q.questionText.take(50)}...")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Answer all questions to proceed",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Total Questions: ${test.questions.size}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                test.questions.forEachIndexed { index, question ->
                    QuestionCard(
                        questionNumber = index + 1,
                        question = question,
                        selectedAnswer = screeningTestViewModel.selectedAnswers[index],
                        onAnswerSelected = { answerIndex ->
                            screeningTestViewModel.selectAnswer(index, answerIndex)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                ErrorText(text = screeningTestViewModel.error)

                Spacer(modifier = Modifier.height(16.dp))

                RefConnectButton(
                    text = if (screeningTestViewModel.isSubmitting) "Submitting..." else "Submit Test",
                    onClick = {
                        screeningTestViewModel.submitTest { result ->
                            onNavigateToResult(result.passed)
                        }
                    },
                    enabled = screeningTestViewModel.canSubmitTest()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        } else if (screeningTestViewModel.isLoading) {
            Log.d("UI_TEST_SCREEN", "=== Rendering Loading State ===")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading screening test...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else if (screeningTestViewModel.error.isNotEmpty()) {
            Log.d("UI_TEST_SCREEN", "=== Rendering Error State ===")
            Log.e("UI_TEST_SCREEN", "Error message: ${screeningTestViewModel.error}")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = "Failed to load test",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = screeningTestViewModel.error,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    RefConnectButton(
                        text = "Retry",
                        onClick = {
                            Log.d("UI_TEST_SCREEN", "Retry button clicked")
                            screeningTestViewModel.loadTest(referralId)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Log.d("UI_TEST_SCREEN", "=== Rendering Fallback Loading State ===")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun QuestionCard(
    questionNumber: Int,
    question: Question,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.questionText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedAnswer == index,
                        onClick = { onAnswerSelected(index) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option)
                }
            }
        }
    }
}
