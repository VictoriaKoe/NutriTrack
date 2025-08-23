package com.fit2081.nutritrack.data.NutriCoachTips

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fit2081.nutritrack.data.patient.Patient

/**
 * Represents a NutriCoachTips entity (table) in the database.
 */
// user gets a GenAI response and save to NutriCoachTips table
@Entity(
    tableName = "genAI_response",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class, // entity (table) that the foreign key references
            // column in the referenced parent Patient entity that the foreign key corresponds to
            // (PK of Patient as FK of FoodIntake)
            parentColumns = arrayOf("userID"),
            // column in the FoodIntake child entity that the foreign key is defined on
            childColumns = arrayOf("userID"),
            // food intake update/delete after patient del/update
            onUpdate = ForeignKey.CASCADE, // update based on row in parent table
            onDelete = ForeignKey.CASCADE // delete based on row in parent table
        )
    ]
)

data class NutriCoachTips(

    // unique genAI response id
    @PrimaryKey(autoGenerate = true)
    val nutriCoachTipsID: Int = 0,

    // ai response
    val generatedAIResponse: String,

    // FK relationship with Patient table
    val userID: Int?,

)
