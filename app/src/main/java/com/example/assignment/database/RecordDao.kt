package com.example.assignment.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert
    suspend fun insert(record: Record): Long

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * FROM records ORDER BY recordDateMillis DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE category = :category ORDER BY recordDateMillis DESC")
    fun getRecordsByCategory(category: RecordCategory): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: Long): Record?

    @Query("SELECT * FROM records ORDER BY uploadedAtMillis DESC LIMIT :limit")
    fun getRecentRecords(limit: Int = 3): Flow<List<Record>>

    @Query(
        """SELECT * FROM records 
           WHERE title LIKE '%' || :query || '%' 
           ORDER BY recordDateMillis DESC"""
    )
    fun searchRecords(query: String): Flow<List<Record>>

    @Query(
        """SELECT * FROM records 
           WHERE category = :category AND title LIKE '%' || :query || '%' 
           ORDER BY recordDateMillis DESC"""
    )
    fun searchRecordsInCategory(category: RecordCategory, query: String): Flow<List<Record>>

    @Query("SELECT COUNT(*) FROM records WHERE category = :category")
    fun getCategoryCount(category: RecordCategory): Flow<Int>
}