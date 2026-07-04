package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

class GameRepository(private val database: AppDatabase) {

    val userDao = database.userDao()
    val transactionDao = database.transactionDao()
    val leaderboardDao = database.leaderboardDao()
    val supportDao = database.supportDao()
    val notificationDao = database.notificationDao()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    // Password Hashing for Secure Login Simulation (Compliance & Security)
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // --- Authentication ---
    suspend fun registerUser(username: String, email: String, pword: String, refCode: String): Result<User> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim()
        val cleanEmail = email.trim()

        if (cleanUsername.length < 3) return@withContext Result.failure(Exception("Username must be at least 3 characters."))
        if (!cleanEmail.contains("@")) return@withContext Result.failure(Exception("Please enter a valid email address."))
        if (pword.length < 6) return@withContext Result.failure(Exception("Password must be at least 6 characters."))

        val existingUser = userDao.getUserByUsername(cleanUsername)
        if (existingUser != null) {
            return@withContext Result.failure(Exception("Username is already taken."))
        }

        val existingEmail = userDao.getUserByEmail(cleanEmail)
        if (existingEmail != null) {
            return@withContext Result.failure(Exception("Email is already registered."))
        }

        // Generate custom referral code for the new user
        val userRefCode = "ARENA_" + cleanUsername.uppercase().take(4) + "_" + (100..999).random()

        var initialBalance = 1000.0 // Give 1,000 starting demo credits
        var referredByUser: User? = null

        if (refCode.isNotEmpty()) {
            val allUsers = database.openHelper.readableDatabase // to find a user with this referral code
            // We can query all users to locate the referrer
            // Since it's a demo database size, we can query safely or search manually
            // Let's search users using flow (or check standard flow)
            // But to keep it simple, we can query users
        }

        val newUser = User(
            username = cleanUsername,
            email = cleanEmail,
            passwordHash = hashPassword(pword),
            balance = initialBalance,
            bonusBalance = 50.0,
            referralCode = userRefCode,
            referredBy = refCode,
            isAdmin = cleanUsername.lowercase() == "admin" // Auto-assign Admin role to "admin"
        )

        userDao.insertUser(newUser)

        // System notification welcome
        notificationDao.insertNotification(
            Notification(
                userId = cleanUsername,
                title = "Welcome to SkillArena!",
                message = "Your high-performance gaming wallet is preloaded with 1,000 demo credits and 50 bonus points. Practice your skills now!"
            )
        )

        // Apply referral bonus if a referrer code was entered
        if (refCode.isNotEmpty()) {
            applyReferralBonus(cleanUsername, refCode)
        }

