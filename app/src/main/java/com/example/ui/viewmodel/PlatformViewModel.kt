package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    AUTH, LOBBY, WALLET, KYC, SUPPORT, SETTINGS, ADMIN, PLAY_REFLEX, PLAY_MATH
}

class PlatformViewModel(
    application: Application,
    private val repository: GameRepository
) : AndroidViewModel(application) {

    // --- Navigation Custom Router State ---
    private val _currentScreen = MutableStateFlow(Screen.AUTH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        // Enforce block if user is banned
        val user = _currentUser.value
        if (user != null && user.isBanned && screen != Screen.AUTH) {
            _errorState.value = "Account suspended. You cannot access platform features."
            _currentScreen.value = Screen.AUTH
            return
        }
        _currentScreen.value = screen
    }

    // --- User Session ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _allUsers = repository.userDao.getAllUsersFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val allUsers: StateFlow<List<User>> = _allUsers

    // --- Transactions & Wallet ---
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _pendingWithdrawals = repository.transactionDao.getPendingTransactionsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val pendingWithdrawals: StateFlow<List<Transaction>> = _pendingWithdrawals

    // --- Leaderboards ---
    private val _leaderboardReflex = repository.leaderboardDao.getLeaderboardByGame("REFLEX").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val leaderboardReflex: StateFlow<List<LeaderboardItem>> = _leaderboardReflex

    private val _leaderboardMath = repository.leaderboardDao.getLeaderboardByGame("MATH").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val leaderboardMath: StateFlow<List<LeaderboardItem>> = _leaderboardMath

    // --- Support Chat ---
    private val _chatMessages = MutableStateFlow<List<SupportMessage>>(emptyList())
    val chatMessages: StateFlow<List<SupportMessage>> = _chatMessages.asStateFlow()

    // --- Notifications ---
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // --- UI UI State Trackers ---
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val _successState = MutableStateFlow<String?>(null)
    val successState: StateFlow<String?> = _successState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Collect transactions, chat, notifications dynamically if user signs in
        viewModelScope.launch {
            _currentUser.collectLatest { user ->
                if (user != null) {
                    // Update lists
                    repository.transactionDao.getTransactionsByUserId(user.username).collect {
                        _transactions.value = it
                    }
                } else {
                    _transactions.value = emptyList()
                }
            }
        }
        viewModelScope.launch {
            _currentUser.collectLatest { user ->
                if (user != null) {
                    repository.supportDao.getMessagesByUserId(user.username).collect {
                        _chatMessages.value = it
                    }
                } else {
                    _chatMessages.value = emptyList()
                }
            }
        }
        viewModelScope.launch {
            _currentUser.collectLatest { user ->
                if (user != null) {
                    repository.notificationDao.getNotificationsByUserId(user.username).collect {
                        _notifications.value = it
                    }
                } else {
                    _notifications.value = emptyList()
                }
            }
        }

        // Insert initial mock players to populate the leaderboard dynamically
        viewModelScope.launch {
            repository.leaderboardDao.insertLeaderboardItem(LeaderboardItem(username = "ReflexViper", gameType = "REFLEX", score = 165, rewardAmount = 35.0))
            repository.leaderboardDao.insertLeaderboardItem(LeaderboardItem(username = "BlitzMathematician", gameType = "MATH", score = 15, rewardAmount = 40.0))
            repository.leaderboardDao.insertLeaderboardItem(LeaderboardItem(username = "HyperCortex", gameType = "REFLEX", score = 210, rewardAmount = 22.0))
            repository.leaderboardDao.insertLeaderboardItem(LeaderboardItem(username = "CalculusDemon", gameType = "MATH", score = 11, rewardAmount = 25.0))
            repository.leaderboardDao.insertLeaderboardItem(LeaderboardItem(username = "PingSlayer", gameType = "REFLEX", score = 245, rewardAmount = 22.0))
        }
    }

    fun clearErrors() { _errorState.value = null }
    fun clearSuccess() { _successState.value = null }

    // --- Authentication Actions ---
    fun register(username: String, email: String, pword: String, refCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registerUser(username, email, pword, refCode)
            _isLoading.value = false
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _successState.value = "Registration Successful! Welcome to SkillArena."
                navigateTo(Screen.LOBBY)
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "Registration Failed"
            }
        }
    }

    fun login(username: String, pword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.loginUser(username, pword)
            _isLoading.value = false
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _successState.value = "Welcome back, $username!"
                navigateTo(Screen.LOBBY)
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "Login Failed"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        navigateTo(Screen.AUTH)
    }

    // --- Wallet Transactions ---
    fun deposit(amount: Double, method: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.depositFunds(user.username, amount, method)
            _isLoading.value = false
            if (result.isSuccess) {
                _successState.value = "Simulated deposit of $${"%.2f".format(amount)} completed!"
                refreshUserSession()
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "Deposit failed"
            }
        }
    }

    fun withdraw(amount: Double, destination: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.withdrawFunds(user.username, amount, destination)
            _isLoading.value = false
            if (result.isSuccess) {
                _successState.value = "Withdrawal request submitted for compliance audit."
                refreshUserSession()
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "Withdrawal failed"
            }
        }
    }

    // --- Submit KYC ---
    fun submitKycInfo(fullName: String, docType: String, docNumber: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.submitKyc(user.username, fullName, docType, docNumber)
            _isLoading.value = false
            if (result.isSuccess) {
                _successState.value = "KYC documents submitted. Approval pending."
                refreshUserSession()
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "KYC upload failed"
            }
        }
    }

    // --- Responsible Gaming Limits ---
    fun setResponsibleGamingLimits(dailyLimit: Double, playtimeMinutes: Int, applySelfExclusion: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateResponsibleLimits(user.username, dailyLimit, playtimeMinutes, applySelfExclusion)
            _isLoading.value = false
            if (result.isSuccess) {
                _successState.value = "Responsible limits applied successfully."
                refreshUserSession()
                if (applySelfExclusion) {
                    logout()
                }
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "Failed to update limits"
            }
        }
    }

    // --- Support Chat ---
    fun sendSupportMessage(msgText: String) {
        val user = _currentUser.value ?: return
        if (msgText.isBlank()) return

        viewModelScope.launch {
            // Save User message
            val userMsg = SupportMessage(userId = user.username, sender = "USER", message = msgText)
            repository.supportDao.insertMessage(userMsg)

            // Collect history for AI context
            val history = _chatMessages.value + userMsg

            // Generate AI Response
            val aiReply = repository.getCustomerSupportReply(user.username, history, msgText)
            val aiMsg = SupportMessage(userId = user.username, sender = "AI", message = aiReply)
            repository.supportDao.insertMessage(aiMsg)
        }
    }

    fun clearChatHistory() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.supportDao.clearMessagesForUser(user.username)
        }
    }

    // --- Game Play Results Processing ---
    fun submitGameResult(gameType: String, entryFee: Double, score: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.processGameResult(user.username, gameType, entryFee, score)
            _isLoading.value = false
            if (result.isSuccess) {
                val winnings = result.getOrDefault(0.0)
                if (winnings > 0.0) {
                    _successState.value = "Victory! Claimed $${"%.2f".format(winnings)} reward credits."
                } else {
                    _errorState.value = "No payout. Score: $score. Better luck next match!"
                }
                refreshUserSession()
            } else {
                _errorState.value = result.exceptionOrNull()?.message ?: "Failed to register game score"
                navigateTo(Screen.LOBBY)
            }
        }
    }

    // --- Admin Operations ---
    fun approveKyc(username: String) {
        viewModelScope.launch {
            val user = repository.userDao.getUserByUsername(username) ?: return@launch
            val updatedUser = user.copy(isKycVerified = true)
            repository.userDao.updateUser(updatedUser)
            
            repository.notificationDao.insertNotification(
                Notification(
                    userId = username,
                    title = "KYC Verified!",
                    message = "Congratulations, your identity is verified. Payouts and high-tier deposits are unlocked."
                )
            )
            refreshUserSession()
            _successState.value = "Approved KYC verification for $username."
        }
    }

    fun rejectKyc(username: String) {
        viewModelScope.launch {
            val user = repository.userDao.getUserByUsername(username) ?: return@launch
            val updatedUser = user.copy(isKycVerified = false, kycDocumentId = "")
            repository.userDao.updateUser(updatedUser)
            
            repository.notificationDao.insertNotification(
                Notification(
                    userId = username,
                    title = "KYC Denied",
                    message = "Your identity documents were rejected. Please submit valid documents to withdraw."
                )
            )
            refreshUserSession()
            _successState.value = "Rejected KYC for $username."
        }
    }

    fun toggleUserBan(username: String) {
        viewModelScope.launch {
            val user = repository.userDao.getUserByUsername(username) ?: return@launch
            val newBanState = !user.isBanned
            repository.userDao.updateUserBanStatus(username, newBanState)
            _successState.value = "User $username ${if (newBanState) "suspended" else "unsuspended"}."
            refreshUserSession()
        }
    }

    fun processAdminWithdrawalApproval(tx: Transaction, approve: Boolean) {
        viewModelScope.launch {
            if (approve) {
                repository.approveWithdrawal(tx.id)
                repository.notificationDao.insertNotification(
                    Notification(
                        userId = tx.userId,
                        title = "Withdrawal Dispatched",
                        message = "Your withdrawal of $${"%.2f".format(tx.amount)} has been approved and processed."
                    )
                )
                _successState.value = "Approved withdrawal ID #${tx.id}."
            } else {
                // Reject, refund balance
                repository.transactionDao.updateTransactionStatus(tx.id, "REJECTED")
                val user = repository.userDao.getUserByUsername(tx.userId)
                if (user != null) {
                    repository.userDao.updateUser(user.copy(balance = user.balance + tx.amount))
                }
                repository.notificationDao.insertNotification(
                    Notification(
                        userId = tx.userId,
                        title = "Withdrawal Rejected",
                        message = "Your withdrawal of $${"%.2f".format(tx.amount)} was rejected. Funds are refunded."
                    )
                )
                _successState.value = "Rejected and refunded withdrawal ID #${tx.id}."
            }
            refreshUserSession()
        }
    }

    fun triggerSystemNotificationBlast(title: String, message: String) {
        viewModelScope.launch {
            val users = _allUsers.value
            users.forEach { u ->
                repository.notificationDao.insertNotification(
                    Notification(userId = u.username, title = title, message = message)
                )
            }
            _successState.value = "Broadcasted notification alert to all active arena players."
        }
    }

    fun toggleAdminAccessOverride() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(isAdmin = !user.isAdmin)
            repository.userDao.insertUser(updated)
            _currentUser.value = updated
            _successState.value = "Role changed. Admin privileges ${if (updated.isAdmin) "ENABLED" else "DISABLED"}."
        }
    }

    fun markNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.notificationDao.markAllAsRead(user.username)
        }
    }

    private suspend fun refreshUserSession() {
        val user = _currentUser.value ?: return
        _currentUser.value = repository.userDao.getUserByUsername(user.username)
    }
}

class PlatformViewModelFactory(
    private val application: Application,
    private val repository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlatformViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlatformViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
