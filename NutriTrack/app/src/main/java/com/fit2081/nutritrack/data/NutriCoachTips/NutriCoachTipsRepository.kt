package com.fit2081.nutritrack.data.NutriCoachTips

import android.content.Context
import com.fit2081.nutritrack.data.NutriTrackDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Repository that separates data logic from the rest of NutriTrack app,
 * fetching genAI response data from local Room DB
 */
class NutriCoachTipsRepository(private val applicationContext: Context){

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // create db instance
    val nutriDatabase = NutriTrackDatabase.getDatabase(applicationContext)

    // get dao from db
    val nutriCoachTipsDao = nutriDatabase.nutriCoachTipsDao()

    // insert new genAI response into db
    suspend fun insert(nutriCoachTips: NutriCoachTips) {
        nutriCoachTipsDao.insert(nutriCoachTips)
    }

    // Retrieves genAI response of specific users from DB based on their ID
     fun getAllResponsesById (patientId: Int?): Flow<List<NutriCoachTips>> {
        return nutriCoachTipsDao.getAllResponsesById(patientId)
     }

    // delete genAI response from db
    suspend fun delete(){
        nutriCoachTipsDao.deleteAllResponse()
    }

}