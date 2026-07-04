package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(viewModel: PlatformViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf("Passport") }
    var documentNumber by remember { mutableStateOf("") }
    var mockImageCaptured by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(bottom = 80.dp) // Nav bar space
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // --- HEADER ---
            Text(
                text = "IDENTITY VERIFICATION (KYC)",
                color = Color(0xFF1D1B20),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Anti-Fraud & Regulatory Compliance Portal",
                color = Color(0xFF49454F),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CURRENT STATUS CARD (Professional Polish Theme) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = when {
                            currentUser?.isKycVerified == true -> Color(0xFF2E7D32).copy(alpha = 0.5f)
                            currentUser?.kycDocumentId?.isNotEmpty() == true -> Color(0xFFFFB300).copy(alpha = 0.5f)
                            else -> Color(0xFFCAC4D0)
                        },
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        currentUser?.isKycVerified == true -> Color(0xFFE2F0D9)
                        currentUser?.kycDocumentId?.isNotEmpty() == true -> Color(0xFFFFF3CD)
                        else -> Color(0xFFF3EDF7)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            currentUser?.isKycVerified == true -> Icons.Default.Verified
                            currentUser?.kycDocumentId?.isNotEmpty() == true -> Icons.Default.HourglassEmpty
                            else -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when {
                            currentUser?.isKycVerified == true -> Color(0xFF2E7D32)
                            currentUser?.kycDocumentId?.isNotEmpty() == true -> Color(0xFF856404)
                            else -> Color(0xFFB3261E)
                        },
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = when {
                                currentUser?.isKycVerified == true -> "COMPLIANCE STATUS: VERIFIED"
                                currentUser?.kycDocumentId?.isNotEmpty() == true -> "AUDIT PENDING"
                                else -> "IDENTITY VERIFICATION MANDATORY"
                            },
                            color = Color(0xFF1D1B20),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when {
                                currentUser?.isKycVerified == true -> "All withdraw options and high-volume limits are successfully unlocked."
                                currentUser?.kycDocumentId?.isNotEmpty() == true -> "Platform compliance officers are reviewing your submission."
                                else -> "Please upload legal identification documents to initiate cash withdrawals."
                            },
                            color = Color(0xFF49454F),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Show Form if not verified and not pending
            val isSubmitted = currentUser?.kycDocumentId?.isNotEmpty() == true && currentUser?.isKycVerified == false
            val isVerified = currentUser?.isKycVerified == true

            if (!isSubmitted && !isVerified) {
                Text(
                    text = "ENTER LEGAL DETAILS",
                    color = Color(0xFF1D1B20),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Name field
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Legal Name", color = Color(0xFF49454F)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6750A4),
                        unfocusedBorderColor = Color(0xFFCAC4D0),
                        focusedTextColor = Color(0xFF1D1B20),
                        unfocusedTextColor = Color(0xFF1D1B20),
                        focusedLabelColor = Color(0xFF6750A4),
                        unfocusedLabelColor = Color(0xFF49454F)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("kyc_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Document Selection Dropdown mock buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Passport", "Driver ID", "PAN/SSN").forEach { doc ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (documentType == doc) Color(0xFF6750A4) else Color(0xFFEADDFF))
                                .clickable { documentType = doc }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = doc,
                                color = if (documentType == doc) Color.White else Color(0xFF21005D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Document ID field
                OutlinedTextField(
                    value = documentNumber,
                    onValueChange = { documentNumber = it },
                    label = { Text("$documentType Number", color = Color(0xFF49454F)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6750A4),
                        unfocusedBorderColor = Color(0xFFCAC4D0),
                        focusedTextColor = Color(0xFF1D1B20),
                        unfocusedTextColor = Color(0xFF1D1B20),
                        focusedLabelColor = Color(0xFF6750A4),
                        unfocusedLabelColor = Color(0xFF49454F)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("kyc_doc_num_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mock Document Capture Zone
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3EDF7))
                        .border(
                            1.dp,
                            if (mockImageCaptured) Color(0xFF2E7D32) else Color(0xFFCAC4D0),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { mockImageCaptured = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (mockImageCaptured) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DOCUMENT CAPTURE ATTACHED", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("TAP TO CAPTURE ID SELFIE", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Simulated camera compliance capture", color = Color(0xFF79747E), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                Button(
                    onClick = {
                        viewModel.submitKycInfo(fullName, documentType, documentNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("kyc_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SUBMIT DOCUMENTS FOR AUDIT", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }

            } else {
                // Documents submitted state detail display
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
                        Icon(
                            imageVector = if (isVerified) Icons.Default.Gavel else Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isVerified) "COMPLIANCE APPROVAL RECOGNIZED" else "KYC QUEUE IN REVIEW",
                            color = Color(0xFF1D1B20),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isVerified)
                                "Your profile is certified. No further compliance steps are requested. Thank you for keeping SkillArena safe and fair."
                            else
                                "Our compliance audits typically process submissions within 60 seconds. In Sandbox mode, navigate to the Admin Dashboard (special admin account) to instantly approve this document review!",
                            color = Color(0xFF49454F),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
