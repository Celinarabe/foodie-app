package com.example.foodie_app.entities

import android.util.Log
import androidx.lifecycle.ViewModel

class DishViewModel: ViewModel() {
    var _dishObj : Dish? = null
    //var currentDishObj: Dish? = _dishObj

    init {
        resetDish()
        Log.d("DishViewModel", "init is run")
    }


    fun setCurrentDishObj(newDish: Dish){
        Log.d("DishViewModel", "setting new ${newDish.name}")
        _dishObj = newDish
        Log.d("DishViewModel", "new dish ${_dishObj?.name ?: "nodish"}")
    }

    private fun resetDish() {
        _dishObj = null
    }
}