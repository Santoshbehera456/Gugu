package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET balance = :newBalance WHERE username = :username")
    suspend fun updateBalance(username: String, newBalance: Double)

    @Query("UPDATE users SET isBanned = :isBanned WHERE username = :username")
    suspend fun updateUserBanStatus(username: String, isBanned: Boolean)

    @Query("UPDATE users SET isKycVerified = :isVerified, kycDocumentId = :docId WHERE username = :username")
    suspend fun updateKycStatus(username: String, isVerified: Boolean, docId: String)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingTransactionsFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("UPDATE transactions SET status = :status WHERE id = :id")
    suspend fun updateTransactionStatus(id: Long, status: String)
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_items WHERE gameType = :gameType ORDER BY score DESC, timestamp DESC LIMIT 20")
    fun getLeaderboardByGame(gameType: String): Flow<List<LeaderboardItem>>

    @Query("SELECT * FROM leaderboard_items ORDER BY score DESC, timestamp DESC LIMIT 20")
    fun getGlobalLeaderboard(): Flow<List<LeaderboardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardItem(item: LeaderboardItem)
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getMessagesByUserId(userId: String): Flow<List<SupportMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SupportMessage)

    @Query("DELETE FROM support_messages WHERE userId = :userId")
    suspend fun clearMessagesForUser(userId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsByUserId(userId: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}
