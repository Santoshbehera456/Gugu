package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PlatformViewModel
import com.example.ui.viewmodel.PlatformViewModelFactory
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Database and Unified Platform Repository
        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database)

        // 2. Instantiate PlatformViewModel with Unified Factory
        val factory = PlatformViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[PlatformViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: PlatformViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    // Notifications State
    val errorState by viewModel.errorState.collectAsState()
    val successState by viewModel.successState.collectAsState()

    // Temporary active game selections
    var activePlayGameType by remember { mutableStateOf("REFLEX") }
    var activePlayStakeAmount by remember { mutableStateOf(10.0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                // Render Navigation Bar only when authenticated
                if (currentScreen != Screen.AUTH) {
                    ArenaBottomNavBar(
                        currentScreen = currentScreen,
                        isAdmin = currentUser?.isAdmin == true,
                        onTabSelect = { screen -> viewModel.navigateTo(screen) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Cross-screen router navigation state machine
                when (currentScreen) {
                    Screen.AUTH -> AuthScreen(viewModel)
                    Screen.LOBBY -> LobbyScreen(viewModel, onPlayGame = { type, stake ->
                        activePlayGameType = type
                        activePlayStakeAmount = stake
                        viewModel.navigateTo(if (type == "REFLEX") Screen.PLAY_REFLEX else Screen.PLAY_MATH)
                    })
                    Screen.WALLET -> WalletScreen(viewModel)
                    Screen.KYC -> KycScreen(viewModel)
                    Screen.SUPPORT -> SupportScreen(viewModel)
                    Screen.SETTINGS -> SettingsScreen(viewModel)
                    Screen.ADMIN -> AdminScreen(viewModel)
                    Screen.PLAY_REFLEX -> GameScreen(viewModel, "REFLEX", activePlayStakeAmount)
                    Screen.PLAY_MATH -> GameScreen(viewModel, "MATH", activePlayStakeAmount)
                }
            }
        }

        // --- CUSTOM TOP FLOATING CYBER STATUS TOAST SNACKBARS (UI POLISH) ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error overlay popup
            errorState?.let { err ->
                FloatingAlertBanner(
                    message = err,
                    backgroundColor = Color(0xFFFF3D00),
                    icon = Icons.Default.Cancel,
                    onDismiss = { viewModel.clearErrors() }
                )
            }

            // Success overlay popup
            successState?.let { msg ->
                FloatingAlertBanner(
                    message = msg,
                    backgroundColor = Color(0xFF00E676),
                    icon = Icons.Default.CheckCircle,
                    onDismiss = { viewModel.clearSuccess() }
                )
            }
        }
    }
}

@Composable
fun ArenaBottomNavBar(
    currentScreen: Screen,
    isAdmin: Boolean,
    onTabSelect: (Screen) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(80.dp)
            .background(Color(0xFFF3EDF7))
            .border(width = 0.5.dp, color = Color(0xFFE6E0E9))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        NavBarItem(
            isSelected = currentScreen == Screen.LOBBY || currentScreen == Screen.PLAY_REFLEX || currentScreen == Screen.PLAY_MATH,
            icon = Icons.Default.SportsEsports,
            label = "Lobby",
            onClick = { onTabSelect(Screen.LOBBY) }
        )
        NavBarItem(
            isSelected = currentScreen == Screen.WALLET,
            icon = Icons.Default.AccountBalanceWallet,
            label = "Wallet",
            onClick = { onTabSelect(Screen.WALLET) }
        )
        NavBarItem(
            isSelected = currentScreen == Screen.KYC,
            icon = Icons.Default.Shield,
            label = "KYC",
            onClick = { onTabSelect(Screen.KYC) }
        )
        NavBarItem(
            isSelected = currentScreen == Screen.SUPPORT,
            icon = Icons.Default.SupportAgent,
            label = "Support",
            onClick = { onTabSelect(Screen.SUPPORT) }
        )
        NavBarItem(
            isSelected = currentScreen == Screen.SETTINGS,
            icon = Icons.Default.Settings,
            label = "Settings",
            onClick = { onTabSelect(Screen.SETTINGS) }
        )
        if (isAdmin) {
            NavBarItem(
                isSelected = currentScreen == Screen.ADMIN,
                icon = Icons.Default.AdminPanelSettings,
                label = "Admin",
                onClick = { onTabSelect(Screen.ADMIN) }
            )
        }
    }
}

@Composable
fun NavBarItem(
    isSelected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF21005D)
    val activePillBg = Color(0xFFEADDFF)
    val inactiveColor = Color(0xFF49454F)

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .testTag("nav_tab_$label"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) activePillBg else Color.Transparent)
                .padding(horizontal = 20.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) Color(0xFF1D1B20) else inactiveColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun FloatingAlertBanner(
    message: String,
    backgroundColor: Color,
    icon: ImageVector,
    onDismiss: () -> Unit
) {
    // Dismiss automatically after 3.5 seconds
    LaunchedEffect(message) {
        delay(3500)
        onDismiss()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .border(1.dp, backgroundColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(imageVector = icon, contentDescription = null, tint = backgroundColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = message,
                    color = Color(0xFF1D1B20),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF49454F), modifier = Modifier.size(16.dp))
            }
        }
    }
}
