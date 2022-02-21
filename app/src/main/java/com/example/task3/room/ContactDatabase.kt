package com.example.task3.room

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room

@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao?

    companion object{
    private var sInstance: ContactDatabase? = null

    fun getInstance(context: Context): ContactDatabase {
        if (sInstance == null) {
            sInstance = Room
                .databaseBuilder(context.applicationContext, ContactDatabase::class.java, "example")
                .build()
        }
        return sInstance!!
    }
}
}