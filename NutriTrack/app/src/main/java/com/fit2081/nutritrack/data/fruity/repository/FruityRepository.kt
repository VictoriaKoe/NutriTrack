package com.fit2081.nutritrack.data.fruity.repository

import com.fit2081.nutritrack.data.fruity.network.APIService
import com.fit2081.nutritrack.data.fruity.network.ResponseModel

/**
 * FruityRepository is a class handling fruits data operations acts as intermediary
 * between ViewModel and API service
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
class FruityRepository {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 2b: Coding (Currency Exchange Rate): https://edstem.org/au/courses/20813/lessons/80448/slides/544885
     **/

    // create API service instance -> network requests
    private val apiService = APIService.create()

    /**
     * Retrieve fruit data from API service
     *
     * @param name fruit name acts as url path to be searched
     *
     * @return A ResponseModel containing exchange rate data, or null if the request fails
     */
    // Use an if-statement to check the response and whether it is successful!
    suspend fun getFruitData(name: String): ResponseModel? {
        val response = apiService.getFruitData(name)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

}