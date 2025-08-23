package com.fit2081.nutritrack.data.fruity.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * APIService is an interface class providing a clean API that the repository layer can use to fetch fruit data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
interface APIService {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 2b: Coding (Currency Exchange Rate): https://edstem.org/au/courses/20813/lessons/80448/slides/544885
     *  Jetpack compose | Network requests with Retrofit:
     *      https://medium.com/@marelso/jetpack-compose-network-requests-with-retrofit-99314caf249e
     **/

    /**
     * Endpoints relative to base URL
     *
     * @return [ResponseModel] instance
     */
    // endpoints relative to base URL
    @GET("api/fruit/{name}")
    suspend fun getFruitData(
        // added as path parameters to the URL
        @Path("name") name: String
    ): Response<ResponseModel> // return type, using Retrofit's Response wrapper

    /**
     * Companion obj -> provide a factory method for creating APIService instance
     */
    companion object {
        /**
         * base URL for Fruity API
         */
        var BASE_URL = "https://www.fruityvice.com/"

        /**
         * Creates APIService instance using Retrofit
         *
         * @return implementation APIService for making API calls
         */
        fun create (): APIService {
            val retrofit = Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }

}