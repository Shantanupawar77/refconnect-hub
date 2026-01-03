package com.example.refconnect.repository

import android.util.Log
import com.example.refconnect.BuildConfig
import com.example.refconnect.data.local.*
import com.example.refconnect.data.local.entity.*
import com.example.refconnect.model.*
import com.example.refconnect.service.TestGenerationService
import com.example.refconnect.service.LlmTestGenerationService
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import java.util.UUID

/**
 * Repository pattern implementation using Room database for persistent storage.
 * All data operations go through DAOs and are mapped between entities and domain models.
 */
class RefConnectRepository(
    private val database: AppDatabase,
    private val preferencesManager: PreferencesManager,
    private val testGenerationService: TestGenerationService = LlmTestGenerationService(apiKey = BuildConfig.GROQ_API_KEY)
) {

    // ========== Current User Management ==========

    val currentUserId: Flow<String?> = preferencesManager.currentUserId
    val isLoggedIn: Flow<Boolean> = preferencesManager.isLoggedIn

    suspend fun getCurrentUser(): User? {
        val userId = preferencesManager.currentUserId.first() ?: return null
        val userEntity = database.userDao().getUserById(userId) ?: return null
        val profile = database.profileDao().getProfileByUserId(userId)
        return userEntity.toUser(profile)
    }

    // ========== Authentication ==========

    suspend fun login(email: String, password: String, role: UserRole): User? {
        val userEntity = database.userDao().login(email, password) ?: return null

        // Enforce role consistency - user must login with the role they signed up with
        if (userEntity.role != role.name) {
            val existingRole = when (userEntity.role) {
                "RECRUITER" -> "Recruiter"
                "SEEKER" -> "Seeker"
                else -> userEntity.role
            }
            throw IllegalStateException("A $existingRole profile already exists for this email")
        }

        val profile = database.profileDao().getProfileByUserId(userEntity.id)

        // Save session
        preferencesManager.setCurrentUser(userEntity.id)

        return userEntity.toUser(profile)
    }

    suspend fun signup(name: String, email: String, password: String, role: UserRole): User {
        // Check if email already exists with different role
        val existingUser = database.userDao().getUserByEmail(email)
        if (existingUser != null) {
            val existingRole = when (existingUser.role) {
                "RECRUITER" -> "Recruiter"
                "SEEKER" -> "Seeker"
                else -> existingUser.role
            }
            throw IllegalStateException("A $existingRole profile already exists for this email")
        }

        val userId = UUID.randomUUID().toString()
        val userEntity = UserEntity(
            id = userId,
            name = name,
            email = email,
            password = password,
            role = role.name
        )

        database.userDao().insert(userEntity)
        preferencesManager.setCurrentUser(userId)

        return userEntity.toUser(null)
    }

    suspend fun updateUserProfile(
        company: String,
        experience: Int,
        techStack: List<String>,
        leetcodeUrl: String = "",
        codeforcesUrl: String = "",
        codechefUrl: String = "",
        githubUrl: String = "",
        hackathonExperience: String = "",
        achievements: String = ""
    ) {
        val userId = preferencesManager.currentUserId.first() ?: return

        val profile = ProfileEntity(
            userId = userId,
            company = company,
            experience = experience,
            techStack = techStack.joinToString(","),
            leetcodeUrl = leetcodeUrl,
            codeforcesUrl = codeforcesUrl,
            codechefUrl = codechefUrl,
            githubUrl = githubUrl,
            hackathonExperience = hackathonExperience,
            achievements = achievements,
            profileSetupComplete = true
        )

        database.profileDao().insert(profile)
    }

    suspend fun logout() {
        preferencesManager.clearCurrentUser()
    }

    // ========== Referrals ==========

    fun getAllReferrals(): Flow<List<Referral>> {
        return database.referralDao().getAllReferrals()
            .map { entities -> entities.map { it.toReferral() } }
    }

    fun getMyReferrals(): Flow<List<Referral>> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                database.referralDao().getReferralsByUser(userId)
                    .map { entities -> entities.map { it.toReferral() } }
            } else {
                flowOf(emptyList())
            }
        }
    }

    suspend fun getApplicantsForReferral(referralId: String): List<Connection> {
        return database.connectionDao().getConnectionsByReferral(referralId)
            .map { it.toConnection() }
    }

    suspend fun getApplicantTestResult(userId: String, referralId: String): TestResult? {
        val referral = getReferralById(referralId) ?: return null
        val test = getScreeningTest(referralId) ?: return null
        val attempt = database.testAttemptDao().getPassedAttempt(userId, test.id)
            ?: database.testAttemptDao().getLatestAttempt(userId, test.id)
        return attempt?.toTestResult()
    }

    suspend fun getApplicantProfile(userId: String): User? {
        val userEntity = database.userDao().getUserById(userId) ?: return null
        val profile = database.profileDao().getProfileByUserId(userId)
        return userEntity.toUser(profile)
    }

    suspend fun getReferralById(id: String): Referral? {
        return database.referralDao().getReferralById(id)?.toReferral()
    }

    suspend fun createReferral(
        company: String,
        role: String,
        description: String,
        techStack: List<String>,
        experienceRequired: Int,
        location: String
    ): Referral {
        val userId = preferencesManager.currentUserId.first() ?: throw IllegalStateException("Not logged in")
        val user = getCurrentUser() ?: throw IllegalStateException("User not found")

        val referralId = UUID.randomUUID().toString()
        val entity = ReferralEntity(
            id = referralId,
            postedByUserId = userId,
            postedByUserName = user.name,
            company = company,
            role = role,
            description = description,
            techStack = techStack.joinToString(","),
            experienceRequired = experienceRequired,
            location = location
        )

        database.referralDao().insert(entity)

        // Generate screening test for this referral
        generateScreeningTestForReferral(referralId, techStack, description)

        return entity.toReferral()
    }

    /**
     * Generate screening test questions dynamically based on tech stack and job description.
     * This is called automatically when a new referral is created.
     */
    suspend fun generateScreeningTestForReferral(
        referralId: String,
        techStack: List<String>,
        jobDescription: String
    ): Result<ScreeningTest> {
        return try {
            Log.d("REPO_AI", "=== Generate Screening Test Started ===")
            Log.d("REPO_AI", "Referral ID: $referralId")
            Log.d("REPO_AI", "Tech Stack: ${techStack.joinToString(", ")}")
            Log.d("REPO_AI", "Job Description Length: ${jobDescription.length}")

            // Generate questions using AI service
            Log.d("REPO_AI", "Calling testGenerationService.generateQuestions()")
            val questionsResult = testGenerationService.generateQuestions(
                techStack = techStack,
                jobDescription = jobDescription,
                numberOfQuestions = 5
            )

            Log.d("REPO_AI", "AI Service returned: isSuccess=${questionsResult.isSuccess}, isFailure=${questionsResult.isFailure}")

            if (questionsResult.isFailure) {
                val error = questionsResult.exceptionOrNull() ?: Exception("Failed to generate questions")
                Log.e("REPO_AI", "Failed to generate questions: ${error.message}")
                return Result.failure(error)
            }

            val questions = questionsResult.getOrThrow()
            Log.d("REPO_AI", "Successfully received ${questions.size} questions from AI service")
            questions.forEachIndexed { index, q ->
                if (index < 2) {
                    Log.d("REPO_AI", "Question ${index + 1}: ${q.questionText.take(50)}...")
                }
            }

            // Create test entity
            val testId = UUID.randomUUID().toString()
            Log.d("REPO_DB", "=== Saving to Database ===")
            Log.d("REPO_DB", "Generated Test ID: $testId")
            Log.d("REPO_DB", "Referral ID: $referralId")

            val testEntity = ScreeningTestEntity(
                id = testId,
                referralId = referralId,
                passThreshold = 60 // 60% pass threshold
            )

            // Save test to database
            Log.d("REPO_DB", "Inserting ScreeningTestEntity...")
            database.screeningTestDao().insert(testEntity)
            Log.d("REPO_DB", "ScreeningTestEntity inserted successfully")

            // Convert questions to entities and save
            val questionEntities = questions.mapIndexed { index, question ->
                QuestionEntity(
                    id = question.id,
                    testId = testId,
                    questionText = question.questionText,
                    options = JSONArray(question.options).toString(),
                    correctAnswerIndex = question.correctAnswerIndex,
                    orderIndex = index
                )
            }

            Log.d("REPO_DB", "Inserting ${questionEntities.size} QuestionEntities...")
            database.questionDao().insertAll(questionEntities)
            Log.d("REPO_DB", "All questions inserted successfully")

            // Return the created test
            val screeningTest = ScreeningTest(
                id = testId,
                referralId = referralId,
                questions = questions
            )

            Log.d("REPO_AI", "=== Screening Test Created Successfully ===")
            Log.d("REPO_AI", "Test ID: $testId, Questions: ${questions.size}")
            Result.success(screeningTest)
        } catch (e: Exception) {
            Log.e("REPO_AI", "Failed to create screening test: ${e.message}", e)
            Result.failure(Exception("Failed to create screening test: ${e.message}"))
        }
    }

    /**
     * Check if a screening test exists for a referral
     */
    suspend fun hasScreeningTest(referralId: String): Boolean {
        return database.screeningTestDao().getTestByReferralId(referralId) != null
    }

    suspend fun deleteReferral(referralId: String) {
        database.referralDao().deleteById(referralId)
    }

    // ========== Screening Tests ==========

    suspend fun getScreeningTest(referralId: String): ScreeningTest? {
        Log.d("REPO_DB", "=== Fetching Screening Test ===")
        Log.d("REPO_DB", "Referral ID: $referralId")

        val testEntity = database.screeningTestDao().getTestByReferralId(referralId)
        if (testEntity == null) {
            Log.e("REPO_DB", "No ScreeningTestEntity found for referralId: $referralId")
            return null
        }

        Log.d("REPO_DB", "Found ScreeningTestEntity: testId=${testEntity.id}, passThreshold=${testEntity.passThreshold}")

        val questions = database.questionDao().getQuestionsByTestId(testEntity.id)
        Log.d("REPO_DB", "Fetched ${questions.size} QuestionEntities for testId: ${testEntity.id}")

        questions.forEachIndexed { index, q ->
            if (index < 2) {
                Log.d("REPO_DB", "Question $index: ${q.questionText.take(50)}..., options length=${q.options.length}")
            }
        }

        val screeningTest = testEntity.toScreeningTest(questions)
        Log.d("REPO_DB", "Converted to ScreeningTest with ${screeningTest.questions.size} questions")

        return screeningTest
    }

    suspend fun submitTest(testId: String, answers: List<Int>): TestResult {
        val userId = preferencesManager.currentUserId.first() ?: throw IllegalStateException("Not logged in")
        val testEntity = database.screeningTestDao().getTestById(testId) ?: throw IllegalStateException("Test not found")
        val questions = database.questionDao().getQuestionsByTestId(testId)

        // Calculate score
        var correctCount = 0
        questions.forEachIndexed { index, question ->
            if (index < answers.size && answers[index] == question.correctAnswerIndex) {
                correctCount++
            }
        }

        val scorePercentage = (correctCount * 100) / questions.size
        val passed = scorePercentage >= testEntity.passThreshold

        // Save attempt
        val attempt = TestAttemptEntity(
            userId = userId,
            testId = testId,
            score = correctCount,
            totalQuestions = questions.size,
            passed = passed,
            answers = JSONArray(answers).toString()
        )
        database.testAttemptDao().insert(attempt)

        return TestResult(
            testId = testId,
            score = correctCount,
            totalQuestions = questions.size,
            passed = passed
        )
    }

    suspend fun hasPassedTest(testId: String): Boolean {
        val userId = preferencesManager.currentUserId.first() ?: return false
        return database.testAttemptDao().getPassedAttempt(userId, testId) != null
    }

    // ========== Connections ==========

    suspend fun requestConnection(referralId: String): Connection? {
        val userId = preferencesManager.currentUserId.first() ?: return null
        val user = getCurrentUser() ?: return null
        val referral = getReferralById(referralId) ?: return null

        val connectionId = UUID.randomUUID().toString()
        val entity = ConnectionEntity(
            id = connectionId,
            requesterId = userId,
            requesterName = user.name,
            referralGiverId = referral.postedByUserId,
            referralGiverName = referral.postedByUserName,
            referralId = referralId,
            referralRole = referral.role,
            status = "PENDING",
            isUnreadByGiver = true,
            isUnreadBySeeker = false
        )

        database.connectionDao().insert(entity)
        return entity.toConnection()
    }

    fun getMyConnections(): Flow<List<Connection>> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                database.connectionDao().getConnectionsByUser(userId)
                    .map { entities -> entities.map { it.toConnection() } }
            } else {
                flowOf(emptyList())
            }
        }
    }

    fun getPendingConnectionRequests(): Flow<List<Connection>> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                database.connectionDao().getPendingRequestsForGiver(userId)
                    .map { entities -> entities.map { it.toConnection() } }
            } else {
                flowOf(emptyList())
            }
        }
    }

    fun getUnreadConnectionsCount(): Flow<Int> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                database.connectionDao().getUnreadCountForGiver(userId)
            } else {
                flowOf(0)
            }
        }
    }

    suspend fun acceptConnection(connectionId: String) {
        val connection = database.connectionDao().getConnectionById(connectionId) ?: return

        // Update connection status
        val updated = connection.copy(
            status = "ACCEPTED",
            isUnreadBySeeker = true,
            respondedAt = System.currentTimeMillis()
        )
        database.connectionDao().update(updated)

        // Create chat
        val chatId = UUID.randomUUID().toString()
        val chat = ChatEntity(
            id = chatId,
            user1Id = connection.requesterId,
            user1Name = connection.requesterName,
            user2Id = connection.referralGiverId,
            user2Name = connection.referralGiverName
        )
        database.chatDao().insert(chat)
    }

    suspend fun rejectConnection(connectionId: String) {
        val connection = database.connectionDao().getConnectionById(connectionId) ?: return
        val updated = connection.copy(
            status = "REJECTED",
            isUnreadBySeeker = true,
            respondedAt = System.currentTimeMillis()
        )
        database.connectionDao().update(updated)
    }

    suspend fun markConnectionsAsRead() {
        val userId = preferencesManager.currentUserId.first() ?: return
        database.connectionDao().markAllAsReadForGiver(userId)
    }

    // ========== Chats ==========

    fun getMyChats(): Flow<List<Chat>> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                database.chatDao().getChatsByUser(userId)
                    .map { entities -> entities.map { it.toChat() } }
            } else {
                flowOf(emptyList())
            }
        }
    }

    suspend fun getChatById(chatId: String): Chat? {
        return database.chatDao().getChatById(chatId)?.toChat()
    }

    suspend fun getChatByUsers(userId1: String, userId2: String): Chat? {
        return database.chatDao().getChatByUsers(userId1, userId2)?.toChat()
    }

    fun getMessagesForChat(chatId: String): Flow<List<Message>> {
        return database.messageDao().getMessagesByChatId(chatId)
            .map { entities -> entities.map { it.toMessage() } }
    }

    fun getTotalUnreadMessagesCount(): Flow<Int> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                database.chatDao().getTotalUnreadCount(userId)
            } else {
                flowOf(0)
            }
        }
    }

    suspend fun sendMessage(chatId: String, content: String) {
        val userId = preferencesManager.currentUserId.first() ?: return
        val user = getCurrentUser() ?: return
        val chat = database.chatDao().getChatById(chatId) ?: return

        // Create message
        val messageId = UUID.randomUUID().toString()
        val message = MessageEntity(
            id = messageId,
            chatId = chatId,
            senderId = userId,
            senderName = user.name,
            content = content,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        database.messageDao().insert(message)

        val isUser1 = chat.user1Id == userId
        val updatedChat = chat.copy(
            lastMessage = content,
            lastMessageTime = System.currentTimeMillis(),
            unreadCountUser1 = if (isUser1) chat.unreadCountUser1 else chat.unreadCountUser1 + 1,
            unreadCountUser2 = if (isUser1) chat.unreadCountUser2 + 1 else chat.unreadCountUser2
        )
        database.chatDao().update(updatedChat)
    }

    suspend fun markChatAsRead(chatId: String) {
        val userId = preferencesManager.currentUserId.first() ?: return
        val chat = database.chatDao().getChatById(chatId) ?: return
        if (chat.user1Id == userId) {
            database.chatDao().clearUnreadForUser1(chatId)
        } else {
            database.chatDao().clearUnreadForUser2(chatId)
        }
        database.messageDao().markMessagesAsRead(chatId, userId)
    }
}