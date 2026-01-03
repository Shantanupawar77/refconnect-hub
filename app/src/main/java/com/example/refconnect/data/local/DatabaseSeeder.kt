package com.example.refconnect.data.local

import com.example.refconnect.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DatabaseSeeder(private val database: AppDatabase) {

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        // Check if database is already seeded
        val existingUsers = database.userDao().getAllUsers().first()

        // Only seed if database is empty
        if (existingUsers.isEmpty()) {
            seedUsers()
            seedProfiles()
            seedReferrals()
            seedScreeningTests()
            seedQuestions()
        }
    }

    private suspend fun seedUsers() {
        val users = listOf(
            UserEntity(
                id = "user1",
                name = "John Doe",
                email = "john@example.com",
                password = "password123",
                role = "RECRUITER",
                profileImageUrl = null
            ),
            UserEntity(
                id = "user2",
                name = "Jane Smith",
                email = "jane@example.com",
                password = "password123",
                role = "RECRUITER",
                profileImageUrl = null
            ),
            UserEntity(
                id = "user3",
                name = "Mike Johnson",
                email = "mike@example.com",
                password = "password123",
                role = "SEEKER",
                profileImageUrl = null
            )
        )

        users.forEach { database.userDao().insert(it) }
    }

    private suspend fun seedProfiles() {
        val profiles = listOf(
            ProfileEntity(
                userId = "user1",
                company = "Google",
                experience = 5,
                techStack = "Kotlin,Android,Compose",
                leetcodeUrl = "https://leetcode.com/johndoe",
                githubUrl = "https://github.com/johndoe",
                hackathonExperience = "Won Google Hackathon 2023",
                achievements = "Published 5 Android apps",
                profileSetupComplete = true
            ),
            ProfileEntity(
                userId = "user2",
                company = "Amazon",
                experience = 7,
                techStack = "Java,Spring,AWS",
                codeforcesUrl = "https://codeforces.com/janesmith",
                githubUrl = "https://github.com/janesmith",
                achievements = "AWS Certified Solutions Architect",
                profileSetupComplete = true
            ),
            ProfileEntity(
                userId = "user3",
                company = "Microsoft",
                experience = 3,
                techStack = "C#,.NET,Azure",
                codechefUrl = "https://codechef.com/mike",
                profileSetupComplete = true
            )
        )

        profiles.forEach { database.profileDao().insert(it) }
    }

    private suspend fun seedReferrals() {
        val referrals = listOf(
            ReferralEntity(
                id = "ref1",
                postedByUserId = "user1",
                postedByUserName = "John Doe",
                company = "Google",
                role = "Senior Android Engineer",
                description = "We are looking for an experienced Android developer with strong Kotlin and Compose skills. Must have 3+ years of experience building production Android apps.",
                techStack = "Kotlin,Jetpack Compose,MVVM,Coroutines",
                experienceRequired = 3,
                location = "Remote",
                difficulty = "MEDIUM",
                techTag = "ANDROID",
                companyLogoUrl = null
            ),
            ReferralEntity(
                id = "ref2",
                postedByUserId = "user2",
                postedByUserName = "Jane Smith",
                company = "Amazon",
                role = "Backend Engineer",
                description = "Join our team to build scalable microservices. Strong knowledge of Spring Boot and AWS required. Experience with high-traffic systems is a plus.",
                techStack = "Java,Spring Boot,AWS,Microservices",
                experienceRequired = 5,
                location = "Seattle, WA",
                difficulty = "HARD",
                techTag = "BACKEND",
                companyLogoUrl = null
            ),
            ReferralEntity(
                id = "ref3",
                postedByUserId = "user3",
                postedByUserName = "Mike Johnson",
                company = "Microsoft",
                role = "Full Stack Developer",
                description = "Looking for a full stack developer to work on cloud-based solutions using .NET and Azure. Must be comfortable with both frontend and backend development.",
                techStack = "C#,.NET,Azure,React",
                experienceRequired = 2,
                location = "Redmond, WA",
                difficulty = "EASY",
                techTag = "FULLSTACK",
                companyLogoUrl = null
            ),
            ReferralEntity(
                id = "ref4",
                postedByUserId = "user1",
                postedByUserName = "John Doe",
                company = "Google",
                role = "iOS Developer",
                description = "We need an iOS developer proficient in Swift and SwiftUI to work on our mobile products. Strong understanding of iOS architecture patterns required.",
                techStack = "Swift,SwiftUI,iOS,UIKit",
                experienceRequired = 4,
                location = "Mountain View, CA",
                difficulty = "MEDIUM",
                techTag = "IOS",
                companyLogoUrl = null
            )
        )

        database.referralDao().insertAll(referrals)
    }

    private suspend fun seedScreeningTests() {
        val tests = listOf(
            ScreeningTestEntity(id = "test1", referralId = "ref1", passThreshold = 60),
            ScreeningTestEntity(id = "test2", referralId = "ref2", passThreshold = 70),
            ScreeningTestEntity(id = "test3", referralId = "ref3", passThreshold = 50),
            ScreeningTestEntity(id = "test4", referralId = "ref4", passThreshold = 60)
        )

        database.screeningTestDao().insertAll(tests)
    }

    private suspend fun seedQuestions() {
        // Test 1 - Android (Medium)
        val test1Questions = listOf(
            QuestionEntity(
                id = "q1_1",
                testId = "test1",
                questionText = "What is the primary purpose of Jetpack Compose?",
                options = """["Modern toolkit for building native Android UI","Database library","Networking library","Testing framework"]""",
                correctAnswerIndex = 0,
                orderIndex = 0
            ),
            QuestionEntity(
                id = "q1_2",
                testId = "test1",
                questionText = "Which lifecycle-aware component is used in MVVM architecture?",
                options = """["Fragment","Activity","ViewModel","Service"]""",
                correctAnswerIndex = 2,
                orderIndex = 1
            ),
            QuestionEntity(
                id = "q1_3",
                testId = "test1",
                questionText = "What is the main benefit of Kotlin Coroutines?",
                options = """["UI rendering","Asynchronous programming with structured concurrency","Database management","Network security"]""",
                correctAnswerIndex = 1,
                orderIndex = 2
            ),
            QuestionEntity(
                id = "q1_4",
                testId = "test1",
                questionText = "Which state management function is used in Compose for remembering state across recompositions?",
                options = """["LiveData","remember","SharedPreferences","Bundle"]""",
                correctAnswerIndex = 1,
                orderIndex = 3
            ),
            QuestionEntity(
                id = "q1_5",
                testId = "test1",
                questionText = "What does MVVM stand for in Android architecture?",
                options = """["Model-View-ViewModel","Multiple-Virtual-ViewManager","Model-Variant-ViewMode","Managed-View-ViewModel"]""",
                correctAnswerIndex = 0,
                orderIndex = 4
            )
        )

        // Test 2 - Backend (Hard)
        val test2Questions = listOf(
            QuestionEntity(
                id = "q2_1",
                testId = "test2",
                questionText = "In Spring Boot, what is the purpose of @Transactional annotation?",
                options = """["To mark a method as REST endpoint","To manage database transactions","To cache method results","To schedule tasks"]""",
                correctAnswerIndex = 1,
                orderIndex = 0
            ),
            QuestionEntity(
                id = "q2_2",
                testId = "test2",
                questionText = "Which AWS service is used for serverless compute?",
                options = """["EC2","Lambda","S3","RDS"]""",
                correctAnswerIndex = 1,
                orderIndex = 1
            ),
            QuestionEntity(
                id = "q2_3",
                testId = "test2",
                questionText = "What is the primary benefit of microservices architecture?",
                options = """["Faster compilation","Independent deployment and scaling","Reduced code size","Simpler testing"]""",
                correctAnswerIndex = 1,
                orderIndex = 2
            ),
            QuestionEntity(
                id = "q2_4",
                testId = "test2",
                questionText = "Which pattern is used to handle eventual consistency in distributed systems?",
                options = """["Singleton","Observer","Saga pattern","Factory"]""",
                correctAnswerIndex = 2,
                orderIndex = 3
            )
        )

        // Test 3 - Full Stack (Easy)
        val test3Questions = listOf(
            QuestionEntity(
                id = "q3_1",
                testId = "test3",
                questionText = "What is .NET?",
                options = """["A network protocol","A developer platform and runtime","A database","An operating system"]""",
                correctAnswerIndex = 1,
                orderIndex = 0
            ),
            QuestionEntity(
                id = "q3_2",
                testId = "test3",
                questionText = "What is Azure?",
                options = """["A programming language","Microsoft's cloud computing platform","A framework","A mobile OS"]""",
                correctAnswerIndex = 1,
                orderIndex = 1
            ),
            QuestionEntity(
                id = "q3_3",
                testId = "test3",
                questionText = "What is React primarily used for?",
                options = """["Backend services","Building user interfaces","Database management","Cloud hosting"]""",
                correctAnswerIndex = 1,
                orderIndex = 2
            )
        )

        // Test 4 - iOS (Medium)
        val test4Questions = listOf(
            QuestionEntity(
                id = "q4_1",
                testId = "test4",
                questionText = "What is SwiftUI?",
                options = """["A database framework","Apple's declarative UI framework","A testing tool","A networking library"]""",
                correctAnswerIndex = 1,
                orderIndex = 0
            ),
            QuestionEntity(
                id = "q4_2",
                testId = "test4",
                questionText = "Which language is primarily used for modern iOS development?",
                options = """["Java","Kotlin","Swift","C++"]""",
                correctAnswerIndex = 2,
                orderIndex = 1
            ),
            QuestionEntity(
                id = "q4_3",
                testId = "test4",
                questionText = "What is UIKit?",
                options = """["A graphics framework","Apple's imperative UI framework","A design tool","A cloud service"]""",
                correctAnswerIndex = 1,
                orderIndex = 2
            ),
            QuestionEntity(
                id = "q4_4",
                testId = "test4",
                questionText = "Which pattern is commonly used in iOS for managing app state?",
                options = """["MVVM","Singleton only","No patterns needed","MVC"]""",
                correctAnswerIndex = 0,
                orderIndex = 3
            )
        )

        val allQuestions = test1Questions + test2Questions + test3Questions + test4Questions
        database.questionDao().insertAll(allQuestions)
    }
}