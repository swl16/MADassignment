package com.example.assignment.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment.database.ReminderViewModel
import com.example.assignment.navigation.BottomNavBar

@Composable
fun ReminderScreen(navController: NavController, viewModel: ReminderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val reminders by viewModel.reminders.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8FAFF),
        bottomBar = {
            BottomNavBar(navController = navController, selectedIndex = 2)
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp)
        ){
            Spacer(modifier = Modifier.height(24.dp))

            //Header
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){

                Text(text = "Medication",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )

                IconButton(
                    onClick = { navController.navigate("addReminder") },
                    modifier = Modifier.background(Color(0xFF2563EB), CircleShape).size(40.dp)
                ){
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (reminders.isEmpty()) {
                Text(text = "No reminders set yet.", color = Color.Gray)
            } else {
                LazyColumn {
                    items(reminders) { reminder ->
                        ReminderCardItem(
                            time = reminder.time,
                            medicineName = reminder.medicineName,
                            dosageInfo = "${reminder.dosage} • ${reminder.instructions}",
                            statusText = if (reminder.isActive) "Active" else "Inactive",
                            isActive = reminder.isActive,
                            onCheckedChange = { isChecked ->
                                // Update status directly to the cloud
                                viewModel.saveReminder(reminder.copy(isActive = isChecked))
                            },
                            onClick = {
                                navController.navigate("reminder_details/${reminder.documentId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCardItem(
    time: String,
    medicineName: String,
    dosageInfo: String,
    statusText: String,
    isActive: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left orange status bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(100.dp) // Adjust based on your content height
                    .background(OrangeWarning)
            )

            Column(modifier = Modifier
                .padding(16.dp)
                .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = time, color = OrangeWarning, fontWeight = FontWeight.Bold)
                    Text(text = statusText, color = TextGray, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = medicineName, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = dosageInfo, color = TextGray, fontSize = 14.sp)
                    Switch(
                        checked = isActive,
                        onCheckedChange = onCheckedChange,
                        colors = SwitchDefaults.colors(checkedTrackColor = GreenSuccess)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderPreview() {
    ReminderScreen(navController = androidx.navigation.compose.rememberNavController())
}
