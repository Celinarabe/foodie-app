package com.example.foodie_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodie_app.entities.Dish
import com.example.foodie_app.entities.DishDAO


@Database(
    entities = [
        Dish::class
    ],
    version = 2
)

abstract class DishDatabase : RoomDatabase() {

    abstract fun dishDAO(): DishDAO

    //create db singleton (w/companion keyword) in a thread safe way
    companion object  {
        @Volatile //volatile keyword - we expect this variable to be modified by multiple threads
        private var INSTANCE:DishDatabase? = null
        fun getInstance(context: Context): DishDatabase {
            synchronized(this) { //synchronized kw takes in a lock which says only one thread at a time through this function

                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DishDatabase::class.java,
                    "quote_db"
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                    println("database instantiated")
                }
            }
        }
    }
}