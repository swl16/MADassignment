package com.example.assignment.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.appointment.AppointmentDetailScreen
import com.example.assignment.appointment.AppointmentHistoryScreen
import com.example.assignment.appointment.BookingConfirmedScreen
import com.example.assignment.appointment.DoctorProfileScreen
import com.example.assignment.appointment.SelectDateTimeScreen
import com.example.assignment.appointment.sampleDoctors
import com.example.assignment.authentication.LoginPage
import com.example.assignment.authentication.SignUpScreen
import com.example.assignment.authentication.StartingScreen
import com.example.assignment.database.AppDatabase
import com.example.assignment.database.Appointment
import com.example.assignment.database.RecordRepository
import com.example.assignment.homescreen.HomeScreen
import com.example.assignment.nearby.NearbyScreen
import com.example.assignment.profile.EditProfileScreen
import com.example.assignment.profile.ProfileScreen
import com.example.assignment.reminder.AddReminderScreen
import com.example.assignment.reminder.ReminderDetailsScreen
import com.example.assignment.reminder.ReminderScreen
import com.example.assignment.viewmodel.EmergencyContactViewModel
import com.example.assignment.viewmodel.RecordViewModel
import com.example.assignment.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. Get the Context and open the Database
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val appointmentDao = db.appointmentDao()
    val emergencyContactDao = db.emergencyContactDao()
    val recordDao = db.recordDao()

    val reminderDao = db.reminderDao()
    val reminderRepository = remember{ com.example.assignment.database.ReminderRepository(reminderDao)}

    val reminderViewModel: com.example.assignment.database.ReminderViewModel = viewModel(
        factory = object: ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return com.example.assignment.database.ReminderViewModel(reminderRepository) as T
            }
        }
    )

    // 2. Safely create the UserViewModel passing the DAO
    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(userDao) as T
            }
        }
    )

    // 3. Safely create the EmergencyContactViewModel passing the DAO
    val emergencyContactViewModel: EmergencyContactViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EmergencyContactViewModel(emergencyContactDao) as T
            }
        }
    )

    val recordViewModel: RecordViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecordViewModel(RecordRepository(recordDao, context)) as T
            }
        }
    )

    val appointments = remember { mutableStateListOf<Appointment>() }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var activeUsername by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "starting") {

        composable("starting") {
            StartingScreen {
                navController.navigate("login") {
                    popUpTo("starting") { inclusive = true }
                }
            }
        }

        composable("login") {
            LoginPage(
                viewModel = userViewModel,
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onNavigateToHome = { username ->
                    activeUsername = username
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                viewModel = userViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = { username ->
                    activeUsername = username
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("forgot_password") {
            com.example.assignment.authentication.ForgotPasswordScreen(
                viewModel = userViewModel,
                onBackClick = { navController.popBackStack() },
                onUserVerified = { username ->
                    navController.navigate("reset_password/$username")
                }
            )
        }

        composable(
            route = "reset_password/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            com.example.assignment.authentication.SetPasswordScreen(
                viewModel = userViewModel,
                username = username,
                onBackClick = { navController.popBackStack() },
                onPasswordCreated = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = userViewModel,
                loggedInUsername = activeUsername,
                appointmentDao = appointmentDao,
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToAppointmentDetail = { appointment ->
                    selectedAppointment = appointment
                    navController.navigate("appointment_detail")
                }
            )
        }

        composable("nearby"){
            NearbyScreen(navController, onNavigateBack = { navController.popBackStack() })
        }

        composable("reminders") {
            ReminderScreen(navController, reminderViewModel)
        }

        composable("addReminder") {
            AddReminderScreen(navController, reminderViewModel)
        }

        composable(
            route = "reminder_details/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("documentId") ?: ""
            ReminderDetailsScreen(navController, reminderViewModel, docId)
        }

        composable("appointments") {
            com.example.assignment.appointment.AppointmentMain(
                navController = navController,
                onViewProfile = { doctorIndex ->
                    navController.navigate("doctor_profile/$doctorIndex")
                },
                onBookNow = { doctor ->
                    val index = sampleDoctors.indexOf(doctor)
                    navController.navigate("doctor_profile/$index")
                }
            )
        }

        composable(
            route = "doctor_profile/{doctorIndex}",
            arguments = listOf(navArgument("doctorIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("doctorIndex") ?: 0
            DoctorProfileScreen(
                doctor = sampleDoctors[index],
                onNavigateBack = { navController.popBackStack() },
                onBookAppointment = { navController.navigate("select_date_time/$index") },
                onMessageClinic = { /* TODO if in scope */ }
            )
        }

        composable("records") {
            com.example.assignment.records.RecordsMain(navController, activeUsername, recordViewModel)
        }

        composable("profile") {
            ProfileScreen(
                viewModel = userViewModel,
                username = activeUsername,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("edit_profile") },
                onNavigateToSettings = { navController.navigate("notification_settings") },
                onNavigateToEmergency = { navController.navigate("emergency_contact") },
                onNavigateToAppointmentHistory = { navController.navigate("appointment_history") },
                onNavigateToChangePassword = { navController.navigate("change_password") },
                onLogOut = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable("change_password") {
            com.example.assignment.authentication.SetPasswordScreen(
                viewModel = userViewModel,
                username = activeUsername,
                onBackClick = { navController.popBackStack() },
                onPasswordCreated = { navController.popBackStack() }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                viewModel = userViewModel,
                username = activeUsername,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("notifications") {
            com.example.assignment.notification.NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("notification_settings") {
            com.example.assignment.profile.NotificationSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("emergency_contact") {
            com.example.assignment.profile.EmergencyContactScreen(
                viewModel = emergencyContactViewModel, // CHANGED
                username = activeUsername,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("appointment_history"){
            AppointmentHistoryScreen(
                onBack = { navController.popBackStack() },
                onBookAppointment = { navController.navigate("appointments") },
                onAppointmentClick = { appointment ->
                    selectedAppointment = appointment
                    navController.navigate("appointment_detail")
                },
                appointmentDao = appointmentDao,
                username = activeUsername
            )
        }

        composable("appointment_detail") {
            val scope = rememberCoroutineScope()

            val doctor = sampleDoctors.find {
                it.name.equals(selectedAppointment?.doctorName, ignoreCase = true)
            } ?: sampleDoctors.first()

            AppointmentDetailScreen(
                navController = navController,
                appointment = selectedAppointment,
                doctor = doctor,
                onCancelAppointment = { toCancel ->
                    scope.launch {
                        appointmentDao.delete(toCancel)
                    }
                }
            )
        }

        composable("reschedule_appointment/{appointmentId}",
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId")
            val scope = rememberCoroutineScope()
            val appointment = appointments.find { it.id == appointmentId }

            if (appointment != null) {
                val matchingDoctor = sampleDoctors.find {
                    it.name.equals(appointment.doctorName, ignoreCase = true)
                } ?: sampleDoctors.first()

                val initialDate = try {
                    LocalDate.parse(appointment.date, DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
                } catch (e: Exception) {
                    LocalDate.now()
                }

                SelectDateTimeScreen(
                    doctor = matchingDoctor,
                    initialDate = initialDate,
                    initialTime = appointment.time,
                    isRescheduling = true,
                    onNavigateBack = { navController.popBackStack() },
                    onConfirm = { newDate, newTime ->
                        val formattedDate = newDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))

                        val index = appointments.indexOfFirst { it.id == appointment.id }
                        if (index != -1) {
                            appointments[index] = appointment.copy(
                                date = formattedDate,
                                time = newTime
                            )
                        }

                        scope.launch {
                            val updatedAppointment = appointment.copy(
                                date = formattedDate,
                                time = newTime
                            )
                            appointmentDao.update(updatedAppointment)
                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        composable(
            route = "select_date_time/{doctorIndex}",
            arguments = listOf(navArgument("doctorIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("doctorIndex") ?: 0
            val doctor = sampleDoctors[index]
            val scope = rememberCoroutineScope()

            SelectDateTimeScreen(
                doctor = doctor,
                onNavigateBack = { navController.popBackStack() },
                onConfirm = { date, time ->
                    scope.launch {
                        val newAppointment = Appointment(
                            username = activeUsername,
                            doctorName = doctor.name,
                            specialty = doctor.specialty,
                            date = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
                            time = time
                        )
                        val newId = appointmentDao.insert(newAppointment)
                        navController.navigate("booking_confirmed/${newId.toInt()}") {
                            popUpTo("appointments") { inclusive = false }
                        }
                    }
                }
            )
        }

        composable(
            route = "booking_confirmed/{appointmentId}",
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: 0
            BookingConfirmedScreen(
                appointmentId = appointmentId,
                appointmentDao = appointmentDao,
                onViewNotification = { navController.navigate("notifications") },
                onBackToHome = {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                }
            )
        }
    }
}

@Composable
fun BottomNavBar(navController: androidx.navigation.NavController, selectedIndex: Int) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("home", "⌂", "Home"),
            Triple("appointments", "🕒", "Appointments"),
            Triple("reminders", "⏰", "Reminders"),
            Triple("records", "☰", "Records")
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    if (selectedIndex != index) {
                        navController.navigate(item.first) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(
                        text = item.second,
                        fontSize = if (index == 0) 24.sp else 20.sp,
                        color = if (selectedIndex == index) Color(0xFF1E50FF) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.third,
                        fontSize = 10.sp,
                        color = if (selectedIndex == index) Color(0xFF1E50FF) else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}