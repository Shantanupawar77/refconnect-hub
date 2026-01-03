package com.example.refconnect.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.refconnect.navigation.Screen
import com.example.refconnect.viewmodel.*

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    // Home will be dynamically resolved
    object MyOpportunities : BottomNavItem(Screen.MyOpportunities.route, Icons.Default.Home, "Home")
    object ReferralFeed : BottomNavItem(Screen.ReferralFeed.route, Icons.Default.Home, "Home")
    object Connections : BottomNavItem(Screen.Connections.route, Icons.Default.Group, "Connections")
    object Chat : BottomNavItem(Screen.ChatList.route, Icons.Default.Chat, "Chat")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
}

@Composable
fun MainScreen(
    viewModelFactory: ViewModelFactory,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // Create ViewModels with factory
    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val connectionViewModel: ConnectionViewModel = viewModel(factory = viewModelFactory)
    val chatViewModel: ChatViewModel = viewModel(factory = viewModelFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
    val screeningTestViewModel: ScreeningTestViewModel = viewModel(factory = viewModelFactory)

    // Determine home route based on user role
    val currentUserRole = profileViewModel.currentUser?.role
    val homeItem = if (currentUserRole == com.example.refconnect.model.UserRole.RECRUITER) {
        BottomNavItem.MyOpportunities
    } else {
        BottomNavItem.ReferralFeed
    }

    val items = listOf(
        homeItem,
        BottomNavItem.Connections,
        BottomNavItem.Chat,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.RoleResolver.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.RoleResolver.route) {
                RoleResolverScreen(
                    profileViewModel = profileViewModel,
                    onNavigateToMyOpportunities = {
                        navController.navigate(Screen.MyOpportunities.route) {
                            popUpTo(Screen.RoleResolver.route) { inclusive = true }
                        }
                    },
                    onNavigateToReferralFeed = {
                        navController.navigate(Screen.ReferralFeed.route) {
                            popUpTo(Screen.RoleResolver.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.MyOpportunities.route) {
                MyOpportuniScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToCreateReferral = {
                        navController.navigate(Screen.CreateReferral.route)
                    },
                    onNavigateToApplicants = { referralId ->
                        navController.navigate(Screen.ApplicantList.createRoute(referralId))
                    }
                )
            }

            composable(Screen.ReferralFeed.route) {
                RefFeedScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToReferralDetails = { referralId ->
                        navController.navigate(Screen.ReferralDetails.createRoute(referralId))
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToReferralDetails = { referralId ->
                        navController.navigate(Screen.ReferralDetails.createRoute(referralId))
                    },
                    onNavigateToCreateReferral = {
                        navController.navigate(Screen.CreateReferral.route)
                    },
                    onNavigateToApplicants = { referralId ->
                        navController.navigate(Screen.ApplicantList.createRoute(referralId))
                    }
                )
            }

            composable(Screen.ApplicantList.route) { backStackEntry ->
                val referralId = backStackEntry.arguments?.getString("referralId") ?: ""
                ApplicantListScreen(
                    referralId = referralId,
                    homeViewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onDeleteSuccess = {
                        navController.navigate(Screen.MyOpportunities.route) {
                            popUpTo(Screen.MyOpportunities.route) { inclusive = true }
                        }
                    },
                    onNavigateToConnection = { applicantName ->
                        navController.navigate(Screen.Connections.route) {
                            popUpTo(Screen.MyOpportunities.route)
                        }
                    }
                )
            }

            composable(Screen.CreateReferral.route) {
                CreateReferralScreen(
                    onNavigateBack = { navController.popBackStack() },
                    homeViewModel = homeViewModel
                )
            }

            composable(Screen.ReferralDetails.route) { backStackEntry ->
                val referralId = backStackEntry.arguments?.getString("referralId") ?: ""
                RefDetailsScreen(
                    referralId = referralId,
                    homeViewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTest = { refId ->
                        navController.navigate(Screen.ScreeningTest.createRoute(refId))
                    }
                )
            }

            composable(Screen.ScreeningTest.route) { backStackEntry ->
                val referralId = backStackEntry.arguments?.getString("referralId") ?: ""
                ScreeningTestScreen(
                    referralId = referralId,
                    screeningTestViewModel = screeningTestViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResult = { passed ->
                        navController.navigate(Screen.TestResult.createRoute(passed))
                    }
                )
            }

            composable(Screen.TestResult.route) { backStackEntry ->
                val passed = backStackEntry.arguments?.getString("passed")?.toBoolean() ?: false
                val referralId = navController.previousBackStackEntry
                    ?.arguments?.getString("referralId") ?: ""
                TestResultScreen(
                    passed = passed,
                    screeningTestViewModel = screeningTestViewModel,
                    onNavigateToConnectionRequest = {
                        navController.navigate(Screen.ConnectionRequest.createRoute(referralId)) {
                            popUpTo(Screen.ReferralFeed.route)
                        }
                    },
                    onNavigateBack = {
                        navController.navigate(Screen.ReferralFeed.route) {
                            popUpTo(Screen.ReferralFeed.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ConnectionRequest.route) { backStackEntry ->
                val referralId = backStackEntry.arguments?.getString("referralId") ?: ""
                ConnReqScreen(
                    referralId = referralId,
                    connectionViewModel = connectionViewModel,
                    onNavigateToConnections = {
                        navController.navigate(Screen.Connections.route) {
                            popUpTo(Screen.ReferralFeed.route)
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.ReferralFeed.route) {
                            popUpTo(Screen.ReferralFeed.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Connections.route) {
                ConnectionsScreen(
                    connectionViewModel = connectionViewModel,
                    chatViewModel = chatViewModel,
                    onNavigateToChat = { chatId ->
                        navController.navigate(Screen.Chat.createRoute(chatId))
                    }
                )
            }

            composable(Screen.ChatList.route) {
                ChatListScreen(
                    chatViewModel = chatViewModel,
                    onNavigateToChat = { chatId ->
                        navController.navigate(Screen.Chat.createRoute(chatId))
                    }
                )
            }

            composable(Screen.Chat.route) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                ChatScreen(
                    chatId = chatId,
                    chatViewModel = chatViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfile.route)
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }
        }
    }
}