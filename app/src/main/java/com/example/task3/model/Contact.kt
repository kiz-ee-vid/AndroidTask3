package com.example.task3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Contact(
    @PrimaryKey
    var id: String,
    var firstName: String?,
    var lastName: String?,
    var number: String?,
    var email: String?
)
