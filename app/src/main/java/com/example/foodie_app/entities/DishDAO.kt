package com.example.foodie_app.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//DAO - Data Access Object
//contains methods for accessing the db

@Dao
interface DishDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDish(dish: Dish)

    //transaction annotation to ensure no multithreading problems

    @Query("SELECT * FROM dish")
    fun getAllDishes() : MutableList<Dish>
    
}