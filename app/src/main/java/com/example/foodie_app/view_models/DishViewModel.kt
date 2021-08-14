package com.example.foodie_app.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodie_app.db.entities.Dish
import com.example.foodie_app.db.entities.DishDAO
import kotlinx.coroutines.launch

class DishViewModel(private val dishDao: DishDAO): ViewModel() {



    private fun insertDish(newDish:Dish) {
        viewModelScope.launch {
            dishDao.insertDish(newDish)
        }
    }

    private fun getNewDishObj(newName:String, newDate:String, newLocation: String, newNotes: String, newPhoto: String) : Dish{
        return Dish(
            name = newName,
            date = newDate,
            location = newLocation,
            notes = newNotes,
            photoPath = newPhoto
        )
    }

    fun addNewDish(newName:String, newDate:String, newLocation: String, newNotes: String, newPhoto: String){
        val newDish = getNewDishObj(newName, newDate, newLocation, newNotes, newPhoto)
        insertDish(newDish)
    }

    fun isEntryValid(newName: String):Boolean = newName.isNotBlank()




}

//accepts as a parameter the dependencies needed to create our DishViewModel aka the DishRepository
class DishViewModelFactory(private val dishDao: DishDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishViewModel(dishDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
