package com.example.foodie_app.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//Room creates a table for each class that has @Entity annotation.
//Fields in the class correspond to columns in the table
@Entity
data class Dish (
    var name: String,
    var date: String,
    var location: String,
    var notes: String,
    var photoPath: String
)
{
    //every entity needs a primary key
    @PrimaryKey(autoGenerate = true)
    var idx: Int = 0
}
