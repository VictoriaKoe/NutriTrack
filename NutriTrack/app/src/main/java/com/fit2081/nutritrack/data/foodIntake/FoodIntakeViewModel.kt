package com.fit2081.nutritrack.data.foodIntake

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrack.data.patient.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * FoodIntakeViewModel class, a bridge between the UI and the Repository, handles UI logic
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class FoodIntakeViewModel(context: Context): ViewModel() {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // creates a repo object that will be used to interact with the database
    private val foodIntakeRepo = FoodIntakeRepository(context)

    /**
     * Inserts a new [FoodIntake] instance into database
     *
     * @param foodIntake [FoodIntake] instance
     */
    // Inserts a new food intake instance into db
    fun insert(foodIntake: FoodIntake) = viewModelScope.launch(Dispatchers.IO) {
        foodIntakeRepo.insert(foodIntake)
    }

    /**
     * Updates an existing [FoodIntake] instance
     *
     * @param foodIntake [FoodIntake] instance
     */
    // Updates an existing food intake db
    fun update(foodIntake: FoodIntake) = viewModelScope.launch(Dispatchers.IO) {
        foodIntakeRepo.update(foodIntake)
    }

    /**
     * Updates an existing FoodIntake instance by user id
     *
     * @param fruits fruits checkbox
     * @param vegetables vegetables checkbox
     * @param grains grains checkbox
     * @param redMeat red meat checkbox
     * @param seafood seafood checkbox
     * @param poultry poultry checkbox
     * @param fish fish checkbox
     * @param eggs eggs checkbox
     * @param persona selected persona
     * @param mealTime meal time selection
     * @param wakeUpTime wake up time selection
     * @param sleepTime sleep time selection
     */
    fun updateFoodById(fruits: Boolean,
                       vegetables: Boolean,
                       grains: Boolean,
                       redMeat: Boolean,
                       seafood: Boolean,
                       poultry: Boolean,
                       fish: Boolean,
                       eggs: Boolean,
                       nutsSeeds: Boolean,
                       persona: String,
                       mealTime: String,
                       sleepTime: String,
                       wakeUpTime: String,
                       userID: Int?) = viewModelScope.launch(Dispatchers.IO) {
        foodIntakeRepo.updateFoodById(
            fruits,
            vegetables,
            grains,
            redMeat,
            seafood,
            poultry,
            fish,
            eggs,
            nutsSeeds,
            persona,
            mealTime,
            sleepTime,
            wakeUpTime,
            userID
        )
    }

    /**
     * Retrieves all FoodIntake objects
     * @return a flow of list of [FoodIntake] objects
     */
    // Retrieves all food intake data from db.
    fun getAllFoodIntakes(): Flow<List<FoodIntake?>> = foodIntakeRepo.getAllFoodIntakes()

    /**
     * Retrieves [FoodIntake] details of the current user
     *
     * @param patientId current patient (user) id
     * @return a flow of list of [FoodIntake] objects
     */
    suspend fun getFoodIntakeByID(patientId: Int?):FoodIntake? {
        return foodIntakeRepo.getFoodIntakeByID(patientId)
    }

    // Deletes a specific food intake entry from db
    fun delete(foodIntake: FoodIntake) = viewModelScope.launch(Dispatchers.IO) {
        foodIntakeRepo.delete(foodIntake)
    }

    /**
     * factory class for creating FoodIntake ViewModel
     */
    // factory class for creating ViewModel
    class FoodIntakeViewModelFactory (context: Context): ViewModelProvider.Factory{
        // get app context
        private val context = context.applicationContext
        override fun<T: ViewModel> create(modelClass: Class<T>) =
            FoodIntakeViewModel(context) as T
    }

}