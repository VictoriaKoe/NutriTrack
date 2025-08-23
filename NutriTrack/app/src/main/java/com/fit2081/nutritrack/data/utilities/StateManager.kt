package com.fit2081.nutritrack.data.utilities

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AuthManager is responsible for central authority for authentication state
 * Applying Singleton pattern
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class StateManager (
    context: Context
) {

    /**
     * AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in creating singleton pattern
     * class for shared preferences to track the user login session in the following code lines below.
     */

    private val sharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    // MutableStateFlow to represent current authentication state
    private val _authState = MutableStateFlow(isUserLoggedIn())
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    /**
     * Check if user is logged in based on stored preferences
     *
     * @return true if the user has completed it other false
     */
    // Check if user is logged in based on stored preferences
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    companion object {
        private const val PREFS_NAME = "user"
        private const val KEY_IS_LOGGED_IN = "is_log_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_HAS_COMPLETED_QUESTIONNAIRE = "has_completed_questionnaire"
    }

    /**
     * Save user details into stored preferences
     *
     * @return true if the user has completed it other false
     */
    // save user session to track login status
    fun loginUserSession(userId: String) {
        val sharedPref = sharedPreferences.edit()
        sharedPref.putString(KEY_USER_ID, userId)
        sharedPref.putBoolean(KEY_IS_LOGGED_IN, true)
        sharedPref.apply()
    }

    /**
     * Clear user session on logout from stored preferences
     **/
    // clear user session on logout
    fun logout() {
        val sharedPref = sharedPreferences.edit()
        sharedPref.remove(KEY_IS_LOGGED_IN)
        sharedPref.remove(KEY_USER_ID)
        sharedPref.remove(KEY_HAS_COMPLETED_QUESTIONNAIRE)

        sharedPref.apply()

        println(sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false))

        println("Logout completed. Is logged in: ${sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)}")

    }

    /**
     * Function indicating user has completed questionnaire
     *
     * @return true if the user has completed it otherwise false
     **/
    // check user complete questionnaire or not
    fun hasCompletedQuestionnaire(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAS_COMPLETED_QUESTIONNAIRE, false)
    }

    /**
     * Function indicating user has completed questionnaire
     **/
    // complete questionnaire
    fun completedQuestionnaire() {
        val sharedPref = sharedPreferences.edit()
        sharedPref.putBoolean(KEY_HAS_COMPLETED_QUESTIONNAIRE, true)
        sharedPref.apply()
    }

    /**
     * Function to retrieve current logged-in user’s ID in shared preferences
     *
     * @return the current logged-in user’s ID otherwise null
     */
    // Get current user ID
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}



