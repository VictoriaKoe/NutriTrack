package com.fit2081.nutritrack.data.foodIntake

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fit2081.nutritrack.data.patient.Patient
import kotlinx.coroutines.flow.Flow

/**
 * FoodIntakeDao is an interface class that defines the DAO for the Food Intake entity.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Dao
interface FoodIntakeDao {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    /**
     * Inserts a new [FoodIntake] instance into database
     *
     * @param foodIntake [FoodIntake] instance
     */
    // Inserts a new food intake instance into db
    @Insert
    suspend fun insert(foodIntake: FoodIntake)

    /**
     * Updates an existing [FoodIntake] instance
     *
     * @param foodIntake [FoodIntake] instance
     */
    // Updates an existing food intake db
    @Update
    suspend fun update(foodIntake: FoodIntake)

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
    // Updates curr user food intake response in db
    @Query("UPDATE food_intake SET fruits =:fruits, " +
            "vegetables =:vegetables, grains=:grains," +
            "redMeat =:redMeat, seafood =:seafood," +
            "poultry =:poultry, fish =:fish," +
            "eggs =:eggs, nutsSeeds=:nutsSeeds," +
            "persona =:persona, mealTime =:mealTime," +
            "mealTime =:mealTime,sleepTime=:sleepTime," +
            "wake_up_time= :wakeUpTime WHERE userID =:userID")
    suspend fun updateFoodById(
        fruits: Boolean,
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
        userID: Int?
    )

    /**
     * Retrieves all food intake data from database
     */
    // Retrieves all food intake data from db.
    // The return type is a Flow, which is a data stream that can be observed for changes.
    @Query("SELECT * FROM food_intake")
    fun getAllFoodIntakes(): Flow<List<FoodIntake?>>

    /**
     *Retrieves food intake of specific users from database based on their ID
     */
    // Retrieves food intake of specific users from DB based on their ID
    @Query("SELECT * FROM food_intake WHERE userID = :patientId")
    suspend fun getFoodIntakeByID (patientId: Int?): FoodIntake? // async fun

    // Deletes a specific food intake entry from db (testing)
    @Delete
    suspend fun delete(foodIntake: FoodIntake)

}