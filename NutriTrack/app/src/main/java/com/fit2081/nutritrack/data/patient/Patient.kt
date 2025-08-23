package com.fit2081.nutritrack.data.patient

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a patient entity (table) in the database.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
//  patient table include: UserID, PhoneNumber, Name, Sex, and all scores
@Entity(tableName = "patients")
data class Patient(

    /**
     * References:
     * The following codes were taken from the following ed lesson with some modifications.
     *  Lab 6, Database Pt.2 https://edstem.org/au/courses/20813/lessons/79902/slides/540937
     **/

    // PK - unique patient id, auto-generated id
    @PrimaryKey(autoGenerate = false)
    val userID: Int,
    // password (def val empty)
    val password: String = "",
    // ph
    val phoneNumber: String,
    // patient name
    val username: String = "",
    // sex
    val gender: String,

    // first time login/register (assume all user first time login)
    val isFirstTimeUser: Boolean = true,
    // register acc
    val isRegister: Boolean = false,

    // HEIFA scores
    // total
    val totalHEIFAScore: Double,
    // Discretionary
    val discretionaryHEIFAScore: Double,
    // vegetables
    val vegetableHEIFAScore: Double,
    // fruit
    val fruitHEIFAScore: Double,
    // grain and cereals
    val grainAndCerealsHEIFAScore: Double,
    // whole grains
    val wholeGrainsHEIFAScore: Double,
    // Meat and alternatives
    val meatAndAlternativeHEIFAScore: Double,
    // sodium
    val sodiumHEIFAScore: Double,
    // alcohol
    val alcoholHEIFAScore: Double,
    // water
    val waterHEIFAScore: Double,
    // sugar
    val sugarHEIFAScore: Double,
    // saturated fat
    val saturatedFatHEIFAScore: Double,
    // unsaturated fat
    val unsaturatedFatHEIFAScore: Double

)
