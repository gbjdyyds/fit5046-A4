package com.example.ass4.repository

import android.app.Application
import com.example.ass4.database.WearHistory
import com.example.ass4.database.WearHistoryDao
import com.example.ass4.database.ClothDatabase
import com.example.ass4.database.MonthlyRepeatReusage
import kotlinx.coroutines.flow.Flow

class WearHistoryRepository(application: Application) {
    private val wearHistoryDao = ClothDatabase.getDatabase(application).wearHistoryDao()

    suspend fun insertWearHistory(wearHistory: WearHistory) {
        wearHistoryDao.insertWearHistory(wearHistory)
    }

    suspend fun getMonthlyRepeatReusageTrend(
        uid: String,
        start: Long,
        end: Long,
//        limit: Int = 6
    ): List<MonthlyRepeatReusage> {
        return wearHistoryDao.getMonthlyRepeatReusageTrend(uid, start, end)
    }

    suspend fun getRepeatWearCountInDays(uid: String, startTime: Long): Int {
        return wearHistoryDao.getRepeatWearCountInDays(uid, startTime)
    }

    suspend fun getMaxRepeatWearCount(uid: String): Int? {
        return wearHistoryDao.getMaxRepeatWearCount(uid)
    }

    suspend fun getAllAvailableMonths(uid: String): List<String> {
        return wearHistoryDao.getAllAvailableMonths(uid)
    }
}
