package com.example.assignment.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.assignment.database.Appointment
import com.example.assignment.database.AppointmentDao
import com.example.assignment.database.Reminder
import com.example.assignment.database.ReminderViewModel
import com.example.assignment.navigation.BottomNavBar
import com.example.assignment.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: UserViewModel,
    reminderViewModel: ReminderViewModel,
    loggedInUsername: String,
    appointmentDao: AppointmentDao,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAppointmentDetail: (Appointment) -> Unit
) {
    val appointments by appointmentDao.getAppointmentsForUser(loggedInUsername)
        .collectAsState(initial = emptyList())

    val latestAppointment = appointments.firstOrNull()
    val reminders by reminderViewModel.reminders.collectAsState()
    // 1. Observe the user state directly from the ViewModel
    val currentUser by viewModel.currentUser.collectAsState()

    // 2. Fetch the data right when the screen opens
    LaunchedEffect(loggedInUsername) {
        viewModel.loadUserProfile(loggedInUsername)
    }

    // 3. Derive UI values directly from the state (defaults to fallback text if loading)
    val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "--"
    val firstName = currentUser?.fullName?.split(" ")?.firstOrNull() ?: "User"
    val profilePictureUri = currentUser?.profilePictureUri

    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val currentTime = LocalTime.now()

    val nextMedication = reminders.filter{it.isActive}.filter{
        reminder ->
        try{
            LocalTime.parse(reminder.time,timeFormatter).isAfter(currentTime)
        }catch (e:Exception){
            false
        }
    }.minByOrNull { reminder ->
        try {
            LocalTime.parse(reminder.time, timeFormatter)
        }catch (e: Exception){
            LocalTime.MAX
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = {
            BottomNavBar(navController = navController, selectedIndex = 0)
        }
    ) { innerPadding: PaddingValues ->

        Box(modifier = Modifier.padding(innerPadding)) {
            // 4. Pass the dynamic text down to the content
            HomeContent(
                navController = navController,
                initials = initials,
                firstName = firstName,
                profilePictureUri = profilePictureUri,
                latestAppointment = latestAppointment,
                nextMedication = nextMedication,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToAppointmentDetail = onNavigateToAppointmentDetail
            )
        }
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    initials: String,
    firstName: String,
    profilePictureUri: String?,
    latestAppointment: Appointment?,
    nextMedication: Reminder?,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAppointmentDetail: (Appointment) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopHeader(
            initials = initials,
            profilePictureUri = profilePictureUri,
            onAvatarClick = onNavigateToProfile,
            onBellClick = onNavigateToNotifications
        )

        GreetingLayer(firstName = firstName)

        NextAppointmentCard(
            appointment = latestAppointment,
            onClick = {
                latestAppointment?.let { onNavigateToAppointmentDetail(it) }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        QuickActionsGrid(navController = navController)
        Spacer(modifier = Modifier.height(20.dp))

        TodayMedicationCard(medicineName = nextMedication?.medicineName,
            timeText = nextMedication?.time ?: "",
            contextText = if (nextMedication != null) "${nextMedication.dosage} • ${nextMedication.instructions}" else ""
        )
    }
}

@Composable
fun TopHeader(
    initials: String,
    profilePictureUri: String? = null, 
    onAvatarClick: () -> Unit = {},
    onBellClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "HealthCare",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E50FF)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFF0F5FF),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onBellClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🔔", fontSize = 16.sp)
                }
            }

            // shows the picture if set, otherwise initials
            if (profilePictureUri != null) {
                AsyncImage(
                    model = profilePictureUri,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE5EDFF), shape = androidx.compose.foundation.shape.CircleShape)
                        .clickable { onAvatarClick() },
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color(0xFFE5EDFF),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onAvatarClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            color = Color(0xFF1E50FF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingLayer(
    firstName: String // <-- Accept first name
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Hello, $firstName", // <-- DYNAMIC GREETING HERE!
            fontSize = 24.sp,
            fontWeight = SemiBold,
        )
        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "How can we help you today?",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun NextAppointmentCard(
    appointment: Appointment?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .clickable(enabled = appointment != null, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E50FF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "NEXT APPOINTMENT",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (appointment == null) {
                Text(
                    text = "No upcoming appointments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                return@Column
            }

            Text(
                text = appointment.doctorName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.specialty,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text("•", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                Text(
                    text = "${formatRelativeDate(appointment.date)}, ${appointment.time}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "View details  →",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatRelativeDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
        when (date) {
            LocalDate.now() -> "Today"
            LocalDate.now().plusDays(1) -> "Tomorrow"
            else -> dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(90.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // THE FIX: Now it fills the entire 120.dp height
                .padding(16.dp),
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionsGrid(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Quick actions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        // Pushes the grid of cards away from the "Quick actions" title
        Spacer(modifier = Modifier.height(16.dp))

        // --- ROW 1 ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                title = "Book appointment",
                subtitle = "Find a doctor",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("appointments")}// Takes 50% of the screen
            )

            QuickActionCard(
                title = "Medication",
                subtitle = "Set reminder",
                modifier = Modifier.weight(1f),
                onClick = {navController.navigate("addReminder")}// Takes 50% of the screen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ROW 2 ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                title = "Find nearby",
                subtitle = "Clinic or hospital",
                modifier = Modifier.weight(1f),
                onClick = {navController.navigate("nearby")}
            )

            QuickActionCard(
                title = "Medical records",
                subtitle = "View your files",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("records") }
            )
        }
    }
}

@Composable
fun TodayMedicationCard(
    medicineName: String?,
    timeText: String = "",
    contextText: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(bottom = 30.dp) // Gives space before the bottom navigation bar
    ) {
        // Section Title
        Text(
            text = "Today",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // THE LOGIC: If there is a medicine, show the green card.
        if (medicineName != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {

                    // 1. The Green Line
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF00BFA5)) // HealthCare Green
                    )

                    // 2. The Text Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "$timeText  •  $contextText",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = medicineName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Mark as taken button aligned to the right
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Mark as taken",
                                color = Color(0xFF1E50FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // THE LOGIC: If medicineName is null, show the "Empty State" card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F5FF)), // Very light blue
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No more medications for today! 🎉",
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
