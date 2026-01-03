package com.example.refconnect.service

import android.util.Log
import com.example.refconnect.BuildConfig
import com.example.refconnect.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Service for generating screening test questions using a real LLM.
 *
 * This service sends the tech stack and job description DIRECTLY to an LLM
 * and receives dynamically generated questions. NO domain-specific logic
 * exists in the app - ALL intelligence comes from the LLM.
 */
interface TestGenerationService {
    suspend fun generateQuestions(
        techStack: List<String>,
        jobDescription: String,
        numberOfQuestions: Int = 5
    ): Result<List<Question>>
}

/**
 * Real LLM implementation using OpenAI-compatible API.
 *
 * This implementation:
 * - Makes REAL HTTP calls to an LLM API
 * - Sends tech stack + job description as natural language
 * - Receives JSON response with questions
 * - NO template logic, NO domain detection, NO keyword mapping
 * - ALL question generation is done by the LLM
 */
class LlmTestGenerationService(
    private val apiKey: String = BuildConfig.GROQ_API_KEY,
    private val apiBaseUrl: String = "https://api.groq.com/openai/v1",
    private val model: String = "llama-3.1-8b-instant"
) : TestGenerationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun generateQuestions(
        techStack: List<String>,
        jobDescription: String,
        numberOfQuestions: Int
    ): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            // LOG: Before API call
            Log.d("LLM_REQUEST", "=== Starting LLM API Call ===")
            Log.d("LLM_REQUEST", "Tech Stack: ${techStack.joinToString(", ")}")
            Log.d("LLM_REQUEST", "Job Description Length: ${jobDescription.length} chars")
            Log.d("LLM_REQUEST", "Number of Questions: $numberOfQuestions")
            Log.d("LLM_REQUEST", "API Base URL: $apiBaseUrl")
            Log.d("LLM_REQUEST", "Model: $model")

            // Build the prompt - send tech stack and job description DIRECTLY to LLM
            val prompt = buildPrompt(techStack, jobDescription, numberOfQuestions)
            Log.d("LLM_REQUEST", "Prompt Length: ${prompt.length} chars")

            // Make REAL API call to LLM
            val requestBody = buildRequestBody(prompt)
            val request = Request.Builder()
                .url("$apiBaseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            Log.d("LLM_REQUEST", "Executing HTTP POST to ${request.url}")

            // Execute HTTP call
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("LLM_RESPONSE", "API call failed with code ${response.code}: ${response.message}")
                Log.e("LLM_RESPONSE", "API call failed with response ${response}")
                return@withContext Result.failure(
                    IOException("LLM API call failed: ${response.code} ${response.message}")
                )
            }

            // Parse response from LLM
            val responseBody = response.body?.string()
            if (responseBody == null) {
                Log.e("LLM_RESPONSE", "Response body is NULL")
                return@withContext Result.failure(IOException("Empty response from LLM"))
            }

            Log.d("LLM_RESPONSE", "Response Body Length: ${responseBody.length} chars")
            Log.d("LLM_RESPONSE", "Response Body (first 500 chars): ${responseBody.take(500)}")

            val questions = parseApiResponse(responseBody)

            // LOG: After parsing
            Log.d("LLM_PARSE", "=== Questions Parsed Successfully ===")
            Log.d("LLM_PARSE", "Number of Questions Parsed: ${questions.size}")
            questions.forEachIndexed { index, question ->
                if (index < 2) {
                    Log.d("LLM_PARSE", "Question ${index + 1}: ${question.questionText}")
                    Log.d("LLM_PARSE", "  Options: ${question.options.size}, Correct: ${question.correctAnswerIndex}")
                }
            }

            Log.d("LLM_PARSE", "Returning Result.success with ${questions.size} questions")
            Result.success(questions)
        } catch (e: IOException) {
            Log.e("LLM_REQUEST", "Network error: ${e.message}", e)
            Result.failure(Exception("Network error calling LLM: ${e.message}"))
        } catch (e: Exception) {
            Log.e("LLM_REQUEST", "Failed to generate questions: ${e.message}", e)
            Log.e("LLM_REQUEST", "Failed to generate questions: with cause ${e.cause} and e: ${e}", e)
            Result.failure(Exception("Failed to generate questions: ${e.message}"))

        }
    }

    /**
     * Build the prompt to send to the LLM.
     * Tech stack and job description are sent as-is - NO processing.
     */
    private fun buildPrompt(
        techStack: List<String>,
        jobDescription: String,
        numberOfQuestions: Int
    ): String {
        return """
Generate exactly $numberOfQuestions medium-to-hard technical screening questions for the following job opportunity.

Tech Stack: ${techStack.joinToString(", ")}

Job Description: $jobDescription

Requirements:
- Exactly $numberOfQuestions questions
- 4 multiple-choice options per question
- 1 correct answer per question (specify index 0-3)
- Medium to hard difficulty
- Questions must be highly relevant to the tech stack and job requirements
- Focus on practical knowledge, problem-solving, and real-world scenarios
- Avoid trivial or easily googleable questions

Return ONLY a valid JSON array in this exact format (no additional text):
[
  {
    "questionText": "Your question here?",
    "options": ["Option A", "Option B", "Option C", "Option D"],
    "correctAnswerIndex": 0
  }
]
        """.trimIndent()
    }

    /**
     * Build the HTTP request body for the OpenAI-compatible API.
     */
    private fun buildRequestBody(prompt: String): RequestBody {
        val json = JSONObject().apply {
            put("model", model)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are an expert technical interviewer who creates challenging, relevant screening questions for software engineering positions. Always return valid JSON.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 2000)
        }

        return json.toString().toRequestBody(mediaType)
    }

    /**
     * Parse the API response from the LLM.
     * Extracts the questions from the response.
     */
    private fun parseApiResponse(responseBody: String): List<Question> {
        Log.d("LLM_PARSE", "=== Parsing API Response ===")
        val responseJson = JSONObject(responseBody)
        val choices = responseJson.getJSONArray("choices")
        Log.d("LLM_PARSE", "Choices array length: ${choices.length()}")

        val firstChoice = choices.getJSONObject(0)
        val message = firstChoice.getJSONObject("message")
        val content = message.getString("content")

        Log.d("LLM_PARSE", "Content extracted from message")
        Log.d("LLM_PARSE", "Content length: ${content.length} chars")
        Log.d("LLM_PARSE", "Content (first 300 chars): ${content.take(300)}")

        // Extract JSON array from content (LLM might return markdown code blocks)
        val jsonContent = extractJsonFromContent(content)
        Log.d("LLM_PARSE", "JSON extracted from content, length: ${jsonContent.length}")

        return parseQuestionsFromJson(jsonContent)
    }

    /**
     * Extract JSON array from LLM response content.
     * Handles cases where LLM wraps JSON in markdown code blocks.
     */
    private fun extractJsonFromContent(content: String): String {
        // Remove markdown code blocks if present
        var cleanContent = content.trim()
        if (cleanContent.startsWith("```json")) {
            cleanContent = cleanContent.removePrefix("```json").removeSuffix("```").trim()
        } else if (cleanContent.startsWith("```")) {
            cleanContent = cleanContent.removePrefix("```").removeSuffix("```").trim()
        }

        // Find JSON array boundaries
        val startIndex = cleanContent.indexOf('[')
        val endIndex = cleanContent.lastIndexOf(']')

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            throw IllegalStateException("No valid JSON array found in LLM response")
        }

        return cleanContent.substring(startIndex, endIndex + 1)
    }

    /**
     * Parse questions from JSON array.
     */
    private fun parseQuestionsFromJson(jsonContent: String): List<Question> {
        Log.d("LLM_PARSE", "=== Parsing Questions from JSON ===")
        val questions = mutableListOf<Question>()
        val jsonArray = JSONArray(jsonContent)
        Log.d("LLM_PARSE", "JSON Array length: ${jsonArray.length()}")

        for (i in 0 until jsonArray.length()) {
            val questionJson = jsonArray.getJSONObject(i)

            val questionText = questionJson.getString("questionText")
            val optionsArray = questionJson.getJSONArray("options")
            val options = mutableListOf<String>()

            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }

            val correctAnswerIndex = questionJson.getInt("correctAnswerIndex")

            Log.d("LLM_PARSE", "Question $i: Text='${questionText.take(50)}...', Options=${options.size}, CorrectIdx=$correctAnswerIndex")

            // Validate
            if (options.size != 4) {
                Log.e("LLM_PARSE", "Validation failed: Question has ${options.size} options (expected 4)")
                throw IllegalStateException("Question must have exactly 4 options")
            }
            if (correctAnswerIndex !in 0..3) {
                Log.e("LLM_PARSE", "Validation failed: Correct answer index $correctAnswerIndex not in range 0-3")
                throw IllegalStateException("Correct answer index must be 0-3")
            }

            val question = Question(
                id = UUID.randomUUID().toString(),
                questionText = questionText,
                options = options,
                correctAnswerIndex = correctAnswerIndex
            )
            questions.add(question)
            Log.d("LLM_PARSE", "Question $i added with ID: ${question.id}")
        }

        if (questions.isEmpty()) {
            Log.e("LLM_PARSE", "No questions generated by LLM")
            throw IllegalStateException("No questions generated by LLM")
        }

        Log.d("LLM_PARSE", "Successfully parsed ${questions.size} questions")
        return questions
    }
}

/**
 * Fallback service for development/testing when LLM API is not available.
 * This generates basic questions but does NOT use templates or domain logic.
 * It creates generic questions based on the tech stack items themselves.
 */
class FallbackTestGenerationService : TestGenerationService {

    override suspend fun generateQuestions(
        techStack: List<String>,
        jobDescription: String,
        numberOfQuestions: Int
    ): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            // Simulate network delay
            kotlinx.coroutines.delay(1500)

            val questions = mutableListOf<Question>()

            // Generate simple questions directly from tech stack
            // This is ONLY a fallback - production should use LlmTestGenerationService
            for (i in 0 until numberOfQuestions) {
                val tech = if (techStack.isNotEmpty()) {
                    techStack[i % techStack.size]
                } else {
                    "General Programming"
                }

                questions.add(
                    Question(
                        id = UUID.randomUUID().toString(),
                        questionText = "What is your experience level with $tech?",
                        options = listOf(
                            "Expert - I have extensive production experience",
                            "Intermediate - I have worked on several projects",
                            "Beginner - I have basic knowledge",
                            "No experience - I am willing to learn"
                        ),
                        correctAnswerIndex = 0
                    )
                )
            }

            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(Exception("Fallback service failed: ${e.message}"))
        }
    }
}
