package com.example.ass4.database

import androidx.room.*
import com.example.ass4.database.WearHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WearHistoryDao {

    // add new wearing history into table
    @Insert
    suspend fun insertWearHistory(wearHistory: WearHistory)

    // Calculate the repeated usage behaviour on monthly basis, while user is able to
    // select the start and end month
    @Query("""
    SELECT month, SUM(repeat_count) as repeat_count
    FROM (
        SELECT 
            strftime('%Y-%m', datetime(timestamp / 1000, 'unixepoch')) AS month,
            clothId,
            COUNT(*) - 1 AS repeat_count
        FROM wear_history
        WHERE uid = :uid AND timestamp BETWEEN :start AND :end
        GROUP BY month, clothId
        HAVING COUNT(*) > 1
    )
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


    // Accept user id and a timestamp, calculate the repeat wearing behavior from
    // the selected time to now
    @Query("""
        SELECT 
          (COUNT(*) - COUNT(DISTINCT clothId)) as repeat_count
        FROM wear_history
        WHERE uid = :uid AND timestamp >= :startTime
    """)
    suspend fun getRepeatWearCountInDays(uid: String, startTime: Long): Int

    // get the max repeat wearing count of a single outfit
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

    // Get all available months from wear history for a specific user
    @Query("""
        SELECT DISTINCT strftime('%Y-%m', datetime(timestamp/1000, 'unixepoch')) as month
        FROM wear_history
        WHERE uid = :uid
        ORDER BY month DESC
    """)
    suspend fun getAllAvailableMonths(uid: String): List<String>
}

// data class to enable monthly usage data count for line chart
data class MonthlyRepeatReusage(
    val month: String,
    val repeat_count: Int
)