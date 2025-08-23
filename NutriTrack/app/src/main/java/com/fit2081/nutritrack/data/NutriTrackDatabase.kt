package com.fit2081.nutritrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fit2081.nutritrack.data.NutriCoachTips.NutriCoachTips
import com.fit2081.nutritrack.data.NutriCoachTips.NutriCoachTipsDao
import com.fit2081.nutritrack.data.foodIntake.FoodIntake
import com.fit2081.nutritrack.data.foodIntake.FoodIntakeDao
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientDao
import com.fit2081.nutritrack.data.utilities.getAllUserDataFromCSV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An abstract class representing central point of database for managing the NutriTrack app' local data
 * Database schema (3 entities): [Patient],[foodIntake], [NutriCoachTips]
 * version = 1, exportSchema=false
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Database(entities = [Patient::class, FoodIntake::class, NutriCoachTips::class], version = 1, exportSchema = false)
abstract class NutriTrackDatabase: RoomDatabase() {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    /**
     * Provides access to PatientDao interface for performing db ops of Patient entities
     *
     * @return [PatientDao] instance
     */
    // patient dao
    abstract fun patientDao(): PatientDao

    /**
     * Provides access to FoodIntakeDao interface for performing db ops of FoodIntake entities
     *
     * @return [FoodIntakeDao] instance
     */
    // food intake dao
    abstract fun foodIntakeDao(): FoodIntakeDao

    /**
     * Provides access to NutriCoachTipsDao interface for performing db ops of NutriCoachTips entities
     *
     * @return [NutriCoachTipsDao] instance
     */
    // nutricoach tips dao
    abstract fun nutriCoachTipsDao(): NutriCoachTipsDao

    companion object {
        /**
         * This is a volatile variable that holds the database instance.
         * It is volatile so that it is immediately visible to all threads.
         */
        @Volatile // thread-safe access
        private var Instance: NutriTrackDatabase? = null

        /** AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
         * CSV migration to Room DB for load data once during first launch app in the following code lines below.
         */

        /**
         * Returns the NutriTrack database instance.
         * If the instance is null, it creates a new database instance.
         *
         * @param context The context of the application
         *
         * @return instance of NutriTrack Database
         */
        fun getDatabase(context: Context): NutriTrackDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            //synchronized means that only one thread can access this code at a time.
            //hospital_database is the name of the database.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    NutriTrackDatabase::class.java,
                    "nutritrack_database")
                    .addCallback(object: RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Only load CSV data when database is first created
                            CoroutineScope(Dispatchers.IO).launch {
                                loadInitialData(context, getDatabase(context))
                            }
                        }
                    })
                    .build()
                    .also { Instance = it } // ret nutritrack db instance
            }
        }
    }
}

/**
 * Load CSV data only on first database creation
 *
 * @param context context of the activity
 * @param database NutriTrack app database
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// Load CSV data only on first database creation
private suspend fun loadInitialData(context: Context, database: NutriTrackDatabase) {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 9, Multi-source Data + MVVM + API Integration -
     *  Step 5: ViewModel: https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    val patientDao = database.patientDao()

    // Check if database is empty before loading data
    if (patientDao.getPatientCount() == 0) {
        try {
            // fetch all data from CSV
            val csvPatientData = getAllUserDataFromCSV(context, "data.csv")

            // convert csv data to Patient entities and insert into db
            val patientList = csvPatientData.map { user ->
                Patient(
                    userID = user.userID.toInt(),
                    phoneNumber = user.phoneNum,
                    gender = user.gender,
                    totalHEIFAScore = user.foodCategoryHEIFAScore.totalScore,
                    discretionaryHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Discretionary"),
                    vegetableHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Vegetables"),
                    fruitHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Fruit"),
                    grainAndCerealsHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Grainsandcereals"),
                    wholeGrainsHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Wholegrains"),
                    meatAndAlternativeHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Meatandalternatives"),
                    sodiumHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Sodium"),
                    alcoholHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Alcohol"),
                    waterHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Water"),
                    sugarHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("Sugar"),
                    saturatedFatHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("SaturatedFat"),
                    unsaturatedFatHEIFAScore = user.foodCategoryHEIFAScore.foodCategoryScoreMap.getValue("UnsaturatedFat"),
                )
            }

            // save patient list to local db
            if (patientList.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    patientList.forEach{patient ->
                        patientDao.insert(patient)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to load initial data from CSV: ${e.message}")
        }
    }
}



