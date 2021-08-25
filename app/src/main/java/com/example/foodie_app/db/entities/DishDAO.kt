package com.example.foodie_app.db.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

//DAO - Data Access Object
//contains methods for accessing the db
//associates method calls with sql queries
//all queries mus be done on a separate thread

@Dao
interface DishDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //what happens if inserting an entry already in the table? we replace is
    suspend fun insertDish(dish: Dish)

    @Update
    suspend fun updateDish(dish: Dish)


    //we use a flow to automatically update based on changes to the db
    //since its a flow return type, Kotlin knows to run on a coroutine
    @Query("SELECT * FROM dish ORDER BY idx DESC")
    fun getAllDishes() : Flow<List<Dish>>

    @Query("SELECT * FROM dish WHERE idx == :id")
    fun getDish(id: Int): Flow<Dish>
    
}