package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(viewModel: PlatformViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // Dialog state variables
    var depositAmount by remember { mutableStateOf("") }
    var depositMethod by remember { mutableStateOf("UPI") }
    var depositDetails by remember { mutableStateOf("") }

    var withdrawAmount by remember { mutableStateOf("") }
    var withdrawDestination by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(bottom = 80.dp) // Nav bar gap
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // --- TITLE ---
            Text(
                text = "SECURE WALLET",
                color = Color(0xFF1D1B20),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Regulated sandbox financial services",
                color = Color(0xFF49454F),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- LARGE BALANCE DISPLAY CARD (Professional M3 Gradient Purple Card) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6750A4))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AVAILABLE TO PLAY",
                        color = Color(0xFFEADDFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$${"%.2f".format((currentUser?.balance ?: 0.0) + (currentUser?.bonusBalance ?: 0.0))}",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("WITHDRAWABLE CASH", color = Color(0xFFEADDFF), fontSize = 10.sp)
                            Text("$${"%.2f".format(currentUser?.balance ?: 0.0)}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PROMO CREDITS", color = Color(0xFFEADDFF), fontSize = 10.sp)
                            Text("$${"%.2f".format(currentUser?.bonusBalance ?: 0.0)}", color = Color(0xFFEADDFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons Row
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showDepositDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEADDFF),
                                contentColor = Color(0xFF21005D)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("wallet_deposit_trigger"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFF21005D))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DEPOSIT", color = Color(0xFF21005D), fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { showWithdrawDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("wallet_withdraw_trigger"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WITHDRAW", color = Color.White, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TRANSACTIONS LOG TITLE ---
            Text(
                text = "TRANSACTION LEDGER HISTORY",
                color = Color(0xFF1D1B20),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction log Column
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF79747E), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No recorded transaction history in this account.",
                            color = Color(0xFF49454F),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(transactions) { tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3EDF7), RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Dynamic icons based on transaction type
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            when (tx.type) {
                                                "DEPOSIT", "GAME_REWARD", "REFERRAL_BONUS" -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                                else -> Color(0xFFB3261E).copy(alpha = 0.15f)
                                            },
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (tx.type) {
                                            "DEPOSIT" -> Icons.Default.ArrowDownward
                                            "WITHDRAWAL" -> Icons.Default.ArrowUpward
                                            "GAME_ENTRY" -> Icons.Default.SportsEsports
                                            "GAME_REWARD" -> Icons.Default.EmojiEvents
                                            else -> Icons.Default.CardGiftcard
                                        },
                                        contentDescription = null,
                                        tint = when (tx.type) {
                                            "DEPOSIT", "GAME_REWARD", "REFERRAL_BONUS" -> Color(0xFF2E7D32)
                                            else -> Color(0xFFB3261E)
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = when (tx.type) {
                                            "DEPOSIT" -> "Deposited Funds"
                                            "WITHDRAWAL" -> "Withdrawal Payout"
                                            "GAME_ENTRY" -> "Match Entry Wager"
                                            "GAME_REWARD" -> "Tournament Payout"
                                            else -> "Referral Incentive"
                                        },
                                        color = Color(0xFF1D1B20),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tx.description,
                                        color = Color(0xFF49454F),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (tx.type == "DEPOSIT" || tx.type == "GAME_REWARD" || tx.type == "REFERRAL_BONUS") "+" else "-"}$${"%.2f".format(tx.amount)}",
                                    color = if (tx.type == "DEPOSIT" || tx.type == "GAME_REWARD" || tx.type == "REFERRAL_BONUS") Color(0xFF2E7D32) else Color(0xFFB3261E),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Transaction Status Badge
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (tx.status) {
                                                "COMPLETED" -> Color(0xFF2E7D32).copy(alpha = 0.12f)
                                                "PENDING" -> Color(0xFFFFB300).copy(alpha = 0.12f)
                                                else -> Color(0xFFB3261E).copy(alpha = 0.12f)
                                            },
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tx.status,
                                        color = when (tx.status) {
                                            "COMPLETED" -> Color(0xFF2E7D32)
                                            "PENDING" -> Color(0xFF856404)
                                            else -> Color(0xFFB3261E)
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SIMULATED DEPOSIT GATEWAY DIALOG ---
        if (showDepositDialog) {
            AlertDialog(
                onDismissRequest = { showDepositDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = depositAmount.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                viewModel.deposit(amount, "$depositMethod: $depositDetails")
                                showDepositDialog = false
                                depositAmount = ""
                                depositDetails = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SECURE DEPOSIT", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDepositDialog = false }) {
                        Text("CANCEL", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text("ADD ARENA FUNDS (SANDBOX)", color = Color(0xFF1D1B20), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "Deposits exceeding $1,000 will enforce an automated compliance block requiring active KYC Verification.",
                            color = Color(0xFF6750A4),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = depositAmount,
                            onValueChange = { depositAmount = it },
                            label = { Text("Amount ($)", color = Color(0xFF49454F)) },
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
                            modifier = Modifier.fillMaxWidth().testTag("wallet_deposit_amount_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Deposit Method Selector Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("UPI", "CARD", "BANK").forEach { method ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (depositMethod == method) Color(0xFF6750A4) else Color(0xFFEADDFF))
                                        .clickable { depositMethod = method }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = method,
                                        color = if (depositMethod == method) Color.White else Color(0xFF21005D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = depositDetails,
                            onValueChange = { depositDetails = it },
                            label = { Text(if (depositMethod == "UPI") "UPI Virtual ID" else if (depositMethod == "CARD") "Card Number (16-digit)" else "Bank IFSC/Account", color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedTextColor = Color(0xFF1D1B20),
                                unfocusedTextColor = Color(0xFF1D1B20),
                                focusedLabelColor = Color(0xFF6750A4),
                                unfocusedLabelColor = Color(0xFF49454F)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("wallet_deposit_details_input")
                        )
                    }
                },
                containerColor = Color.White
            )
        }

        // --- SIMULATED WITHDRAWAL GATEWAY DIALOG ---
        if (showWithdrawDialog) {
            AlertDialog(
                onDismissRequest = { showWithdrawDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = withdrawAmount.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                viewModel.withdraw(amount, withdrawDestination)
                                showWithdrawDialog = false
                                withdrawAmount = ""
                                withdrawDestination = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("INITIATE WITHDRAW", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWithdrawDialog = false }) {
                        Text("CANCEL", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text("SECURE WITHDRAWAL GATEWAY", color = Color(0xFF1D1B20), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "Withdrawals are strictly audited in compliance with Anti-Money Laundering (AML) directives. Active KYC Verification is MANDATORY.",
                            color = Color(0xFFB3261E),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = withdrawAmount,
                            onValueChange = { withdrawAmount = it },
                            label = { Text("Amount to Withdraw ($)", color = Color(0xFF49454F)) },
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
                            modifier = Modifier.fillMaxWidth().testTag("wallet_withdraw_amount_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = withdrawDestination,
                            onValueChange = { withdrawDestination = it },
                            label = { Text("UPI ID or Bank IBAN Details", color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedTextColor = Color(0xFF1D1B20),
                                unfocusedTextColor = Color(0xFF1D1B20),
                                focusedLabelColor = Color(0xFF6750A4),
                                unfocusedLabelColor = Color(0xFF49454F)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("wallet_withdraw_details_input")
                        )
                    }
                },
                containerColor = Color.White
            )
        }
    }
}
