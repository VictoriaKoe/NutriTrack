package com.fit2081.nutritrack.data.foodIntake

import android.content.Context
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fit2081.nutritrack.data.NutriTrackDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Repository that separates data logic from the rest of NutriTrack app,
 * fetching food intake data from local Room DB
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class FoodIntakeRepository(private val applicationContext: Context) {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // create db instance
    val nutriDatabase = NutriTrackDatabase.getDatabase(applicationContext)

    // get dao from db
    val foodIntakeDao = nutriDatabase.foodIntakeDao()

    /**
     * Inserts a new [FoodIntake] instance into database
     *
     * @param foodIntake [FoodIntake] instance
     */
    // Inserts a new food intake instance into db
    suspend fun insert(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    /**
     * Updates an existing [FoodIntake] instance
     *
     * @param foodIntake [FoodIntake] instance
     */
    // Updates an existing food intake db
    suspend fun update(foodIntake: FoodIntake) {
        foodIntakeDao.update(foodIntake)
    }

    /**
     * Updates the current user's existing [FoodIntake] response
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
    // Updates an existing food intake response of the curr user
    suspend fun updateFoodById(fruits: Boolean,
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
    userID: Int?) {
        foodIntakeDao.updateFoodById(
            fruits, vegetables, grains, redMeat,
            seafood, poultry, fish, eggs,
            nutsSeeds, persona, mealTime, sleepTime,
            wakeUpTime, userID
        )
    }

    /**
     * Retrieves all food intake data from database
     */
    // Retrieves all food intake data from db.
    fun getAllFoodIntakes(): Flow<List<FoodIntake?>> = foodIntakeDao.getAllFoodIntakes()

    /**
     *Retrieves food intake of specific users from database based on their ID
     */
    // Retrieves food intake of specific users from DB based on their ID
    suspend fun getFoodIntakeByID (patientId: Int?):FoodIntake? {
        return foodIntakeDao.getFoodIntakeByID(patientId)
    }

    // Deletes a specific food intake entry from db
    suspend fun delete(foodIntake: FoodIntake) {
        foodIntakeDao.delete(foodIntake)
    }

}