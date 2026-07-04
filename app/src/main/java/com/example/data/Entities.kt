package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val email: String,
    val passwordHash: String,
    val balance: Double = 100.0, // Pre-load 100 simulated credits for a great demo experience
    val bonusBalance: Double = 10.0,
    val isKycVerified: Boolean = false,
    val kycDocumentId: String = "",
    val referralCode: String = "",
    val referredBy: String = "",
    val isBanned: Boolean = false,
    val dailyDepositLimit: Double = 500.0,
    val dailyPlayTimeLimitMinutes: Int = 120,
    val dailyDepositSpent: Double = 0.0,
    val selfExcludedUntil: Long? = null,
    val isAdmin: Boolean = false
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val type: String, // DEPOSIT, WITHDRAWAL, GAME_ENTRY, GAME_REWARD, REFERRAL_BONUS
    val amount: Double,
    val status: String, // PENDING, APPROVED, REJECTED
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String
)

@Entity(tableName = "leaderboard_items")
data class LeaderboardItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val gameType: String, // REFLEX, MATH
    val score: Int,
    val rewardAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val sender: String, // USER, AI
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
