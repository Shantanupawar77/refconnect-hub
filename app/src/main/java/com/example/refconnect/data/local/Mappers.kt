package com.example.refconnect.data.local

import com.example.refconnect.data.local.entity.*
import com.example.refconnect.model.*
import org.json.JSONArray

// User mappers
fun UserEntity.toUser(profile: ProfileEntity?) = User(
    id = id,
    name = name,
    email = email,
    password = password,
    role = UserRole.valueOf(role),
    company = profile?.company ?: "",
    experience = profile?.experience ?: 0,
    techStack = profile?.techStack?.split(",")?.map { it.trim() } ?: emptyList(),
    leetcodeUrl = profile?.leetcodeUrl ?: "",
    codeforcesUrl = profile?.codeforcesUrl ?: "",
    codechefUrl = profile?.codechefUrl ?: "",
    githubUrl = profile?.githubUrl ?: "",
    hackathonExperience = profile?.hackathonExperience ?: "",
    achievements = profile?.achievements ?: "",
    profileSetupComplete = profile?.profileSetupComplete ?: false
)

fun User.toUserEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    password = password,
    role = role.name,
    profileImageUrl = null
)

fun User.toProfileEntity() = ProfileEntity(
    userId = id,
    company = company,
    experience = experience,
    techStack = techStack.joinToString(","),
    leetcodeUrl = leetcodeUrl,
    codeforcesUrl = codeforcesUrl,
    codechefUrl = codechefUrl,
    githubUrl = githubUrl,
    hackathonExperience = hackathonExperience,
    achievements = achievements,
    profileSetupComplete = profileSetupComplete
)

// Referral mappers
fun ReferralEntity.toReferral() = Referral(
    id = id,
    postedByUserId = postedByUserId,
    postedByUserName = postedByUserName,
    company = company,
    role = role,
    description = description,
    techStack = techStack.split(",").map { it.trim() },
    experienceRequired = experienceRequired,
    location = location
)

fun Referral.toReferralEntity(difficulty: String = "MEDIUM", techTag: String = "GENERAL") = ReferralEntity(
    id = id,
    postedByUserId = postedByUserId,
    postedByUserName = postedByUserName,
    company = company,
    role = role,
    description = description,
    techStack = techStack.joinToString(","),
    experienceRequired = experienceRequired,
    location = location,
    difficulty = difficulty,
    techTag = techTag,
    companyLogoUrl = null
)

// Question mappers
fun QuestionEntity.toQuestion(): Question {
    val optionsArray = JSONArray(options)
    val optionsList = mutableListOf<String>()
    for (i in 0 until optionsArray.length()) {
        optionsList.add(optionsArray.getString(i))
    }
    return Question(
        id = id,
        questionText = questionText,
        options = optionsList,
        correctAnswerIndex = correctAnswerIndex
    )
}

fun Question.toQuestionEntity(testId: String, orderIndex: Int = 0) = QuestionEntity(
    id = id,
    testId = testId,
    questionText = questionText,
    options = JSONArray(options).toString(),
    correctAnswerIndex = correctAnswerIndex,
    orderIndex = orderIndex
)

// ScreeningTest mappers
suspend fun ScreeningTestEntity.toScreeningTest(questions: List<QuestionEntity>): ScreeningTest {
    return ScreeningTest(
        id = id,
        referralId = referralId,
        questions = questions.map { it.toQuestion() }
    )
}

// TestResult mappers
fun TestAttemptEntity.toTestResult() = TestResult(
    testId = testId,
    score = score,
    totalQuestions = totalQuestions,
    passed = passed
)

// Connection mappers
fun ConnectionEntity.toConnection() = Connection(
    id = id,
    requesterId = requesterId,
    requesterName = requesterName,
    referralGiverId = referralGiverId,
    referralGiverName = referralGiverName,
    referralId = referralId,
    referralRole = referralRole,
    status = ConnectionStatus.valueOf(status),
    requestedAt = requestedAt
)

fun Connection.toConnectionEntity() = ConnectionEntity(
    id = id,
    requesterId = requesterId,
    requesterName = requesterName,
    referralGiverId = referralGiverId,
    referralGiverName = referralGiverName,
    referralId = referralId,
    referralRole = referralRole,
    status = status.name,
    requestedAt = requestedAt
)

// Chat mappers
fun ChatEntity.toChat() = Chat(
    id = id,
    user1Id = user1Id,
    user1Name = user1Name,
    user2Id = user2Id,
    user2Name = user2Name,
    lastMessage = lastMessage,
    lastMessageTime = lastMessageTime
)

fun Chat.toChatEntity(unreadCountUser1: Int = 0, unreadCountUser2: Int = 0) = ChatEntity(
    id = id,
    user1Id = user1Id,
    user1Name = user1Name,
    user2Id = user2Id,
    user2Name = user2Name,
    lastMessage = lastMessage,
    lastMessageTime = lastMessageTime,
    unreadCountUser1 = unreadCountUser1,
    unreadCountUser2 = unreadCountUser2
)

// Message mappers
fun MessageEntity.toMessage() = Message(
    id = id,
    chatId = chatId,
    senderId = senderId,
    senderName = senderName,
    content = content,
    timestamp = timestamp
)

fun Message.toMessageEntity() = MessageEntity(
    id = id,
    chatId = chatId,
    senderId = senderId,
    senderName = senderName,
    content = content,
    timestamp = timestamp,
    isRead = false
)