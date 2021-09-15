package com.celdevLabs.foodie_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.celdevLabs.foodie_app.db.entities.Dish
import com.celdevLabs.foodie_app.db.entities.DishDAO


@Database(entities = [Dish::class],  version = 12, exportSchema = true)
public abstract class DishDatabase : RoomDatabase() {

    abstract fun dishDAO(): DishDAO

    //create db singleton (w/companion keyword) which prevents multiple instances being created
    companion object  {
        @Volatile //volatile keyword - we expect this variable to be accessed by multiple threads
        private var INSTANCE: DishDatabase? = null

        val migration_11_12: Migration = object: Migration(11,12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE dish ADD COLUMN rating REAL NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): DishDatabase {
            return INSTANCE ?:synchronized(this) { //synchronized kw takes in a lock which says only one thread at a time through this function

                 val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DishDatabase::class.java,
                    "dish_db",

                ).addMigrations(migration_11_12).build()
                INSTANCE = instance
                instance //returning instance
            }
        }
    }
}