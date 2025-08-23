package com.fit2081.nutritrack.data.foodIntake

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fit2081.nutritrack.data.patient.Patient

/**
 * Represents a food intake questionnaire entity (table) in the database.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
//  store responses from the questionnaire with a foreign key column relationship with Patient table
@Entity(
    /**
     *  References:
     *  The following codes were referenced to the link below:
     *       https://medium.com/@vontonnie/connecting-room-tables-using-foreign-keys-c19450361603
     */
    tableName = "food_intake",
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
data class FoodIntake(

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // PK, unique id of questionnaire, auto-generated id
    @PrimaryKey(autoGenerate = true)
    val foodIntakeID: Int = 0,

    // food categories
    val fruits: Boolean = false,
    val vegetables: Boolean = false,
    val grains: Boolean = false,
    val redMeat: Boolean = false,
    val seafood: Boolean = false,
    val poultry: Boolean = false,
    val fish: Boolean = false,
    val eggs: Boolean = false,
    val nutsSeeds: Boolean = false,

    // persona
    val persona: String,

    // meal time
    val mealTime: String,

    // sleep time
    val sleepTime: String,

    // wake up time
    @ColumnInfo(name = "wake_up_time")
    val wakeUpTime: String,

    // FK relationship with Patient table
    val userID: Int?,

)
