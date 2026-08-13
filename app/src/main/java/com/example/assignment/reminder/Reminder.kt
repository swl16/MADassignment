package com.example.assignment.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReminderScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)){
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
                onClick = { /* TODO: handle add reminder navigation */ },
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
        
        Text(text = "No reminders set yet.", color = Color.Gray, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderPreview() {
    ReminderScreen()
}
