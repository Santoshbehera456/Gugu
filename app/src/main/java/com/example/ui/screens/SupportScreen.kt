package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PlatformViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(viewModel: PlatformViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    var supportQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showFaqAccordion by remember { mutableStateOf(false) }

    // Auto scroll chat to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(bottom = 80.dp) // Nav bar space
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VIP CONCIERGE CHAT",
                        color = Color(0xFF1D1B20),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Real-time AI compliance concierge",
                        color = Color(0xFF49454F),
                        fontSize = 12.sp
                    )
                }

                // Clean chat icon
                IconButton(onClick = { viewModel.clearChatHistory() }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = Color(0xFFB3261E))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- FAQ EXPANDABLE PANEL ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFaqAccordion = !showFaqAccordion },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HelpCenter, contentDescription = null, tint = Color(0xFF6750A4))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Frequently Asked Compliance Questions", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Icon(
                            imageVector = if (showFaqAccordion) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF6750A4)
                        )
                    }

                    if (showFaqAccordion) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FaqItem(
                                q = "How do I secure mock withdrawals?",
                                a = "Complete your identity profile in the KYC tab. Then go to Wallet and submit a bank transfer request. Sandbox admins can instantly audit/payout from their dashboard."
                            )
                            FaqItem(
                                q = "Are skill games absolutely fair?",
                                a = "Absolutely. All games (Reflex Blitz, Math Sprint) operate on sub-millisecond hardware timers compiled client-side. No random generator or luck mechanics exist."
                            )
                            FaqItem(
                                q = "What is the KYC deposit ceiling?",
                                a = "Depositing more than $1,000 in a single transaction requires active identity verification to comply with Anti-Money Laundering regulatory checks."
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CHAT MESSAGE THREAD LIST ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                if (chatMessages.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = Color(0xFFE6E0E9), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Welcome to the VIP support desk.", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Arenabot is here to verify KYC, deposits, or answer compliance queries. Send a message below to start.",
                            color = Color(0xFF49454F),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(chatMessages) { message ->
                            val isUser = message.sender == "USER"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) Color(0xFFEADDFF) else Color(0xFFF3EDF7)
                                    ),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = message.message,
                                            color = if (isUser) Color(0xFF21005D) else Color(0xFF1D1B20),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- CHAT INPUT ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = supportQuery,
                    onValueChange = { supportQuery = it },
                    placeholder = { Text("Ask about limits, deposits, KYC...", color = Color(0xFF79747E)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6750A4),
                        unfocusedBorderColor = Color(0xFFCAC4D0),
                        focusedTextColor = Color(0xFF1D1B20),
                        unfocusedTextColor = Color(0xFF1D1B20)
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("support_chat_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF6750A4))
                        .clickable {
                            if (supportQuery.isNotBlank()) {
                                viewModel.sendSupportMessage(supportQuery)
                                supportQuery = ""
                            }
                        }
                        .testTag("support_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun FaqItem(q: String, a: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3EDF7), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(q, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(a, color = Color(0xFF49454F), fontSize = 11.sp, lineHeight = 16.sp)
    }
}
