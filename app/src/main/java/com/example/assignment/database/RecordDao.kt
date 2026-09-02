package com.example.assignment.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM records WHERE username = :username ORDER BY recordDateMillis DESC")
    fun getLocalRecords(username: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE username = :username AND categoryName = :category ORDER BY recordDateMillis DESC")
    fun getLocalRecordsByCategory(username: String, category: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id LIMIT 1")
    suspend fun getLocalRecordById(id: String): Record?

    @Query("SELECT * FROM records WHERE username = :username ORDER BY recordDateMillis DESC LIMIT :limit")
    fun getRecentRecords(username: String, limit: Int): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE username = :username AND (title LIKE '%' || :query || '%' OR provider LIKE '%' || :query || '%') ORDER BY recordDateMillis DESC")
    fun searchRecords(username: String, query: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE username = :username AND categoryName = :category AND (title LIKE '%' || :query || '%' OR provider LIKE '%' || :query || '%') ORDER BY recordDateMillis DESC")
    fun searchRecordsInCategory(username: String, category: String, query: String): Flow<List<Record>>

    @Query("SELECT COUNT(*) FROM records WHERE username = :username AND categoryName = :category")
    fun getCategoryCount(username: String, category: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<Record>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteRecord(id: String)
}