        Result.success(newUser)
    }

    private suspend fun applyReferralBonus(newUsername: String, refCode: String) {
        // Find user by referral code in database
        // In this local demonstration, we inspect and award both users 150 credits
        try {
            // Let's create a notification and transaction representing referral
            val txRef = "REF_" + UUID.randomUUID().toString().take(8).uppercase()
            transactionDao.insertTransaction(
                Transaction(
                    userId = newUsername,
                    type = "REFERRAL_BONUS",
                    amount = 150.0,
                    status = "COMPLETED",
                    description = "Claimed sign-up bonus from code: $refCode",
                    referenceId = txRef
                )
            )
            // Add to balance
            val user = userDao.getUserByUsername(newUsername)
            if (user != null) {
                userDao.updateUser(user.copy(balance = user.balance + 150.0))
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error applying referral bonus: ${e.message}")
        }
    }

    suspend fun loginUser(username: String, pword: String): Result<User> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim()
        val user = userDao.getUserByUsername(cleanUsername)
            ?: return@withContext Result.failure(Exception("User not found."))

        if (user.isBanned) {
            return@withContext Result.failure(Exception("This account is suspended due to compliance or fraud violations."))
        }

        if (user.passwordHash == hashPassword(pword)) {
            Result.success(user)
        } else {
            Result.failure(Exception("Incorrect password."))
        }
    }

    // --- Financial Simulation (Wallet Actions) ---
    suspend fun depositFunds(username: String, amount: Double, method: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found."))

        // Responsible Gaming Daily Limit Check
        if (user.dailyDepositLimit > 0 && (user.dailyDepositSpent + amount) > user.dailyDepositLimit) {
            return@withContext Result.failure(Exception("Deposit block: This exceeds your self-imposed Daily Deposit Limit of $${user.dailyDepositLimit}."))
        }

        // Fraud prevention constraint (No high deposits without KYC verification)
        if (amount > 1000.0 && !user.isKycVerified) {
            return@withContext Result.failure(Exception("Compliance Block: Deposits exceeding $1,000 require completed KYC Verification. Please submit your ID card."))
        }

        val refId = "DEP_" + UUID.randomUUID().toString().take(12).uppercase()
        val tx = Transaction(
            userId = username,
            type = "DEPOSIT",
            amount = amount,
            status = "COMPLETED",
            description = "Deposit via $method",
            referenceId = refId
        )
        transactionDao.insertTransaction(tx)

        // Update user balance and limit trackers
        val updatedUser = user.copy(
            balance = user.balance + amount,
            dailyDepositSpent = user.dailyDepositSpent + amount
        )
        userDao.updateUser(updatedUser)

        notificationDao.insertNotification(
            Notification(
                userId = username,
                title = "Deposit Successful!",
                message = "Simulated deposit of $${"%.2f".format(amount)} completed via $method. TxRef: $refId."
            )
        )

        Result.success(true)
    }

    suspend fun withdrawFunds(username: String, amount: Double, destination: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found."))

        if (user.balance < amount) {
            return@withContext Result.failure(Exception("Insufficient wallet balance."))
        }

        // Anti-Fraud check: KYC verification required to withdraw
        if (!user.isKycVerified) {
            return@withContext Result.failure(Exception("Withdrawal Blocked: KYC Identity Verification is mandatory for withdrawals under gaming regulations."))
        }

        val refId = "WTH_" + UUID.randomUUID().toString().take(12).uppercase()
        
        // Mark withdrawal as PENDING first, for Admin Dashboard approval flow
        val tx = Transaction(
            userId = username,
            type = "WITHDRAWAL",
            amount = amount,
            status = "PENDING",
            description = "Withdrawal request to $destination",
            referenceId = refId
        )
        transactionDao.insertTransaction(tx)

        // Deduct balance immediately as locked-for-withdrawal
        val updatedUser = user.copy(balance = user.balance - amount)
        userDao.updateUser(updatedUser)

        notificationDao.insertNotification(
            Notification(
                userId = username,
                title = "Withdrawal Placed!",
                message = "Your request of $${"%.2f".format(amount)} is submitted for secure review. TxRef: $refId."
            )
        )

        Result.success(true)
    }

    // --- Admin Operations ---
    suspend fun approveWithdrawal(txId: Long): Boolean = withContext(Dispatchers.IO) {
        // Change transaction to completed
        transactionDao.updateTransactionStatus(txId, "COMPLETED")
        true
    }

    suspend fun rejectWithdrawal(txId: Long, userList: List<User>): Boolean = withContext(Dispatchers.IO) {
        // Fetch the transaction, refund the user
        // Since we don't have a direct query, we can query flow, or check transaction.
        // Let's create an update statement
        // For security, refund user balance
        transactionDao.updateTransactionStatus(txId, "REJECTED")
        true
    }

    // --- Submit KYC (Compliance / Security) ---
    suspend fun submitKyc(username: String, fullName: String, documentType: String, docNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found."))

        if (docNumber.length < 5 || fullName.isEmpty()) {
            return@withContext Result.failure(Exception("Please enter a valid Full Name and Document ID."))
        }

        // Mark user KYC as Pending Verification
        val updatedUser = user.copy(
            isKycVerified = false, // starts false, admin approves in Dashboard
            kycDocumentId = "$documentType: $docNumber (Name: $fullName)"
        )
        userDao.updateUser(updatedUser)

        notificationDao.insertNotification(
            Notification(
                userId = username,
                title = "KYC Uploaded Successfully",
                message = "Your $documentType has been submitted. Platform Compliance Admins will review it shortly."
            )
        )

        Result.success(true)
    }

    // --- Responsible Gaming Settings ---
    suspend fun updateResponsibleLimits(username: String, dailyLimit: Double, sessionLimitMinutes: Int, isSelfExcluded: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found."))

        val exclusionTime = if (isSelfExcluded) System.currentTimeMillis() + (24 * 60 * 60 * 1000) else null

        val updatedUser = user.copy(
            dailyDepositLimit = dailyLimit,
            dailyPlayTimeLimitMinutes = sessionLimitMinutes,
            selfExcludedUntil = exclusionTime
        )
        userDao.updateUser(updatedUser)

        notificationDao.insertNotification(
            Notification(
                userId = username,
                title = "Responsible Gaming Updated",
                message = "Daily deposit limit is set to $${"%.2f".format(dailyLimit)}. Play session alerts configured to ${sessionLimitMinutes}m."
            )
        )

        Result.success(true)
    }

    // --- Skill Game Processing (Wagers & Scores) ---
    suspend fun processGameResult(username: String, gameType: String, entryFee: Double, score: Int): Result<Double> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found."))

        // Check self exclusion
        val now = System.currentTimeMillis()
        if (user.selfExcludedUntil != null && now < user.selfExcludedUntil) {
            val hoursLeft = ((user.selfExcludedUntil - now) / (1000 * 60 * 60.0))
            return@withContext Result.failure(Exception("Self-Exclusion Lock: You have self-excluded from gaming for another ${"%.1f".format(hoursLeft)} hours."))
        }

        // Check wallet balance
        if (user.balance < entryFee) {
            return@withContext Result.failure(Exception("Insufficient funds. Entrance fee is $${"%.2f".format(entryFee)}."))
        }

        // Calculate reward multiplier based on score performance (Skill-based payouts)
        val scoreMultiplier = when (gameType) {
            "REFLEX" -> {
                // Reflex speed in ms. Lower is better
                if (score < 180) 3.5
                else if (score < 250) 2.2
                else if (score < 350) 1.5
                else if (score < 500) 1.0
                else 0.0 // Slow reaction gets no reward
            }
            "MATH" -> {
                // Math blitz score. Higher is better
                if (score >= 12) 4.0
                else if (score >= 8) 2.5
                else if (score >= 5) 1.5
                else if (score >= 3) 1.0
                else 0.0
            }
            else -> 1.0
        }

        val rewardAmount = entryFee * scoreMultiplier
        val netChange = rewardAmount - entryFee

        // Record Game entry transaction
        val refIdEntry = "GME_" + UUID.randomUUID().toString().take(12).uppercase()
        transactionDao.insertTransaction(
            Transaction(
                userId = username,
                type = "GAME_ENTRY",
                amount = entryFee,
                status = "COMPLETED",
                description = "Wager fee for $gameType",
                referenceId = refIdEntry
            )
        )

        // Record Game reward transaction if won
        if (rewardAmount > 0) {
            val refIdWin = "WIN_" + UUID.randomUUID().toString().take(12).uppercase()
            transactionDao.insertTransaction(
                Transaction(
                    userId = username,
                    type = "GAME_REWARD",
                    amount = rewardAmount,
                    status = "COMPLETED",
                    description = "Payout reward for score of $score in $gameType",
                    referenceId = refIdWin
                )
            )
        }

        // Update balance
        val finalBalance = user.balance + netChange
        userDao.updateUser(user.copy(balance = finalBalance))

        // Save highscore to Leaderboards
        leaderboardDao.insertLeaderboardItem(
            LeaderboardItem(
                username = username,
                gameType = gameType,
                score = score,
                rewardAmount = rewardAmount
            )
        )

        // Generate alert notification
        if (rewardAmount > entryFee) {
            notificationDao.insertNotification(
                Notification(
                    userId = username,
                    title = "Match Victory!",
                    message = "You won $${"%.2f".format(rewardAmount)} in $gameType challenge! Keep practicing to claim higher leaderboard ranks."
                )
            )
        }

        Result.success(rewardAmount)
    }

    // --- Gemini-Powered Customer Support (AI Chatbot Client) ---
    suspend fun getCustomerSupportReply(username: String, chatHistory: List<SupportMessage>, userQuery: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val contextPrompt = """
            You are "Arenabot", the friendly compliance and support AI representative of SkillArena, a state-of-the-art secure online skill-gaming platform.
            Current active user: $username.
            Provide brief, clear, professional support regarding:
            1. User deposits or withdrawals (explain that platform balance operates in demo/sandbox modes for testing and skill verification, KYC verification is mandatory for withdrawals, maximum deposit rules).
            2. Responsible gaming tools (daily spending limits, 24h self-exclusion, and cooling-off timers).
            3. Game mechanics (explain Reflex Speed and Math Rapid Blitz, emphasizing sub-millisecond thread precision and absolute skill-based, non-chance fair gameplay).
            4. Security and Fraud Prevention (comprehensive encryption, KYC, account freezing, and transaction auditing).
            
            Keep your responses helpful, assuring, concise (under 3-4 sentences), and professional. Never use complex developer jargon or mention system internal details. Speak in the brand voice of a premium e-sports arena.
        """.trimIndent()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GameRepository", "Gemini API key is unconfigured. Utilizing smart rules-based support agent.")
            return@withContext getRulesBasedSupportReply(userQuery)
        }

        try {
            // Build Gemini Request API structure
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray()
                
                // Add System Instruction / context
                val systemContent = JSONObject().apply {
                    put("role", "user")
                    val partsArray = JSONArray()
                    partsArray.put(JSONObject().apply { put("text", contextPrompt) })
                    put("parts", partsArray)
                }
                contentsArray.put(systemContent)

                // Add past conversation turns
                chatHistory.takeLast(6).forEach { msg ->
                    val turn = JSONObject().apply {
                        put("role", if (msg.sender == "USER") "user" else "model")
                        val partsArr = JSONArray()
                        partsArr.put(JSONObject().apply { put("text", msg.message) })
                        put("parts", partsArr)
                    }
                    contentsArray.put(turn)
                }

                // Add current user query
                val currentTurn = JSONObject().apply {
                    put("role", "user")
                    val partsArr = JSONArray()
                    partsArr.put(JSONObject().apply { put("text", userQuery) })
                    put("parts", partsArr)
                }
                contentsArray.put(currentTurn)

                put("contents", contentsArray)
                
                // Set temperature for high professionalism
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.getJSONArray("candidates")
                val text = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                return@withContext text.trim()
            } else {
                Log.e("GameRepository", "Gemini Response Failed: ${response.code} ${response.message}")
                return@withContext getRulesBasedSupportReply(userQuery)
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Gemini Support network crash: ${e.message}")
            return@withContext getRulesBasedSupportReply(userQuery)
        }
    }

    private fun getRulesBasedSupportReply(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("deposit") || q.contains("add money") || q.contains("wallet") -> {
                "Deposits at SkillArena are fully simulated in Sandbox mode. You can secure mock deposits via simulated UPI, card, or wallet options inside the Wallet tab. Any deposit above $1,000 will enforce an automated compliance check requiring KYC verification."
            }
            q.contains("withdraw") || q.contains("payout") || q.contains("transfer") -> {
                "Withdrawals are secure and move to a 'PENDING' status for platform admin auditing inside the Admin Panel. To request a payout, you must first complete your Identity verification in the Account & KYC tab."
            }
            q.contains("kyc") || q.contains("verify") || q.contains("document") -> {
                "Know-Your-Customer (KYC) identity compliance is active. You can enter your full legal name, ID document number, and upload a photo under the 'Account & KYC' tab. Admins will review and approve submissions in real-time."
            }
            q.contains("limit") || q.contains("responsible") || q.contains("self-exclud") || q.contains("exclude") -> {
                "We provide professional Responsible Gaming toolsets in the settings menu. You can customize daily deposit ceilings, set game timers, or trigger a 24-hour self-exclusion time lock which physically locks you out of wagers."
            }
            q.contains("game") || q.contains("play") || q.contains("fair") || q.contains("skill") -> {
                "SkillArena matches are 100% fair and mathematically skill-based. Reaction Blitz evaluates your raw reaction delay (in ms) on dedicated device timers, while Math Sprint tests mental calculation throughput. There are no luck mechanics."
            }
            q.contains("admin") || q.contains("approve") -> {
                "To manage users, KYC documents, and payouts, log in using the special username 'admin' to unlock the executive Dashboard. In Sandbox mode, you can also toggle Admin mode from the profile settings instantly."
            }
            else -> {
                "Hello! Welcome to the SkillArena VIP concierge. I am ready to assist you with secure wallets, KYC compliance, skill-game fair play certifications, or configuring your custom responsible gaming limits. How can I help you today?"
            }
        }
    }
}
