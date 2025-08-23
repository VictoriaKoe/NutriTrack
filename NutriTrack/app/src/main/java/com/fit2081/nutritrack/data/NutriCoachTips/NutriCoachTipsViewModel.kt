package com.fit2081.nutritrack.data.NutriCoachTips

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * NutriCoachTipsViewModel class, a bridge between the UI and the Repository, handles UI logic
 */
class NutriCoachTipsViewModel(context: Context): ViewModel() {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // creates a repo object that will be used to interact with the database
    private val nutriCoachRepo = NutriCoachTipsRepository(context)

    /**
     * Insert a FoodIntake instance
     */
    // Inserts GenAI response of the current user prompts
    fun insert(nutriCoachTips: NutriCoachTips) = viewModelScope.launch {
        nutriCoachRepo.insert(nutriCoachTips)
    }

    /**
     * Retrieves genAI response of specific users
     * @return Flow of list of [NutriCoachTips] instance of the current user
     */
    // Retrieves genAI response of specific users from DB based on their ID
    fun getAllResponsesById (patientId: Int?): Flow<List<NutriCoachTips>> =
        nutriCoachRepo.getAllResponsesById(patientId)

    /**
     * Factory class for creating ViewModel
     */
    // factory class for creating ViewModel
    class NutriCoachViewModelFactory(context: Context) : ViewModelProvider.Factory {
        // get app context
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            NutriCoachTipsViewModel(context) as T
    }

}


