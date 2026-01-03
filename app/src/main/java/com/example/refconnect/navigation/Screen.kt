package com.example.refconnect.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ProfileSetup : Screen("profile_setup")
    object RoleResolver : Screen("role_resolver")
    object MyOpportunities : Screen("my_opportunities")
    object ReferralFeed : Screen("referral_feed")
    object Home : Screen("home")
    object ReferralDetails : Screen("referral_details/{referralId}") {
        fun createRoute(referralId: String) = "referral_details/$referralId"
    }
    object ScreeningTest : Screen("screening_test/{referralId}") {
        fun createRoute(referralId: String) = "screening_test/$referralId"
    }
    object TestResult : Screen("test_result/{passed}") {
        fun createRoute(passed: Boolean) = "test_result/$passed"
    }
    object ConnectionRequest : Screen("connection_request/{referralId}") {
        fun createRoute(referralId: String) = "connection_request/$referralId"
    }
    object Connections : Screen("connections")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{chatId}") {
        fun createRoute(chatId: String) = "chat/$chatId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object CreateReferral : Screen("create_referral")
    object ApplicantList : Screen("applicant_list/{referralId}") {
        fun createRoute(referralId: String) = "applicant_list/$referralId"
    }
}