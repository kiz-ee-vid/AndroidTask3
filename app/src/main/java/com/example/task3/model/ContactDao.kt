package com.example.task3.model

import androidx.room.*
import com.example.task3.model.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact")
    fun getAll(): MutableList<Contact>

    @Query("SELECT * FROM contact WHERE id = :id")
    fun getById(id: String): Contact?

    @Query("SELECT * FROM contact WHERE number = :number")
    fun getByNumber(number: String): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: Contact?)
}