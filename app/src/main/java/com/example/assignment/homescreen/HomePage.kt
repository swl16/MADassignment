package com.example.assignment.homescreen
import androidx.compose.foundation.background
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text(text = "⌂", fontSize = 24.sp, color = if (selectedTab == 0) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Home", fontSize = 10.sp, color = if (selectedTab == 0) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text(text = "🕒", fontSize = 20.sp, color = if (selectedTab == 1) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Appointments", fontSize = 10.sp, color = if (selectedTab == 1) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text(text = "⏰", fontSize = 20.sp, color = if (selectedTab == 2) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Reminders", fontSize = 10.sp, color = if (selectedTab == 2) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Text(text = "☰", fontSize = 20.sp, color = if (selectedTab == 3) Color(0xFF1E50FF) else Color.Gray) },
                    label = { Text("Records", fontSize = 10.sp, color = if (selectedTab == 3) Color(0xFF1E50FF) else Color.Gray) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding: PaddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopHeader()
            GreetingLayer()
            NextAppointmentCard()
            Spacer(modifier= Modifier.height(10.dp))
            QuickActionsGrid()
            Spacer(modifier = Modifier.height(20.dp))
            TodayMedicationCard(medicineName = null)
        }
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. The Brand Name
        Text(
            text = "HealthCare",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E50FF)
        )

        // 2. The Icons Group
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Puts a gap between the two circles
        ) {
            // Notification Bell Background
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFF0F5FF),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Using a unicode bell so we don't need the icon library!
                    Text("🔔", fontSize = 16.sp)
                }
            }

            // Profile Badge Background
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color(0xFFE5EDFF),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "WL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E50FF)
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingLayer(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ){
        Text(
            text = "Hello, User",
            fontSize = 24.sp,
            fontWeight = SemiBold,
        )
        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "How can we help you today? ",
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}