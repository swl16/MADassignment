package com.example.assignment.authentication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingItem(
    val title: String,
    val subtitle: String,
    val iconVector: ImageVector? = null,
    val isCustomMedKit: Boolean = false
)

@Composable
fun StartingScreen(onTapToContinue: () -> Unit = {}) {
    val pages = listOf(
        OnboardingItem(
            title = "HealthCare",
            subtitle = "Your Personal Healthcare\nCompanion",
            isCustomMedKit = true
        ),
        OnboardingItem(
            title = "Easy Appointments",
            subtitle = "Schedule & reschedule visits with top doctors and specialists in seconds",
            iconVector = Icons.Outlined.CalendarMonth
        ),
        OnboardingItem(
            title = "Medical Records",
            subtitle = "Securely organize and view your clinical reports and medical history",
            iconVector = Icons.Outlined.FolderShared
        ),
        OnboardingItem(
            title = "Medication Reminders",
            subtitle = "Stay on track with customizable medication dosages and daily alerts",
            iconVector = Icons.Outlined.Alarm
        ),
        OnboardingItem(
            title = "Nearby Services",
            subtitle = "Locate nearby hospitals, clinics, and emergency facilities easily",
            iconVector = Icons.Outlined.LocationOn
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onTapToContinue()
                }
            }

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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) { page ->
                val item = pages[page]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        if (item.isCustomMedKit) {
                            MedKitIcon(modifier = Modifier.size(42.dp))
                        } else if (item.iconVector != null) {
                            Icon(
                                imageVector = item.iconVector,
                                contentDescription = item.title,
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = item.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D53D4), // Matched the logo blue
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.subtitle,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { index ->
                    PagerDot(isActive = pagerState.currentPage == index)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (pagerState.currentPage == pages.size - 1) "Tap to get started" else "Tap or swipe to continue",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(250.dp))
        }
    }
}

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