package com.celdevLabs.foodie_app.repositories

import androidx.annotation.WorkerThread
import com.celdevLabs.foodie_app.db.entities.Dish
import com.celdevLabs.foodie_app.db.entities.DishDAO
import kotlinx.coroutines.flow.Flow

//repository manages backend queries so your viewModel and front end doesn't need to.
class DishRepository(private val dishDAO: DishDAO) {

    //Room executes queries on a separate thread by default.
    val allDishes: Flow<List<Dish>> = dishDAO.getAllDishes()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addDish(newDish:Dish) {
        dishDAO.insertDish(newDish)
    }

}