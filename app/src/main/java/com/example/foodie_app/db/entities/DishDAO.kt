package com.example.foodie_app.db.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//DAO - Data Access Object
//contains methods for accessing the db
//associates method calls with sql queries
//all queries mus be done on a separate thread

@Dao
interface DishDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //what happens if inserting an entry already in the table? we replace is
    suspend fun insertDish(dish: Dish)


    //we use a flow to automatically update based on changes to the db
    @Query("SELECT * FROM dish")
    fun getAllDishes() : Flow<List<Dish>>
    
}