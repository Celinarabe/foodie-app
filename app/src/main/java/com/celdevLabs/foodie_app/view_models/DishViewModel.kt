package com.celdevLabs.foodie_app.view_models

import android.util.Log
import androidx.lifecycle.*
import com.celdevLabs.foodie_app.db.entities.Dish
import com.celdevLabs.foodie_app.db.entities.DishDAO
import kotlinx.coroutines.launch

class DishViewModel(private val dishDao: DishDAO): ViewModel() {
    val allDishes : LiveData<List<Dish>> = dishDao.getAllDishes().asLiveData()

    private fun insertDish(newDish:Dish) {
        viewModelScope.launch {
            dishDao.insertDish(newDish)
        }
    }

    private fun getNewDishObj(newName:String, newDate:Long, newLocation: String, newNotes: String, newPhoto: String) : Dish{
        return Dish(
            name = newName,
            date = newDate,
            location = newLocation,
            notes = newNotes,
            dishUri = newPhoto
        )
    }

    fun addNewDish(newName:String, newDate:Long, newLocation: String, newNotes: String, newPhoto: String){
        val newDish = getNewDishObj(newName, newDate, newLocation, newNotes, newPhoto)
        insertDish(newDish)
    }

    fun isEntryValid(newName: String):Boolean = newName.isNotBlank()

    fun getDish(id:Int) : LiveData<Dish> {
        return dishDao.getDish(id).asLiveData()
    }

    fun updateDish(id: Int, newName:String, newDate:Long, newLocation: String, newNotes: String, newPhoto: String) {
        val updatedDish = getUpdatedItemEntry(id, newName, newDate, newLocation, newNotes, newPhoto)
        updateDish(updatedDish)
    }

    private fun getUpdatedItemEntry(id:Int, newName:String, newDate:Long, newLocation: String, newNotes: String, newPhoto: String):Dish {
        return Dish(
            idx = id,
            name = newName,
            date = newDate,
            location = newLocation,
            notes = newNotes,
            dishUri = newPhoto

        )
    }

    private fun updateDish(dish:Dish) {
        Log.d("DishViewModel", "in view model ${dish}")
        viewModelScope.launch {
            dishDao.updateDish(dish)
        }
    }

    fun deleteDish(dish:Dish) {
        viewModelScope.launch {
            dishDao.delete(dish)
        }
    }






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
