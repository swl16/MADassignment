package com.example.assignment.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen() {
    // These will show as "unused" until we build the UI boxes below!
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        ) {
        Spacer(modifier = Modifier.height(60.dp))
        // UI goes here
        Text(
            text = "New Account",
            fontSize = 22.sp,
            fontWeight = SemiBold,
            color = Color(0xFF1E50FF),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Full Name",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = {Text("eg.John",color=Color(0xFF8DA6FF))},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5EDFF),
                unfocusedContainerColor = Color(0xFFE5EDFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier=Modifier.height(10.dp))

        Text(
            text ="Password",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it},
            placeholder = { Text("********", color=Color(0xFF8DA6FF))},
            modifier= Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5EDFF),
                unfocusedContainerColor = Color(0xFFE5EDFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Email",
            fontSize = 14.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = email,
            onValueChange = { email = it},
            placeholder = { Text("eg. example123@example.com", color = Color(0xFF8DA6FF))},
            shape = RoundedCornerShape(12.dp),
            modifier= Modifier.fillMaxWidth(),

            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5EDFF),
                unfocusedContainerColor = Color(0xFFE5EDFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Mobile Number",
            fontSize = 12.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            placeholder = { Text("012-3456789", color = Color(0xFF8DA6FF))},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5EDFF),
                unfocusedContainerColor = Color(0xFFE5EDFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Date of Birth ",
            fontSize = 12.sp,
            fontWeight = SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it},
            placeholder = { Text(" DD/MM/YY ",color = Color(0xFF8DA6FF))},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5EDFF),
                unfocusedContainerColor = Color(0xFFE5EDFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "By continuing, please agree to our ",
            fontSize = 11.sp,
            fontWeight = SemiBold,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center

        )
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Terms of use and Privacy Policy",
            fontSize = 10.sp,
            fontWeight = SemiBold,
            color = Color(0xFF1E50FF),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { TODO()  },
            modifier = Modifier
                .fillMaxWidth()
                .size(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E50FF))
        ){
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

        }

        Spacer(modifier=Modifier.height(40.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp), // Gives it a little breathing room at the bottom
            horizontalArrangement = Arrangement.Center, // Centers the text and button together
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 11.sp, // Bumped slightly so it's readable
                fontWeight = SemiBold,
                color = Color.Gray
                // Notice I removed fillMaxWidth() and textAlign here!
                // The Row handles the centering now.
            )

            TextButton(
                onClick = { /* TODO: Navigate to Login */ },
                contentPadding = PaddingValues(0.dp) // Removes the invisible boundary
            ) {
                Text(
                    text = "Log In",
                    fontSize = 11.sp,
                    fontWeight = SemiBold, // Fixed your 'fontWidth' typo here!
                    color = Color(0xFF1E50FF) // Made it match your HealthCare blue
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}