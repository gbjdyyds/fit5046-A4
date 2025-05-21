package com.example.uidesign.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WearHistoryDao {
    @Insert
    suspend fun insertWearHistory(wearHistory: WearHistory)

    // 查询某用户某月所有衣服的重复穿搭总次数
    @Query("""
        SELECT 
          strftime('%Y-%m', datetime(timestamp/1000, 'unixepoch')) as month,
          COUNT(*) - COUNT(DISTINCT clothId) as repeat_count
        FROM wear_history
        WHERE uid = :uid AND timestamp BETWEEN :start AND :end
        GROUP BY month
        ORDER BY month DESC
        LIMIT :limit
    """)
    suspend fun getMonthlyRepeatReusageTrend(
        uid: String,
        start: Long,
        end: Long,
        limit: Int = 6
    ): List<MonthlyRepeatReusage>

    // 查询过去N天内的重复穿搭次数
    @Query("""
        SELECT 
          (COUNT(*) - COUNT(DISTINCT clothId)) as repeat_count
        FROM wear_history
        WHERE uid = :uid AND timestamp >= :startTime
    """)
    suspend fun getRepeatWearCountInDays(uid: String, startTime: Long): Int

    @Query("""
        SELECT 
            MAX(wear_count) - 1 AS max_repeat_count
        FROM (
            SELECT COUNT(*) AS wear_count
            FROM wear_history
            WHERE uid = :uid
            GROUP BY clothId
            HAVING COUNT(*) > 1
        )
    """)
    suspend fun getMaxRepeatWearCount(uid: String): Int?
    
}

// 用于折线图的数据类
data class MonthlyReusage(
    val month: String, // e.g.,"2024-05"
    val count: Int
)