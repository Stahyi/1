@file:Suppress("UNCHECKED_CAST")

package breakbadhabits.android.app.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import breakbadhabits.android.app.R
import breakbadhabits.android.app.compose.screen.AppSettingsScreen
import breakbadhabits.android.app.compose.screen.HabitCreationScreen
import breakbadhabits.android.app.compose.screen.HabitEditingScreen
import breakbadhabits.android.app.compose.screen.HabitEventCreationScreen
import breakbadhabits.android.app.compose.screen.HabitEventEditingScreen
import breakbadhabits.android.app.compose.screen.HabitEventsScreen
import breakbadhabits.android.app.compose.screen.HabitScreen
import breakbadhabits.android.app.compose.screen.HabitsAppWidgetConfigEditingScreen
import breakbadhabits.android.app.compose.screen.HabitsAppWidgetsScreen
import breakbadhabits.android.app.compose.screen.HabitsScreen
import breakbadhabits.android.app.compose.screen.NewHabitsScreen
import breakbadhabits.android.app.utils.NightModeManager
import breakbadhabits.android.app.utils.composeViewModel
import breakbadhabits.android.app.utils.get
import breakbadhabits.android.data.actual
import breakbadhabits.compose.theme.BreakBadHabitsTheme
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val nightModeManager: NightModeManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Activity)
        setContent {
            BreakBadHabitsTheme(
                isDarkTheme = when (nightModeManager.mode) {
                    NightModeManager.Mode.NIGHT -> true
                    NightModeManager.Mode.NOT_NIGHT -> false
                    NightModeManager.Mode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "habits"
                ) {
                    composable(route = "appSettings") {
                        AppSettingsScreen(
                            nightModeManager = get(),
                            openWidgetSettings = {
                                navController.navigate("habitsAppWidgets")
                            }
                        )
                    }

                    composable(route = "habitCreation") {
                        HabitCreationScreen(
                            habitCreationViewModel = composeViewModel(),
                            dateTimeFormatter = get(),
                            habitIconResources = get(),
                            onFinished = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "habitEditing?habitId={habitId}",
                        arguments = listOf(navArgument("habitId") { type = NavType.IntType })
                    ) {
                        val habitId = it.arguments!!.getInt("habitId")
                        HabitEditingScreen(
                            habitEditingViewModel = composeViewModel(habitId),
                            habitIconResources = get(),
                            alertDialogManager = get(),
                            onFinished = {
                                navController.popBackStack()
                            },
                            habitDeletionViewModel = composeViewModel(habitId),
                            onHabitDeleted = {
                                navController.popBackStack(route = "habits", inclusive = false)
                            }
                        )
                    }

                    composable(
                        route = "habitEventCreation?habitId={habitId}",
                        arguments = listOf(navArgument("habitId") { type = NavType.IntType })
                    ) {
                        val habitId = it.arguments!!.getInt("habitId")
                        HabitEventCreationScreen(
                            habitViewModel = composeViewModel(habitId),
                            habitEventCreationViewModel = composeViewModel(habitId),
                            dateTimeFormatter = get(),
                            onFinished = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "habitEventEditing?habitEventId={habitEventId}",
                        arguments = listOf(navArgument("habitEventId") { type = NavType.IntType })
                    ) {
                        val habitEventId = it.arguments!!.getInt("habitEventId")
                        HabitEventEditingScreen(
                            habitEventEditingViewModel = composeViewModel(habitEventId),
                            dateTimeFormatter = get(),
                            alertDialogManager = get(),
                            onFinished = {
                                navController.popBackStack()
                            },
                            onHabitEventDeleted = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "habitsAppWidgetConfigEditing?configId={configId}",
                        arguments = listOf(navArgument("configId") { type = NavType.IntType })
                    ) {
                        val configId = it.arguments!!.getInt("configId")
                        HabitsAppWidgetConfigEditingScreen(
                            habitsAppWidgetConfigEditingViewModel = composeViewModel(configId),
                            alertDialogManager = get(),
                            onFinished = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("habitsAppWidgets") {
                        HabitsAppWidgetsScreen(
                            widgetsViewModel = composeViewModel(),
                            openHabitAppWidgetConfigEditing = { configId ->
                                navController.navigate("habitsAppWidgetConfigEditing?configId=$configId")
                            }
                        )
                    }

                    composable(
                        route = "habit?habitId={habitId}",
                        arguments = listOf(navArgument("habitId") { type = NavType.IntType })
                    ) {
                        val habitId = it.arguments!!.getInt("habitId")
                        HabitScreen(
                            habitAnalyzeViewModel = composeViewModel(habitId),
                            habitViewModel = composeViewModel(habitId),
                            habitEventsViewModel = composeViewModel(habitId),
                            abstinenceTimeFormatter = get(),
                            dateTimeFormatter = get(),
                            openHabitEventCreation = {
                                navController.navigate("habitEventCreation?habitId=$habitId")
                            },
                            openHabitEventEditing = { habitEventId ->
                                navController.navigate("habitEventEditing?habitEventId=$habitEventId")
                            },
                            openHabitEditing = {
                                navController.navigate("habitEditing?habitId=$habitId")
                            },
                            habitIconResources = get(),
                            showALlEvents = {
                                navController.navigate("habitEvents?habitId=$habitId")
                            }
                        )
                    }

                    composable(
                        route = "habitEvents?habitId={habitId}",
                        arguments = listOf(navArgument("habitId") { type = NavType.IntType })
                    ) {
                        val habitId = it.arguments!!.getInt("habitId")
                        HabitEventsScreen(
                            habitViewModel = composeViewModel(habitId),
                            habitEventsViewModel = composeViewModel(habitId),
                            dateTimeFormatter = get(),
                            habitIconResources = get(),
                            openHabitEventEditing = { habitEventId ->
                                navController.navigate("habitEventEditing?habitEventId=$habitEventId")
                            }
                        )
                    }

                    composable("habits") {
                        NewHabitsScreen(
                            habitIconResources = get(),
                            abstinenceTimeFormatter = get(),
                            openHabit = { habitId ->
                                navController.navigate("habit?habitId=${habitId.actual()}")
                            },
                            openHabitEventCreation = { habitId ->
                                navController.navigate("habitEventCreation?habitId=${habitId.actual()}")
                            },
                            openHabitCreation = {
                                navController.navigate("habitCreation")
                            },
                            openSettings = {
                                navController.navigate("appSettings")
                            }
                        )
                    }
                }
            }
        }
    }
}
