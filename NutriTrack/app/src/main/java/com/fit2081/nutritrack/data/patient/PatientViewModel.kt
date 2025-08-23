package com.fit2081.nutritrack.data.patient

import android.content.Context
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * PatientViewModel class, a bridge between the UI and the Repository, handles UI logic
 *
 * @param context context of the activity
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class PatientViewModel(context: Context) : ViewModel() {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // creates a repo object that will be used to interact with the database
    private val patientRepo = PatientRepository(context)

    /**
     * Inserts a new patient into database (load all patient data in csv for first time)
     *
     * @param patient [Patient] instance
     */
    // insert patient into the repo
    fun insertPatient(patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        patientRepo.insert(patient)
    }

    /**
     * Updates an existing patient (user)
     *
     * @param patient [Patient] instance
     */
    // update patient detail into repo
    fun updatePatient(patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        patientRepo.update(patient)
    }

    /**
     * Updates specified details of a patient (user)
     *
     * @param username username
     * @param password user's password
     * @param isRegister flag to indicate the user is registered the account or not
     */
    // update patient username and phone of specified user
    fun updatePatientDetails(username: String, password: String, isRegister: Boolean, patientId: Int?) =
        viewModelScope.launch(Dispatchers.IO) {
            patientRepo.updatePatientDetails(username, password, isRegister, patientId)
        }

    /**
     * Retrieves all patients (users)
     *
     * @return a flow of list of [Patient] objects
     */
    // gets all the patients stored in the repo
    val allPatients: Flow<List<Patient>> = patientRepo.getAllPatients()

    /**
     * Retrieves all unregistered patients (users)
     *
     * @return a flow of list of unregistered [Patient] objects
     */
    // retrieves all unregistered users
    fun getAllUnregisteredPatients(): Flow<List<Patient>> = patientRepo.getAllUnregisteredPatient()

    /**
     * Retrieves all registered patients (users)
     *
     * @return a flow of list of registered [Patient] objects
     */
    // all registered users
    fun getAllRegisteredPatients(): Flow<List<Patient>> = patientRepo.getAllRegisteredPatient()

    /**
     * Retrieves all first time registered patients (users)
     *
     * @return a flow of list of registered [Patient] objects
     */
    fun getAllFirstTimeUserPatient(): Flow<List<Patient>> = patientRepo.getAllFirstTimeUserPatient()

    /**
     * Retrieves patient details based on their user ID
     *
     * @return a [Patient] objects based on their user ID
     */
    // get specific patient from DB based on their ID
    suspend fun getPatientByID(patientID: Int?): Patient {
        return patientRepo.getPatientByID(patientID)
    }

    /**
     * Retrieves patient details based on their user ID
     *
     * @return a [Patient] objects based on their user ID
     */
    // get specific patient from DB based on their ID
    suspend fun getAllPatientsPhoneNumber(): List<String> {
        return patientRepo.getAllPatientsPhoneNumber()
    }

    /**
     * Retrieves average HEIFA scores of Male and Female
     *
     * @param gender gender (M/F) need to be queried
     *
     * @return average score of HEIFA scores based on gender (Male and Female) as Double
     */
    suspend fun getAverageHEIFAScore(gender: String): Double {
        return patientRepo.getAverageHEIFAScore(gender)
    }

    // test delete data in db
    fun deleteDatabase() {
        viewModelScope.launch {
            patientRepo.deleteAllPatients()
        }
    }

    /**
     * Factory class for creating ViewModel
     **/
    // factory class for creating ViewModel
    class PatientViewModelFactory(context: Context) : ViewModelProvider.Factory {
        // get app context
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            PatientViewModel(context) as T
    }
}