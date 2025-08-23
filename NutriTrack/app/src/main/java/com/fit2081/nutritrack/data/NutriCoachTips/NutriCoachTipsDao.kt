package com.fit2081.nutritrack.data.NutriCoachTips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * NutriCoachTipsDao is an interface class that defines the data access object (DAO)
 * for the NutriCoachTips entity.
 */
@Dao
interface NutriCoachTipsDao {

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // Inserts genAi response into db
    @Insert
    suspend fun insert(nutriCoachTips: NutriCoachTips)

    // retrieves all genAI response from db
    // The return type is a Flow, which is a data stream that can be observed for changes.
    @Query("SELECT * FROM genAI_response")
    fun getAllResponses(): Flow<List<NutriCoachTips?>>

    // Retrieves genAI response of specific users from DB based on their ID
    @Query("SELECT * FROM genAI_response WHERE userID = :patientId")
    fun getAllResponsesById (patientId: Int?): Flow<List<NutriCoachTips>>

    // Deletes all response from db (testing)
    @Query("DELETE FROM genAI_response")
    suspend fun deleteAllResponse()
}

