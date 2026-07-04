package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PlatformViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: PlatformViewModel) {
    val allUsers by viewModel.allUsers.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var userSearchQuery by remember { mutableStateOf("") }
    
    // Broadcast notification state
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMsg by remember { mutableStateOf("") }

    // --- AGGREGATE PLATFORM METRICS REPORTS ---
    val totalDeposits = transactions.filter { it.type == "DEPOSIT" && it.status == "COMPLETED" }.sumOf { it.amount }
    val totalWagers = transactions.filter { it.type == "GAME_ENTRY" }.sumOf { it.amount }
    val totalPayouts = transactions.filter { it.type == "GAME_REWARD" }.sumOf { it.amount }
    val totalWithdrawals = transactions.filter { it.type == "WITHDRAWAL" && it.status == "COMPLETED" }.sumOf { it.amount }
    val netHouseMargin = totalWagers - totalPayouts

    val filteredUsers = allUsers.filter {
        it.username.contains(userSearchQuery, ignoreCase = true) ||
        it.email.contains(userSearchQuery, ignoreCase = true)
    }

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
                text = "EXECUTIVE COMPLIANCE CONSOLE",
                color = Color(0xFF1D1B20),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "System reports, transactions, and audit queues",
                color = Color(0xFF49454F),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- PLATFORM FINANCIAL METRICS ROW (Professional Polish theme) ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "PLATFORM DEPOSITS",
                    value = "$${"%.1f".format(totalDeposits)}",
                    color = Color(0xFF2E7D32)
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "NET ARENA PROFIT",
                    value = "$${"%.1f".format(netHouseMargin)}",
                    color = Color(0xFF6750A4)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "TOTAL PLAY WAGERS",
                    value = "$${"%.1f".format(totalWagers)}",
                    color = Color(0xFF1D1B20)
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "TOTAL PLAY PAYOUTS",
                    value = "$${"%.1f".format(totalPayouts)}",
                    color = Color(0xFFB3261E)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- KYC REVIEW QUEUE ---
            Text(
                text = "PENDING KYC APPROVAL QUEUE",
                color = Color(0xFF6750A4),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val pendingKycUsers = allUsers.filter { it.kycDocumentId.isNotEmpty() && !it.isKycVerified }
            if (pendingKycUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No pending KYC verification requests in backlog.", color = Color(0xFF49454F), fontSize = 12.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    pendingKycUsers.forEach { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Player: ${user.username}", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Document ID details:", color = Color(0xFF49454F), fontSize = 11.sp)
                                Text(user.kycDocumentId, color = Color(0xFF1D1B20), fontSize = 12.sp)
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = { viewModel.approveKyc(user.username) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text("APPROVE KYC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.rejectKyc(user.username) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text("REJECT KYC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- WITHDRAWAL APPROVAL AUDIT QUEUE ---
            Text(
                text = "PENDING WITHDRAWAL DISPATCH QUEUE",
                color = Color(0xFF6750A4),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (pendingWithdrawals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No pending withdrawal dispatch wagers.", color = Color(0xFF49454F), fontSize = 12.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    pendingWithdrawals.forEach { tx ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("User: ${tx.userId}", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("$${"%.2f".format(tx.amount)}", color = Color(0xFFB3261E), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Destination routing details: ${tx.description}", color = Color(0xFF49454F), fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = { viewModel.processAdminWithdrawalApproval(tx, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text("DISPATCH PAYMENT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.processAdminWithdrawalApproval(tx, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text("REJECT & REFUND", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BROADCAST SYSTEM ALERTS ---
            Text(
                text = "EMERGENCY BROADCAST NOTIFICATIONS CENTER",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Title", color = Color(0xFF49454F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_broadcast_title")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        label = { Text("Alert Message Body", color = Color(0xFF49454F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("admin_broadcast_msg")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank() && broadcastMsg.isNotBlank()) {
                                viewModel.triggerSystemNotificationBlast(broadcastTitle, broadcastMsg)
                                broadcastTitle = ""
                                broadcastMsg = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("admin_broadcast_submit"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("BROADCAST SYSTEM ALERT", color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- USER ACCOUNTS MANAGEMENT ---
            Text(
                text = "USER ACCOUNTS DIRECTORY",
                color = Color(0xFF6750A4),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Search input
            OutlinedTextField(
                value = userSearchQuery,
                onValueChange = { userSearchQuery = it },
                placeholder = { Text("Search by username or email...", color = Color(0xFF79747E)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6750A4),
                    unfocusedBorderColor = Color(0xFFCAC4D0),
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20)
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF79747E)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_user_search"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredUsers.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(user.username, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(user.email, color = Color(0xFF49454F), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(if (user.isKycVerified) Color(0xFF2E7D32) else Color(0xFFB3261E), RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (user.isKycVerified) "Verified Profile" else "Unverified Profile",
                                    color = if (user.isKycVerified) Color(0xFF2E7D32) else Color(0xFFB3261E),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Suspended/Active action
                        Button(
                            onClick = { viewModel.toggleUserBan(user.username) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isBanned) Color(0xFF2E7D32) else Color(0xFFB3261E)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (user.isBanned) "UNSUSPEND" else "SUSPEND",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier.border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color(0xFF49454F), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}
