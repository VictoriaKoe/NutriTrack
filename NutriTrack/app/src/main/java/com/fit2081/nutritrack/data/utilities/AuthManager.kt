package com.fit2081.nutritrack.data.utilities

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * AuthManager is responsible for central authority for authentication state
 * Applying Singleton pattern
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
object AuthManager {

    /**
     * References:
     * The following code from lines 19-39 was taken from the following ed lesson with some modifications.
     *  Lab 8: Part 3: Authentication Manager: https://edstem.org/au/courses/20813/lessons/80772/slides/547303
     **/

    // mutableStateOf - holds curr user's id
    val _userId: MutableState<String?> = mutableStateOf(null)

    /**
     * Login NutriTrack app
     *
     * @param userId the user ID
     */
    // login
    // update UI where _userId is observed
    // set userId with provided id after user login
    fun login(userId: String?){
        // save logged in user id
        _userId.value = userId
    }

    /**
     * Logout from NutriTrack app
     **/
    // user logout, reset val=null
    // updates UI
    fun logout() {
        _userId.value = null
    }

    /**
     * Function to retrieve current logged-in user’s ID
     *
     * @return the current logged-in user’s ID otherwise null
     */
    // Returns the current logged-in user’s ID or else null
    // fetch user-specific data/check login status
    fun getCurrentUserId(): String? {
        return _userId.value
    }
}