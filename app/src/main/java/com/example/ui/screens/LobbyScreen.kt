package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PlatformViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    viewModel: PlatformViewModel,
    onPlayGame: (gameType: String, stake: Double) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val leaderboardReflex by viewModel.leaderboardReflex.collectAsState()
    val leaderboardMath by viewModel.leaderboardMath.collectAsState()

    var showNotifications by remember { mutableStateOf(false) }
    var selectedGameTab by remember { mutableStateOf("REFLEX") }

    val unreadNotifications = notifications.filter { !it.isRead }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // Leave space for bottom navigation
        ) {
            // --- TOP ARENA HEADER BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Icon Initials Circle - Professional Polish Theme
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFEADDFF), CircleShape)
                            .border(1.dp, Color(0xFFD0BCFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.username ?: "GP").take(2).uppercase(),
                            color = Color(0xFF21005D),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Welcome back,",
                            color = Color(0xFF49454F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.username ?: "GuestPlayer",
                                color = Color(0xFF1D1B20),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (currentUser?.isKycVerified == true) Icons.Default.Verified else Icons.Default.NewReleases,
                                contentDescription = null,
                                tint = if (currentUser?.isKycVerified == true) Color(0xFF2E7D32) else Color(0xFFB3261E),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Alert notification bell
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF3EDF7), RoundedCornerShape(12.dp))
                        .clickable {
                            showNotifications = true
                            viewModel.markNotificationsRead()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF1D1B20),
                        modifier = Modifier.size(20.dp)
                    )
                    if (unreadNotifications.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFFB3261E), CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
            }

            // --- WALLET ACCENT BALANCE CARD (Professional M3 Purple Gradient Card) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6750A4))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TOTAL ARENA BALANCE",
                            color = Color(0xFFEADDFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${"%.2f".format((currentUser?.balance ?: 0.0) + (currentUser?.bonusBalance ?: 0.0))}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(
                                text = "Cash: $${"%.2f".format(currentUser?.balance ?: 0.0)}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Bonus: $${"%.2f".format(currentUser?.bonusBalance ?: 0.0)}",
                                color = Color(0xFFEADDFF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Quick navigation to Wallet Screen (Styled as the light M3 accent Deposit action button)
                    Button(
                        onClick = { viewModel.navigateTo(Screen.WALLET) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEADDFF),
                            contentColor = Color(0xFF21005D)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null, tint = Color(0xFF21005D))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FUND", color = Color(0xFF21005D), fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ACTIVE GAMES LIST ---
            Text(
                text = "SKILL-BASED TOURNAMENTS",
                fontSize = 13.sp,
                color = Color(0xFF1D1B20),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Game 1: Reaction Blitz
            GameLobbyCard(
                title = "Reaction Blitz",
                description = "Tap as soon as the screen triggers green! Measures raw device click speeds down to sub-milliseconds. Higher speeds multiply stakes.",
                icon = Icons.Default.ElectricBolt,
                bannerAccent = Color(0xFF6750A4),
                activeStakes = listOf(10.0, 50.0, 100.0),
                onPlay = { stake -> onPlayGame("REFLEX", stake) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Game 2: Math Sprint
            GameLobbyCard(
                title = "Math Sprint",
                description = "Solve as many quick arithmetic problems as possible in 30 seconds. Accuracy and velocity yield up to 4.0x wagers.",
                icon = Icons.Default.Calculate,
                bannerAccent = Color(0xFF6750A4),
                activeStakes = listOf(10.0, 50.0, 100.0),
                onPlay = { stake -> onPlayGame("MATH", stake) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- LEADERBOARD & STATS SECTION ---
            Text(
                text = "LOBBY RANKINGS & RESULTS",
                fontSize = 13.sp,
                color = Color(0xFF1D1B20),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ranking Tabs Selector (Light M3 Background Style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Color(0xFFF3EDF7), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedGameTab == "REFLEX") Color(0xFFEADDFF) else Color.Transparent)
                        .clickable { selectedGameTab = "REFLEX" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Reflex Blitz",
                        color = if (selectedGameTab == "REFLEX") Color(0xFF21005D) else Color(0xFF49454F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedGameTab == "MATH") Color(0xFFEADDFF) else Color.Transparent)
                        .clickable { selectedGameTab = "MATH" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Math Sprint",
                        color = if (selectedGameTab == "MATH") Color(0xFF21005D) else Color(0xFF49454F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Leaderboard content List (Styled inside white/grey cards)
            val rankingList = if (selectedGameTab == "REFLEX") leaderboardReflex else leaderboardMath
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                if (rankingList.isEmpty()) {
                    Text(
                        text = "No tournament data. Be the first to secure a score!",
                        color = Color(0xFF49454F),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    )
                } else {
                    rankingList.take(5).forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${index + 1}",
                                    color = when (index) {
                                        0 -> Color(0xFFFFB300)
                                        1 -> Color(0xFF9E9E9E)
                                        2 -> Color(0xFF8D6E63)
                                        else -> Color(0xFF49454F)
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(30.dp)
                                )
                                Text(
                                    text = item.username,
                                    color = Color(0xFF1D1B20),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (selectedGameTab == "REFLEX") "${item.score} ms" else "${item.score} Points",
                                    color = Color(0xFF1D1B20),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Won $${"%.2f".format(item.rewardAmount)}",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (index < rankingList.take(5).size - 1) {
                            HorizontalDivider(color = Color(0xFFE6E0E9), thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- IN-APP NOTIFICATIONS PANEL SHEET ---
        if (showNotifications) {
            AlertDialog(
                onDismissRequest = { showNotifications = false },
                confirmButton = {
                    TextButton(onClick = { showNotifications = false }) {
                        Text("DISMISS", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF6750A4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SYSTEM ALERTS", color = Color(0xFF1D1B20), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        if (notifications.isEmpty()) {
                            Text(
                                text = "No notifications or inbox messages.",
                                color = Color(0xFF49454F),
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            LazyColumn {
                                itemsIndexed(notifications) { _, alert ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(alert.title, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(alert.message, color = Color(0xFF49454F), fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(color = Color(0xFFE6E0E9), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun GameLobbyCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bannerAccent: Color,
    activeStakes: List<Double>,
    onPlay: (stake: Double) -> Unit
) {
    var selectedStake by remember { mutableStateOf(activeStakes.first()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFEADDFF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF21005D), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = title, color = Color(0xFF1D1B20), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }

                // Fair game verification tag
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEADDFF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("100% SKILL", color = Color(0xFF21005D), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                color = Color(0xFF49454F),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stake Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("SELECT MATCH ENTRY STAKE", color = Color(0xFF49454F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        activeStakes.forEach { stake ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedStake == stake) Color(0xFF6750A4) else Color.White)
                                    .border(1.dp, if (selectedStake == stake) Color.Transparent else Color(0xFFCAC4D0), RoundedCornerShape(8.dp))
                                    .clickable { selectedStake = stake }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$${stake.toInt()}",
                                    color = if (selectedStake == stake) Color.White else Color(0xFF49454F),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Play Button
                Button(
                    onClick = { onPlay(selectedStake) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = "PLAY",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
