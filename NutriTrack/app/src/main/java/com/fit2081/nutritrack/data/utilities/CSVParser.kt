package com.fit2081.nutritrack.data.utilities

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * User is a data class representing the current User,
 * It serves as temporary data class for CSV migration to Patient database
 */
// data user class
data class User (
    val userID: String,
    val phoneNum: String,
    val gender: String,
    val foodCategoryHEIFAScore: FoodCategoryHEIFAScore
)
/**
 * FoodCategoryHEIFAScore is a data class representing the food intake questionnaire,
 * It serves as temporary data class for CSV migration to Patient database
 */
data class FoodCategoryHEIFAScore (
    val totalScore: Double,
    val foodCategoryScoreMap: Map<String, Double>
)

/**
 * Function retrieving each food category score based on current user that exists in CSV file
 *
 * @param context Context of the activity
 * @param fileName file name to be opened and processed
 *
 * @return map of food category score of the current user with food group name as key and
 * the HEIFA score as value corresponding to the food group name.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
fun getAllUserDataFromCSV(context: Context, fileName: String): MutableList<User>{

    // user data
    val entities = mutableListOf<User>()

    // get asset manager
    val assets = context.assets

    // get data from csv
    var foodScoreGender: FoodCategoryHEIFAScore?

    // open csv file and read l-b-l
    try {
        // open file from assets
        val inputStream = assets.open(fileName)

        // create buffered reader for efficient reading
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines {
            // skip header row
            lines -> lines.drop(1).forEach {line ->
                val values = line.split(",") // split each line into val

                // extract user id, phone num, gender
                val id = values[1].trim()
                val phoneNum = values[0].trim()
                val gender = values[2].trim()
                println("User data: ${id}, ${phoneNum}, ${gender}")

                // check user gender and retrieve HEIFA scores based on gender
                if (gender == "Male") {
                    foodScoreGender =
                        retrieveUserAllHEIFAScoresByGender(context, fileName, gender, values)
                }
                // female
                else if (gender == "Female"){
                    foodScoreGender =
                        retrieveUserAllHEIFAScoresByGender(context, fileName, gender, values)
                }
                else {
                    foodScoreGender = null
                }

                // save to user instance
                val user = User(
                    userID = id,
                    phoneNum = phoneNum,
                    gender = gender,
                    foodCategoryHEIFAScore = foodScoreGender!!
                )

                // save to user list
                entities.add(user)
            }
        }
    }
    catch (e: Exception){
        // handle exception when open file error
        e.printStackTrace()
        println("Error to get CSV data")
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }

    return entities
}

/**
 * Function retrieving each food category score based on current user that exists in CSV file
 *
 * @param context Context of the activity
 * @param fileName file name to be opened and processed
 *
 * @return map of food category score of the current user with food group name as key and
 * the HEIFA score as value corresponding to the food group name.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// retrieve HEIFA scores based on user gender to save to each Patient entry db
fun retrieveUserAllHEIFAScoresByGender(
    context: Context,
    fileName: String,
    gender: String,
    dataRow: List<String>
): FoodCategoryHEIFAScore {

    // Validate gender parameter first
    val validatedGender = when (gender.lowercase()) {
        "male", "m" -> "Male"
        "female", "f" -> "Female"
        else -> throw IllegalArgumentException("Gender must be 'Male' or 'Female'")
    }

    // food group list
    val foodList = listOf(
        "Discretionary", "Vegetables", "Fruit",
        "Grainsandcereals", "Wholegrains", "Meatandalternatives",
        "Sodium", "Alcohol", "Water", "Sugar",
        "SaturatedFat", "UnsaturatedFat"
    )

    // map of individual food category HEIFA scores
    val mapFoodScore = mutableMapOf<String, Double>()

    // food score csv title
    val csvTitleIndex = mutableListOf<Int>()
    // get current food as key
    val currFoodList = mutableListOf<String>()

    // total HEIFA score value
    var totalScore: Double = 0.0

    try {
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        // Parse header to find column indices
        val headerLine = reader.readLine()
        val headerValues = headerLine.split(",").map { it.trim() }

        /** AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in refining
         * the CSV reading file to get specific data and debugging in the following code lines below.
         */
        // Find column indices for food category scores for the specified gender
        foodList.forEach { food ->
            val columnName = "${food}HEIFAscore$validatedGender"
            val index = headerValues.indexOfFirst { it == columnName }
            if (index != -1) {
                csvTitleIndex.add(index)
                currFoodList.add(food)
            } else {
                Log.w("HEIFA", "Column $columnName not found in CSV")
            }
        }

        // Find column index for total HEIFA score for the specified gender
        val totalScoreColumnName = "HEIFAtotalscore$validatedGender"
        val totalScoreIndex = headerValues.indexOfFirst { it == totalScoreColumnName }
        println("Gender: $validatedGender")
        println("total score index: ${totalScoreIndex}")
        // error handling
        if (totalScoreIndex == -1) {
            Log.e("HEIFA", "Total score column for $validatedGender not found")
            throw IllegalStateException("Could not find $totalScoreColumnName in CSV")
        }

        // Read only one data row
        val dataLine = dataRow
        if (dataLine != null) {

            // Extract total score
            dataLine[totalScoreIndex].toDoubleOrNull()?.let {
                totalScore = it
                println("total score: $totalScore")
            }

            // Extract individual food category scores
            csvTitleIndex.forEachIndexed { i, index ->
                dataRow[index].toDoubleOrNull()?.let { score ->
                    println("food: ${currFoodList[i]}")
                    println("score: $score")
                    mapFoodScore[currFoodList[i]] = score
                }
            }
        } else {
            Log.e("HEIFA", "No data found in CSV")
        }

        reader.close()
        inputStream.close()

    } catch (e: Exception) {
        Log.e("HEIFA", "Error retrieving HEIFA scores", e)
        Toast.makeText(context, "Failed to load HEIFA scores: ${e.message}", Toast.LENGTH_LONG).show()
    }

    val foodInstance = FoodCategoryHEIFAScore (
        totalScore,
        mapFoodScore
    )

    // return food category instance
    return foodInstance
}


