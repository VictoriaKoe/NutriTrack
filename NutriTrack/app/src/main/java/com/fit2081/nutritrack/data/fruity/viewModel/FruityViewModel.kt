package com.fit2081.nutritrack.data.fruity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrack.data.fruity.network.ResponseModel
import com.fit2081.nutritrack.data.fruity.repository.FruityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * FruityViewModel is a class acts as bridge between UI and repository,
 * mainly handle UI logic for any state changes
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class FruityViewModel(application: Application): AndroidViewModel(application){
//    AndroidViewModel, giving access to applicationContext
//    repository relies on Room, which requires context

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 9: Tweet Post-Step 5: ViewModel: https://edstem.org/au/courses/20813/lessons/81098/slides/550977
     **/

    /** AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in enhancing
     * API service to render on screen using ViewModel in the following code lines below.
     */

    // get repo obj
    // ViewModel never directly touches the database or network.
    val repositoryFruity: FruityRepository = FruityRepository()

    // private mutable state flow - stores curr list of posts
    // stateflow - observe changes to data over time
    private val _aFruit = MutableStateFlow<ResponseModel?>(null)

    // public, read-only version that the UI can observe.
    // enables ui reacts to changes in post data while
    // prevent direct mutation from outside this cls
    val aFruit: StateFlow<ResponseModel?>
        get() = _aFruit.asStateFlow()

    // load data
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // error validate
    private val _error = MutableStateFlow<String>("")
    val error: StateFlow<String> = _error.asStateFlow()

    /**
     * Get fruit data from Fruity API
     *
     * @param fruitName name acts as endpoint path to retrieve the data
     */
    // get fruit data from Fruity API
    fun getFruityData(fruitName: String) {
        viewModelScope.launch (Dispatchers.IO){
            // launches a coroutine to fetch fruit data
            try {
                _isLoading.value = true
                _error.value = ""

                val data = repositoryFruity.getFruitData(fruitName)
                println("Fruit: $data")
                _aFruit.value = data
                println("${_aFruit.value} loaded")
                Log.d("TAG", "load data")

                // check data is null
                if (data == null) {
                    _error.value = "Fruit not found"
                    Log.d("TAG", "load no data")

                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                _aFruit.value = null
            } finally { // load finish
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear all fruit data obtained from Fruity API
     *
     */
    // clear data from API service
    fun clearFruityData() {
        _aFruit.value = null
        _error.value = ""
        _isLoading.value = false
    }
}