package com.cyberboyone.lecturenotes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.*

data class NavigationItem(
    val route: String,
    val label: String,
    val iconEmoji: String
)

val BottomNavItems = listOf(
    NavigationItem(Routes.HOME, "Home", "🏠"),
    NavigationItem(Routes.FACULTIES, "Explore", "🧭"),
    NavigationItem(Routes.DOWNLOADS, "Downloads", "📥"),
    NavigationItem(Routes.PROFILE, "Profile", "👤")
)

@Composable
fun UniCourseBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    downloadsCount: Int = 0
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItems.forEach { item ->
                val isSelected = when (item.route) {
                    Routes.HOME -> currentRoute == Routes.HOME
                    Routes.FACULTIES -> currentRoute == Routes.FACULTIES || currentRoute?.startsWith("departments") == true || currentRoute?.startsWith("courses/") == true
                    Routes.DOWNLOADS -> currentRoute == Routes.DOWNLOADS
                    Routes.PROFILE -> currentRoute == Routes.PROFILE
                    else -> currentRoute == item.route
                }

                val indicatorBg by animateColorAsState(
                    targetValue = if (isSelected) ElectricViolet.copy(alpha = 0.12f) else Color.Transparent,
                    label = "navIndicator"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) ElectricViolet else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "navText"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(indicatorBg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) onNavigate(item.route)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box {
                            Text(text = item.iconEmoji, fontSize = 20.sp)
                            if (item.route == Routes.DOWNLOADS && downloadsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-4).dp)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(ElectricViolet),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (downloadsCount > 9) "9+" else "$downloadsCount",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        if (isSelected) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
