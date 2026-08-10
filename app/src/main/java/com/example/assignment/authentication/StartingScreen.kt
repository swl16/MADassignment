package com.example.assignment.authentication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartingScreen(onTapToContinue: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onTapToContinue() }
    ) {

        // --- LAYER 1: PERFECT CANVAS WAVES ---
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height

            val path1 = Path().apply {
                moveTo(0f, height * 0.6f)
                quadraticBezierTo(width * 0.4f, height * 0.1f, width, height * 0.8f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path1, color = Color(0xFF8DA6FF))

            val path2 = Path().apply {
                moveTo(0f, height * 0.8f)
                quadraticBezierTo(width * 0.5f, height * 0.4f, width, height * 0.95f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path2, color = Color(0xFF4A7DFF))

            val path3 = Path().apply {
                moveTo(0f, height)
                quadraticBezierTo(width * 0.3f, height * 0.6f, width, height * 0.9f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path3, color = Color(0xFF1E50FF))
        }

        // --- LAYER 2: THE MAIN CONTENT COLUMN ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(180.dp))

            // THE SOLID BLUE LOGO BOX
            Surface(
                modifier = Modifier.size(85.dp), // Matched proportion
                shape = RoundedCornerShape(24.dp), // Slightly rounder to match image
                color = Color(0xFF0D53D4) // Deepened the blue slightly to match the icon screenshot
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Custom drawn MedKit!
                    MedKitIcon(modifier = Modifier.size(42.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HealthCare",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D53D4), // Matched the logo blue
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your Personal Healthcare\nCompanion",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PagerDot(isActive = true)
                PagerDot(isActive = false)
                PagerDot(isActive = false)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Tap to continue",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(250.dp))
        }
    }
}

// Custom composable drawing the exact MedKit shape
// Custom composable drawing the exact MedKit shape
// Custom composable drawing the exact MedKit shape
// Custom composable drawing the exact MedKit shape
// Custom composable drawing the exact MedKit shape
@Composable
fun MedKitIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeW = 2.5.dp.toPx()
        val halfStroke = strokeW / 2f

        // 1. Bag Proportions
        val bagWidth = size.width
        val bagHeight = size.height * 0.60f
        val bagTop = size.height * 0.28f
        val bagCornerRadius = CornerRadius(size.width * 0.15f, size.width * 0.15f)

        // 2. Handle Proportions (Narrowed to match perfectly)
        val handleWidth = size.width * 0.28f
        val handleHeight = size.height * 0.14f
        val handleLeft = (size.width - handleWidth) / 2f
        val handleRight = handleLeft + handleWidth
        val handleTop = bagTop - handleHeight
        val handleCornerRadius = size.width * 0.08f

        // 3. Draw Handle
        val handlePath = Path().apply {
            moveTo(handleLeft, bagTop)
            lineTo(handleLeft, handleTop + handleCornerRadius)
            quadraticBezierTo(handleLeft, handleTop, handleLeft + handleCornerRadius, handleTop)
            lineTo(handleRight - handleCornerRadius, handleTop)
            quadraticBezierTo(handleRight, handleTop, handleRight, handleTop + handleCornerRadius)
            lineTo(handleRight, bagTop)
        }
        drawPath(
            path = handlePath,
            color = Color.White,
            style = Stroke(width = strokeW)
        )

        // 4. Draw Main Bag
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(0f, bagTop),
            size = Size(bagWidth, bagHeight),
            cornerRadius = bagCornerRadius,
            style = Stroke(width = strokeW)
        )

        // 5. Draw Horizontal Divider
        val lineY = bagTop + bagHeight * 0.35f
        drawLine(
            color = Color.White,
            start = Offset(halfStroke, lineY),
            end = Offset(bagWidth - halfStroke, lineY),
            strokeWidth = strokeW
        )

        // 6. The Perfect "十" (Ten) Crossing
        // Extends EQUALLY above and below the horizontal line now!
        val crossHalfHeight = bagHeight * 0.14f
        drawLine(
            color = Color.White,
            start = Offset(size.width / 2f, lineY - crossHalfHeight), // Goes UP perfectly
            end = Offset(size.width / 2f, lineY + crossHalfHeight),   // Goes DOWN perfectly
            strokeWidth = strokeW,
            cap = StrokeCap.Round // Smooth, soft edges like the first photo
        )
    }
}

@Composable
fun PagerDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(
                color = if (isActive) Color(0xFF8DA6FF) else Color(0xFFE5EDFF),
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StartingScreenPreview() {
    StartingScreen()
}