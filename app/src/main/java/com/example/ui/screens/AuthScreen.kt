package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PlatformViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: PlatformViewModel) {
    var isLogin by remember { mutableStateOf(true) }
    
    // Form Inputs
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Visual Palette: Professional Polish Theme
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
    )
    val accentBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6750A4), Color(0xFF7E60CC))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Visual Logo / Brand Header - Professional Polish Theme
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = "SkillArena Logo",
                tint = Color(0xFF21005D),
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFEADDFF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .border(1.5.dp, Color(0xFFCAC4D0), RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SKILL ARENA",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1D1B20),
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp
            )
            Text(
                text = "High-Performance Skill Wagers",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF49454F),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Card - Professional Polish Theme
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLogin) "EXECUTIVE LOGIN" else "REGISTRATION GATE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6750A4),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username", color = Color(0xFF49454F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F)
                        ),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6750A4)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_username_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!isLogin) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Email Input
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedTextColor = Color(0xFF1D1B20),
                                unfocusedTextColor = Color(0xFF1D1B20),
                                focusedLabelColor = Color(0xFF6750A4),
                                unfocusedLabelColor = Color(0xFF49454F)
                            ),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF6750A4)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Secure Password", color = Color(0xFF49454F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF49454F)
                        ),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6750A4)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF49454F)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!isLogin) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Referral Code
                        OutlinedTextField(
                            value = referralCode,
                            onValueChange = { referralCode = it },
                            label = { Text("Referral Invite Code (Optional)", color = Color(0xFF49454F)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                focusedTextColor = Color(0xFF1D1B20),
                                unfocusedTextColor = Color(0xFF1D1B20),
                                focusedLabelColor = Color(0xFF6750A4),
                                unfocusedLabelColor = Color(0xFF49454F)
                            ),
                            leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF6750A4)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_referral_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Entering a code awards +150 bonus credits on registration.",
                            color = Color(0xFF6750A4).copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Action Button
                    Button(
                        onClick = {
                            if (isLogin) {
                                viewModel.login(username, password)
                            } else {
                                viewModel.register(username, email, password, referralCode)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentBrush)
                            .testTag("auth_submit_button")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLogin) "ENTER ARENA" else "REGISTER ACCOUNT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Switch Mode Click
                    TextButton(onClick = { isLogin = !isLogin }) {
                        Text(
                            text = if (isLogin) "New to Arena? Register a secure profile" else "Already verified? Authenticate login",
                            color = Color(0xFF49454F),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legality & Responsible Gaming Disclaimer
            Text(
                text = "SkillArena complies with international skill gaming laws. Balance is simulated for test deployment. Play responsibly.",
                color = Color(0xFF79747E),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 16.sp
            )
        }
    }
}
