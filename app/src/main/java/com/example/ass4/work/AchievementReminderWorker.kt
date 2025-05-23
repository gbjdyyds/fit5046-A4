package com.example.ass4.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ass4.repository.ClothRepository
import com.example.ass4.repository.WearHistoryRepository
import com.google.firebase.auth.FirebaseAuth

class AchievementReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
        val clothRepo = ClothRepository(applicationContext as android.app.Application)
        val wearHistoryRepo = WearHistoryRepository(applicationContext as android.app.Application)

        // Minimalist
        val totalClothes = clothRepo.getClothesByUserOnce(uid).size
        val isMinimalist = totalClothes in 10..20

        // Eco Warrior
        val noShoppingDays = clothRepo.getNoShoppingDays(uid)
        val isEcoWarrior = noShoppingDays >= 30

        // Style Master
        val maxRepeatCount = wearHistoryRepo.getMaxRepeatWearCount(uid) ?: 0
        val isStyleMaster = maxRepeatCount >= 50

        val hasLocked = !(isMinimalist && isEcoWarrior && isStyleMaster)

        // For testing: always trigger on each entry
        val shouldShow = hasLocked

        if (shouldShow) {
            applicationContext.getSharedPreferences("reminder", Context.MODE_PRIVATE)
                .edit().putBoolean("show_achievement_reminder", true).apply()
        }
        return Result.success()
    }
} 