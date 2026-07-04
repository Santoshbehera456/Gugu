package com.example.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PlatformViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GameScreen(
    viewModel: PlatformViewModel,
    gameType: String,
    stakeAmount: Double
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        if (gameType == "REFLEX") {
            PlayReflexGame(viewModel, currentUser?.username ?: "GuestPlayer", stakeAmount)
        } else {
            PlayMathGame(viewModel, currentUser?.username ?: "GuestPlayer", stakeAmount)
        }
    }
}

// ==========================================
// GAME 1: REACTION BLITZ
// ==========================================
@Composable
fun PlayReflexGame(
    viewModel: PlatformViewModel,
    username: String,
    stakeAmount: Double
) {
    var gameState by remember { mutableStateOf("READY") } // READY, WAITING, TRIGGERED, FINISHED, FOUL
    var startTime by remember { mutableStateOf(0L) }
    var reactionTime by remember { mutableStateOf(0L) }
    var opponentTime by remember { mutableStateOf(0L) }
    
    val coroutineScope = rememberCoroutineScope()

    // Generate opponent metrics
    LaunchedEffect(Unit) {
        opponentTime = Random.nextLong(210, 320)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOURNAMENT: REFLEX SPEED", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Stake: $${stakeAmount.toInt()} Credits", color = Color(0xFF1D1B20), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEADDFF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("MULTIPLIER UP TO 3.5x", color = Color(0xFF21005D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Main game interactive click zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    when (gameState) {
                        "READY" -> Color(0xFFF3EDF7)
                        "WAITING" -> Color(0xFFFFF1F2) // Soft Rose Warning
                        "TRIGGERED" -> Color(0xFFDCFCE7) // Tap Now Green
                        "FINISHED" -> Color(0xFFF3EDF7)
                        else -> Color(0xFFFDE8E8) // Foul Soft Red
                    }
                )
                .border(
                    width = 2.dp,
                    color = when (gameState) {
                        "READY" -> Color(0xFF6750A4)
                        "WAITING" -> Color(0xFFF43F5E)
                        "TRIGGERED" -> Color(0xFF15803D)
                        "FINISHED" -> Color(0xFFCAC4D0)
                        else -> Color(0xFFE11D48)
                    },
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable {
                    when (gameState) {
                        "READY" -> {
                            gameState = "WAITING"
                            coroutineScope.launch {
                                val delayTime = Random.nextLong(1500, 3500)
                                delay(delayTime)
                                if (gameState == "WAITING") {
                                    gameState = "TRIGGERED"
                                    startTime = System.currentTimeMillis()
                                }
                            }
                        }

                        "WAITING" -> {
                            // Clicked early! Foul start
                            gameState = "FOUL"
                        }

                        "TRIGGERED" -> {
                            reactionTime = System.currentTimeMillis() - startTime
                            gameState = "FINISHED"
                            viewModel.submitGameResult("REFLEX", stakeAmount, reactionTime.toInt())
                        }
                    }
                }
                .testTag("game_click_zone"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = when (gameState) {
                        "READY" -> "TAP TO ARM TRIGGER"
                        "WAITING" -> "WAIT FOR GREEN COLOR..."
                        "TRIGGERED" -> "CLICK NOW!!!"
                        "FINISHED" -> "COMPUTING RATINGS..."
                        else -> "FOUL START DETECTED!"
                    },
                    color = when (gameState) {
                        "TRIGGERED" -> Color(0xFF14532D)
                        "WAITING" -> Color(0xFF9F1239)
                        "FOUL" -> Color(0xFF9F1239)
                        else -> Color(0xFF1D1B20)
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when (gameState) {
                        "READY" -> "Prepares the digital neural signal clock."
                        "WAITING" -> "Do not tap! Early trigger disqualifies wagers."
                        "TRIGGERED" -> "SPEED MATTERS! GO GO GO!"
                        "FINISHED" -> "Transferring high-resolution metrics to compliance ledger."
                        else -> "You clicked too early. Wager is forfeit. Practice discipline!"
                    },
                    color = when (gameState) {
                        "TRIGGERED" -> Color(0xFF15803D)
                        "WAITING" -> Color(0xFFBE123C)
                        "FOUL" -> Color(0xFFBE123C)
                        else -> Color(0xFF49454F)
                    },
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Live Rival Telemetry Display (Simulated Real-time gameplay feel)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("REAL-TIME MULTIPLAYER MATCH RIVAL", color = Color(0xFF49454F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF2E7D32), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active Opponent: ReflexViper", color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        text = if (gameState == "FINISHED") "Rival Time: ${opponentTime}ms" else "Rival: READY",
                        color = Color(0xFF6750A4),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (gameState == "FINISHED") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "YOUR REACTION: ${reactionTime} ms",
                        color = Color(0xFF1D1B20),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (reactionTime < opponentTime) "🏆 YOU DEFEATED REFLEXVIPER!" else "Rival was faster. Keep practicing!",
                        color = if (reactionTime < opponentTime) Color(0xFF2E7D32) else Color(0xFFB3261E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Return Button
        Button(
            onClick = { viewModel.navigateTo(Screen.LOBBY) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("LOBBY RETURN", color = Color.White)
        }
    }
}

// ==========================================
// GAME 2: MATH SPRINT
// ==========================================
@Composable
fun PlayMathGame(
    viewModel: PlatformViewModel,
    username: String,
    stakeAmount: Double
) {
    var gameState by remember { mutableStateOf("READY") } // READY, PLAYING, FINISHED
    var timerSeconds by remember { mutableIntStateOf(30) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var rivalScore by remember { mutableIntStateOf(0) }

    // Math Question Struct
    var op1 by remember { mutableIntStateOf(0) }
    var op2 by remember { mutableIntStateOf(0) }
    var operation by remember { mutableStateOf("+") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var answerOptions by remember { mutableStateOf(emptyList<Int>()) }

    fun generateQuestion() {
        op1 = Random.nextInt(3, 30)
        op2 = Random.nextInt(2, 12)
        operation = listOf("+", "-", "*").random()
        correctAnswer = when (operation) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            else -> op1 * op2
        }

        val optionsSet = mutableSetOf(correctAnswer)
        while (optionsSet.size < 4) {
            val offset = Random.nextInt(-10, 10)
            if (offset != 0) optionsSet.add(correctAnswer + offset)
        }
        answerOptions = optionsSet.toList().shuffled()
    }

    // Rival pacing ticker
    LaunchedEffect(gameState) {
        if (gameState == "PLAYING") {
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
                // Rival scores points at a standard pace
                if (Random.nextDouble() < 0.35) {
                    rivalScore++
                }
            }
            gameState = "FINISHED"
            viewModel.submitGameResult("MATH", stakeAmount, score)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOURNAMENT: MATH BLITZ", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Stake: $${stakeAmount.toInt()} Credits", color = Color(0xFF1D1B20), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEADDFF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Timer: ${timerSeconds}s", color = Color(0xFF21005D), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Gameplay state switcher
        when (gameState) {
            "READY" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 24.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("30-SECOND SPEED SOLVER", color = Color(0xFF1D1B20), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Solve arithmetic equations rapid-fire. Win high score payouts. No cheats allowed.",
                            color = Color(0xFF49454F),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                generateQuestion()
                                gameState = "PLAYING"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("START TOURNAMENT MATCH", color = Color.White, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            "PLAYING" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Question box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFF3EDF7), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$op1 $operation $op2 = ?",
                            color = Color(0xFF1D1B20),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer Options Grid (4 options)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            answerOptions.take(2).forEach { option ->
                                CardAnswerButton(
                                    text = option.toString(),
                                    onClick = {
                                        if (option == correctAnswer) {
                                            score++
                                        }
                                        generateQuestion()
                                    }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            answerOptions.takeLast(2).forEach { option ->
                                CardAnswerButton(
                                    text = option.toString(),
                                    onClick = {
                                        if (option == correctAnswer) {
                                            score++
                                        }
                                        generateQuestion()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            "FINISHED" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 24.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOURNAMENT MATCH FINISHED", color = Color(0xFF6750A4), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your Total Correct: $score Equations", color = Color(0xFF1D1B20), fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("Simulated Rival Score: $rivalScore Equations", color = Color(0xFF49454F), fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (score > rivalScore) "🏆 WINNER! MATCH BOUNTY AWARDED!" else "Rival won this run. Train hard!",
                            color = if (score > rivalScore) Color(0xFF2E7D32) else Color(0xFFB3261E),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live stats footer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("YOUR SCORE: $score Points", color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Rival Score: $rivalScore Points", color = Color(0xFF6750A4), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exit
        Button(
            onClick = { viewModel.navigateTo(Screen.LOBBY) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("LOBBY RETURN", color = Color.White)
        }
    }
}

@Composable
fun RowScope.CardAnswerButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("math_option_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF1D1B20),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
