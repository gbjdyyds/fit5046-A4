package com.example.uidesign.repository

import android.app.Application
import com.example.uidesign.database.WearHistory
import com.example.uidesign.database.WearHistoryDao
import com.example.uidesign.database.ClothDatabase
import com.example.uidesign.database.MonthlyRepeatReusage
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
        limit: Int = 6
    ): List<MonthlyRepeatReusage> {
        return wearHistoryDao.getMonthlyRepeatReusageTrend(uid, start, end, limit)
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
