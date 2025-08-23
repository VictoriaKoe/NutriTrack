package com.fit2081.nutritrack.data.patient

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fit2081.nutritrack.data.foodIntake.FoodIntake
import kotlinx.coroutines.flow.Flow

/**
 * PatientDao is an interface class that defines the data access object (DAO) for the Patient entity.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Dao
interface PatientDao {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    /**
     * Inserts a new patient into database (load all patient data in csv for first time)
     *
     * @param patient [Patient] instance
     */
    // Inserts a new patient into db (load all patient data in csv for first time)
    // suspend - coroutine function that can be paused and resumed at a later time
    // indicate that the function will be called from a coroutine.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: Patient)

    /**
     * Updates an existing patient
     *
     * @param patient [Patient] instance
     */
    // Updates an existing patient db
    @Update
    suspend fun update(patient: Patient)

    /**
     * Updates an existing patient details
     *
     * @param username username
     * @param password user's password
     * @param isRegister flag to indicate the user is registered the account or not
     */
    // update patient details
    @Query("UPDATE patients SET username =:username, " +
            "password =:password, isRegister =:isRegister WHERE userID =:patientId")
    suspend fun updatePatientDetails(username: String, password: String, isRegister: Boolean, patientId: Int?)

    /**
     * Retrieves all patients from database
     *
     * @return flow of list of [Patient] objects
     */
    // Retrieves all patients from db, ordered by their ID in ascending order.
    // The return type is a Flow, which is a data stream that can be observed for changes.
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    /**
     * Retrieves all unregistered patients from database
     *
     * @return flow of list of [Patient] objects
     */
    // Retrieves current user registration status
    @Query("SELECT * FROM patients WHERE isRegister == False")
    fun getAllUnregisteredPatient(): Flow<List<Patient>>

    /**
     * Retrieves all registered patients from database
     *
     * @return flow of list of [Patient] objects
     */
    @Query("SELECT * FROM patients WHERE isRegister == True")
    fun getAllRegisteredPatient(): Flow<List<Patient>>

    /**
     * Retrieves all first time registered patients from database
     *
     * @return flow of list of [Patient] objects
     */
    @Query("SELECT * FROM patients WHERE isFirstTimeUser == True")
    fun getAllFirstTimeUserPatient(): Flow<List<Patient>>

    /**
     * Retrieves  data of specific patient from database based on their ID
     *
     * @return a [Patient] objects based on their user ID
     */
    // Retrieves data of specific patient from DB based on their ID
    @Query("SELECT * FROM patients WHERE userID = :patientId")
    suspend fun getPatientByID(patientId: Int?): Patient

    /**
     * Retrieves all phone number of patients from database
     *
     * @return a [Patient] objects
     */
    // Retrieves all phone number of patients from DB
    @Query("SELECT phoneNumber FROM patients")
    suspend fun getAllPatientsPhoneNumber(): List<String>

    /**
     * Get count of patients (needed to check if database is empty)
     *
     * @return number of patient as Int
     */
    // Get count of patients (needed to check if database is empty)
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    /**
     * Retrieves average HEIFA scores of Male and Female
     *
     * @param gender gender (M/F) need to be queried
     *
     * @return average score of HEIFFA scores based on gender (Male and Female) as Double
     */
    // Retrieves avg HEIFA scores of Male and Female
    @Query("SELECT AVG(totalHEIFAScore) FROM patients WHERE gender =:gender ")
    suspend fun getAverageHEIFAScore(gender: String): Double

    // Deletes all patients from db (testing)
    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()
}


