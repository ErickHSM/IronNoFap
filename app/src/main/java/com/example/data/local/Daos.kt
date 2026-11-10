package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DayLogDao {
    @Query("SELECT * FROM day_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DayLogEntity>>

    @Query("SELECT * FROM day_logs WHERE date = :date LIMIT 1")
    suspend fun getLogByDate(date: String): DayLogEntity?

    @Query("SELECT * FROM day_logs WHERE date = :date LIMIT 1")
    fun observeLogByDate(date: String): Flow<DayLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DayLogEntity)

    @Query("DELETE FROM day_logs WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("SELECT COUNT(*) FROM day_logs WHERE status = 'CLEAN'")
    fun getCleanDaysCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM day_logs WHERE status = 'RELAPSE'")
    fun getRelapseDaysCount(): Flow<Int>
}

@Dao
interface PunishmentDao {
    @Query("SELECT * FROM punishments ORDER BY isCompleted ASC, dateAssigned DESC")
    fun getAllPunishments(): Flow<List<PunishmentEntity>>

    @Query("SELECT * FROM punishments WHERE isCompleted = 0 ORDER BY dateAssigned DESC")
    fun getActivePunishments(): Flow<List<PunishmentEntity>>

    @Query("SELECT COUNT(*) FROM punishments WHERE isCompleted = 0")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(punishment: PunishmentEntity)

    @Update
    suspend fun update(punishment: PunishmentEntity)

    @Query("UPDATE punishments SET isCompleted = :completed, completedAt = :completedAt WHERE id = :id")
    suspend fun markCompleted(id: String, completed: Boolean, completedAt: Long?)

    @Query("DELETE FROM punishments WHERE id = :id")
    suspend fun delete(id: String)
}
