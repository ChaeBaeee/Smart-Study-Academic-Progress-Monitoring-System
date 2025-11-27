package com.smartstudy.services

import com.smartstudy.data.DataManager
import com.smartstudy.models.UserProfile
import com.smartstudy.models.UserSession
import java.security.MessageDigest
import java.util.UUID

class AuthService {

    private var cachedUser: UserProfile? = null

    init {
        // Check for existing session - auto-login if session exists
        val session = DataManager.getUserSession()
        if (session.loggedIn && session.userId != null) {
            val profile = DataManager.getUserProfile()
            if (profile != null && profile.id == session.userId) {
                cachedUser = profile
            } else {
                // Session exists but user doesn't match - clear invalid session
                DataManager.clearUserSession()
            }
        }
    }

    fun isLoggedIn(): Boolean = DataManager.getUserSession().loggedIn && cachedUser != null

    fun getCurrentUser(): UserProfile? {
        if (!isLoggedIn()) return null
        if (cachedUser == null) {
            cachedUser = DataManager.getUserProfile()
        }
        return cachedUser
    }

    fun login(email: String, password: String): Result<UserProfile> {
        val user = DataManager.getUserProfile()
            ?: return Result.failure(IllegalStateException("No user profile found"))

        return if (user.email.equals(email.trim(), ignoreCase = true) &&
            verifyPassword(password, user.passwordHash)
        ) {
            cachedUser = user
            DataManager.saveUserSession(
                UserSession(userId = user.id, loggedIn = true, lastLogin = System.currentTimeMillis())
            )
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }

    fun register(name: String, email: String, password: String): Result<UserProfile> {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Please enter valid registration details"))
        }

        val profile = UserProfile(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            email = email.trim(),
            passwordHash = hashPassword(password),
            avatarInitials = name.trim().split(" ").take(2).joinToString("") { it.first().uppercase() },
            studyGoalHours = 15,
            streakDays = 0
        )

        DataManager.saveUserProfile(profile)
        cachedUser = profile
        DataManager.saveUserSession(
            UserSession(userId = profile.id, loggedIn = true, lastLogin = System.currentTimeMillis())
        )
        return Result.success(profile)
    }

    fun logout() {
        cachedUser = null
        DataManager.clearUserSession()
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }
}

