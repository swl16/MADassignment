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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment.navigation.BottomNavBar
import androidx.compose.runtime.*
import com.example.assignment.database.UserDao
@Composable
fun HomeScreen(
    navController: NavController,
    userDao: UserDao, // <-- 1. Accept the database
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    // 2. Create state variables to hold the dynamic data
    var initials by remember { mutableStateOf("--") }
    var firstName by remember { mutableStateOf("User") }

    // 3. Fetch the data right when the screen opens
    LaunchedEffect(Unit) {
        val user = userDao.getLatestUser()
        if (user != null) {
            initials = user.fullName.take(2).uppercase()
            // Split the full name by spaces and just take their first name for a friendly greeting
            firstName = user.fullName.split(" ").firstOrNull() ?: "User"
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = {
            BottomNavBar(navController = navController, selectedIndex = 0)
        }
    ) { innerPadding: PaddingValues ->

        Box(modifier = Modifier.padding(innerPadding)) {
            // 4. Pass the dynamic text down to the content!
            HomeContent(
                initials = initials,
                firstName = firstName,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToNotifications = onNavigateToNotifications
            )
        }
    }
}

@Composable
fun HomeContent(
    initials: String, // <-- Accept initials
    firstName: String, // <-- Accept first name
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Pass initials to the header
        TopHeader(
            initials = initials,
            onAvatarClick = onNavigateToProfile,
            onBellClick = onNavigateToNotifications)

        // Pass first name to the greeting
        GreetingLayer(firstName = firstName)

        NextAppointmentCard()
        Spacer(modifier= Modifier.height(10.dp))
        QuickActionsGrid()
        Spacer(modifier = Modifier.height(20.dp))
        TodayMedicationCard(medicineName = null)
    }
}

@Composable
fun TopHeader(
    initials: String, // <-- Accept initials
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

            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFE5EDFF),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onAvatarClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials, // <-- DYNAMIC INITIALS HERE!
                        color = Color(0xFF1E50FF),
                        fontWeight = FontWeight.Bold
                    )
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
    ){
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
fun NextAppointmentCard(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E50FF)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = "NEXT APPOINTMENT", // Tweaked to all-caps like Figma
                fontSize = 12.sp,
                fontWeight = SemiBold,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Dr. Sarah Lim", // Updated to Figma name
                fontSize = 24.sp,
                fontWeight = Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically // Centers everything nicely
            ){
                Text(
                    text = "General Medicine",
                    fontSize = 12.sp,
                    fontWeight = SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                // THE FIX: The separator is now its own responsive element
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = "Tomorrow, 10:30 AM",
                    fontSize = 12.sp,
                    fontWeight = SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // THE FIX: Added a spacer here to push the "View details" box down!
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

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(
        // THE FIX: Chain .height() to the modifier to force it to be taller!
        modifier = modifier.height(90.dp),
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
fun QuickActionsGrid() {
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
                modifier = Modifier.weight(1f), // Takes 50% of the screen
            )

            QuickActionCard(
                title = "Medication",
                subtitle = "Set reminder",
                modifier = Modifier.weight(1f) // Takes 50% of the screen
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
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                title = "Medical records",
                subtitle = "View your files",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TodayMedicationCard(
    medicineName: String?, // The "?" means this can be null!
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
                // THE FIX: IntrinsicSize.Min lets the green line match the exact text height
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
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
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
