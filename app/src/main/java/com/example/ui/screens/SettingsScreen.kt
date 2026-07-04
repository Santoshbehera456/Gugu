package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PlatformViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: PlatformViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    var dailyLimit by remember { mutableStateOf(currentUser?.dailyDepositLimit?.toString() ?: "500.0") }
    var playtimeLimit by remember { mutableStateOf(currentUser?.dailyPlayTimeLimitMinutes?.toString() ?: "120") }
    var triggerSelfExclusion by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(bottom = 80.dp) // Nav bar padding
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- TITLE ---
            Text(
                text = "CONTROLS & COMPLIANCE",
                color = Color(0xFF1D1B20),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Configure limits, self-exclusion, and codes",
                color = Color(0xFF49454F),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- RESPONSIBLE GAMING PANEL ---
            Text(
                text = "RESPONSIBLE GAMING REGULATED TOOLS",
                color = Color(0xFF6750A4),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Card container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Customize your limits to ensure a safe, fun, and controlled gaming atmosphere. Changes are locked to prevent impulse betting.",
                        color = Color(0xFF49454F),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily deposit limit input
                    OutlinedTextField(
                        value = dailyLimit,
                        onValueChange = { dailyLimit = it },
                        label = { Text("Daily Deposit Limit ($)", color = Color(0xFF49454F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_daily_limit_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Daily Playtime minutes input
                    OutlinedTextField(
                        value = playtimeLimit,
                        onValueChange = { playtimeLimit = it },
                        label = { Text("Daily Play Session Timer (Minutes)", color = Color(0xFF49454F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_time_limit_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Self exclusion toggle row (Soft red warning context)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFDE8E8), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF8B4B4), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("24H SELF-EXCLUSION", color = Color(0xFFB3261E), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Hard lock blocks match entry for 24 hours.", color = Color(0xFF49454F), fontSize = 11.sp)
                        }
                        Switch(
                            checked = triggerSelfExclusion,
                            onCheckedChange = { triggerSelfExclusion = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFB3261E),
                                checkedTrackColor = Color(0xFFB3261E).copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.LightGray
                            ),
                            modifier = Modifier.testTag("settings_exclusion_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save Limits Button
                    Button(
                        onClick = {
                            val limitVal = dailyLimit.toDoubleOrNull() ?: 500.0
                            val timeVal = playtimeLimit.toIntOrNull() ?: 120
                            viewModel.setResponsibleGamingLimits(limitVal, timeVal, triggerSelfExclusion)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_save_limits_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SAVE & UPDATE CONTROLS", color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- REFERRAL CAMPAIGN PANEL ---
            Text(
                text = "REFERRAL & REWARDS ENGINE",
                color = Color(0xFF6750A4),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Share the passion! Every referral awards you and your friend 150.0 bonus play credits when they secure a profile verification.",
                        color = Color(0xFF49454F),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display Referral Code
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3EDF7), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF6750A4), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.referralCode ?: "ARENA_CODE_PENDING",
                            color = Color(0xFF1D1B20),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Your Custom Referral Invite Key", color = Color(0xFF49454F), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- OVERRIDE SANDBOX CONTROLLER (UX POLISH) ---
            Text(
                text = "SANDBOX DEVELOPER TOOLS",
                color = Color(0xFF6750A4),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Because this runs in a sandbox, you can instantly toggle Admin roles for yourself below to test document approval queues and pending cashouts.",
                        color = Color(0xFF49454F),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Your System Role:", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = if (currentUser?.isAdmin == true) "PLATFORM EXECUTIVE ADMIN" else "STANDARD PLAYER",
                                color = if (currentUser?.isAdmin == true) Color(0xFF6750A4) else Color(0xFF49454F),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleAdminAccessOverride() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEADDFF),
                                contentColor = Color(0xFF21005D)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF6750A4)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("TOGGLE ROLE", color = Color(0xFF21005D), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LOGOUT BUTTON ---
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("settings_logout_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT ARENA PROFILE", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun ColorxFFFF3D00() = Color(0xFFFF3D00)
