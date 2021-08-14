package com.example.foodie_app

import android.app.Application
import com.example.foodie_app.db.DishDatabase

class DishApplication: Application() {
    //use by lazy so the db and repository are created when needed, not on app startup
    val database by lazy { DishDatabase.getInstance(this)}
}