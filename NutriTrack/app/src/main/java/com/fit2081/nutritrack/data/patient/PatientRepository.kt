package com.fit2081.nutritrack.data.patient

import android.content.Context
import androidx.room.Query
import com.fit2081.nutritrack.data.NutriTrackDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Repository that separates data logic from the rest of NutriTrack app,
 * fetching patient data from local Room DB
 *
 * @param applicationContext the application context
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class PatientRepository(private val applicationContext: Context) {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // create db instance
    val nutriDatabase = NutriTrackDatabase.getDatabase(applicationContext)

    // get dao from db
    val patientDao = nutriDatabase.patientDao()

    /**
     * Inserts a new patient into database (load all patient data in csv for first time)
     *
     * @param patient [Patient] instance
     */
    // insert new patient into db
    suspend fun insert(patient: Patient) {
        // save to local db
        patientDao.insert(patient)
    }

    /**
     * Updates an existing patient
     *
     * @param patient [Patient] instance
     */
    // update all patients data
    suspend fun update(patient: Patient) {
        patientDao.update(patient)
    }

    /**
     * Updates an existing patient details
     *
     * @param username username
     * @param password user's password
     * @param isRegister flag to indicate the user is registered the account or not
     */
    // update patient username and phone of specified user
    suspend fun updatePatientDetails(username: String, password: String, isRegister: Boolean, patientId: Int?) {
        patientDao.updatePatientDetails(username, password, isRegister, patientId)
    }

    /**
     * Retrieves data of specific patient from database based on their ID
     *
     * @return a [Patient] objects based on their user ID
     */
    // retrieves data of specific patient from DB based on their ID
    suspend fun getPatientByID(patientId: Int?): Patient {
        return patientDao.getPatientByID(patientId)
    }

    /**
     * Retrieves all phone number of patients from database
     *
     * @return a [Patient] objects
     */
    // retrieves patients phone number from DB
    suspend fun getAllPatientsPhoneNumber(): List<String> {
        return patientDao.getAllPatientsPhoneNumber()
    }

    /**
     * Retrieves all patients from database
     *
     * @return flow of list of [Patient] objects
     */
    // get all patients from the database as a Flow
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    /**
     * Retrieves all unregistered patients from database
     *
     * @return flow of list of [Patient] objects
     */
    // retrieves all unregistered users
    fun getAllUnregisteredPatient(): Flow<List<Patient>> = patientDao.getAllUnregisteredPatient()

    /**
     * Retrieves all registered patients from database
     *
     * @return flow of list of [Patient] objects
     */
    // retrieves all registered users
    fun getAllRegisteredPatient():  Flow<List<Patient>> = patientDao.getAllRegisteredPatient()

    /**
     * Retrieves all first time registered patients from database
     *
     * @return flow of list of [Patient] objects
     */
    // retrieves all first time users
    fun getAllFirstTimeUserPatient(): Flow<List<Patient>> = patientDao.getAllFirstTimeUserPatient()

    /**
     * Retrieves average HEIFA scores of Male and Female
     *
     * @param gender gender (M/F) need to be queried
     *
     * @return average score of HEIFA scores based on gender (Male and Female) as Double
     */
    // retrieves avg HEIFA scores of Male and Female
    suspend fun getAverageHEIFAScore(gender: String): Double {
        return patientDao.getAverageHEIFAScore(gender)
    }

    // Deletes all patients from db (testing)
    suspend fun deleteAllPatients() {
        // Call the deleteAllPatients() from patient dao
        patientDao.deleteAllPatients()
    }
}