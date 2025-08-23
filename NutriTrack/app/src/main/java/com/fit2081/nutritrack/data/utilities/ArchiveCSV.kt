package com.fit2081.nutritrack.data.utilities

import android.content.Context
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader

// Kt file contains all csv function

/**
 * Function retrieving the current user's gender that exists and matched in CSV file
 *
 * @param context Context(from the CompositionLocal system) from the nearest available scope
 * @param fileName file name to be opened and processed
 * @param userID the current user ID
 * @param userPhNumber the current user phone number
 *
 * @return map of food category score of the current user with food group name as key and
 * the HEIFA score as value corresponding to the food group name.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// get current user gender
fun retrieveUserGender(context: Context, fileName: String, userID: String, userPhNumber: String): String {

    /**
     * References:
     * The following code from lines 440 to 478 was taken from the following ed lesson with some modifications.
     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
     **/
    // get asset manager
    val assets = context.assets
    // user gender
    var retGender = ""
    // gender csv title
    var csvTitleIndex = 0

    // open csv file and read l-b-l
    try{
        // open file from assets
        val inputStream = assets.open(fileName)

        // create buffered reader for efficient reading
        val reader = BufferedReader(InputStreamReader(inputStream))

        // get title
        val headerLine = reader.readLine() // read single line, title
        val headerValues = headerLine.split(",") // split header
        csvTitleIndex = headerValues.indexOfFirst { it.trim() == "Sex" } // get sex title idx
        println("Selected index: ${csvTitleIndex}")

        reader.useLines {
            // skip header row
                lines -> lines.forEach {line ->
            val values = line.split(",") // split each line into val
//                retGender = values[csvTitleIndex].trim()
//                println("Selected gender: ${retGender}")
            // check user data & retrieve gender
            if (values[1] == userID && values[0] == userPhNumber) {
                // get current user gender
                retGender = values[csvTitleIndex].trim()
                println("Selected gender: ${retGender}")
                println("Selected userID: ${userPhNumber}")
            }
        }
        }

    } catch (e:Exception){
        // handle exception when open file error
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }

    // ret list of user gender
    return retGender
}

/**
 * Function validating user input phone number exist in CSV file
 *
 * @param context Context(from the CompositionLocal system) from the nearest available scope.
 * @param fileName file name to be opened and processed
 * @param phoneNum user input phone number
 *
 * @return true if match, else false
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below
 */
// validate id match with phone num in csv
fun validatePhoneNumberInput(context: Context, fileName: String, phoneNum: String): Boolean {

    /**
     * References:
     * The following code from lines 443 to 472 was taken from the following ed lesson with some modifications.
     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
     **/
    // return result whether is matched or not
    var result = false
    val assets = context.assets

    // open csv file and read l-b-l
    try{
        // open file from assets
        val inputStream = assets.open(fileName)

        // create buffered reader for efficient reading
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines {
            // skip header row
                lines -> lines.drop(1).forEach {line ->
            val values = line.split(",") // split each line into val
            // validate credential is matched
            if (values[0].trim() == phoneNum.trim()){
                result = true
            }
        }
        }

    } catch (e:Exception){
        // handle exception when open file error
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }

    // return validated result
    return result
}

/**
 * Function retrieving User_Id from data.csv file to render on dropdown menu
 *
 * @param context Context(from the CompositionLocal system) from the nearest available scope.
 * @param fileName file name to be opened and processed
 *
 * @return selected user id after user select from dropdown menu
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// get user id from csv to render
fun getUserIDFromCSV (context: Context, fileName: String): List<String> {

    /**
     * References:
     * The following code from lines 322 to 350 was taken from the following ed lesson with some modifications.
     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
     **/
    // get asset manager
    val assets = context.assets
    var retList = mutableListOf<String>()

    // open csv file and read l-b-l
    try{
        // open file from assets
        val inputStream = assets.open(fileName)

        // create buffered reader for efficient reading
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines {
            // skip header row
                lines -> lines.drop(1).forEach {line ->
            val values = line.split(",") // split each line into val
            val id = values[1].trim()
            // append id to list
            retList.add(id)
        }
        }

    } catch (e:Exception){
        // handle exception when open file error
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }

    // ret list of user id
    return retList
}

/**
 * Function validating whether both credentials match each other
 *
 * @param context Context(from the CompositionLocal system) from the nearest available scope. Context
 * of the activity
 * @param fileName file name to be opened and processed
 * @param userID selected user id from dropdown menu
 * @param phoneNum user input phone number
 *
 * @return true if match, else false
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// validate id match with phone num in csv
fun validateIDAndPhoneNumFromCSV(context: Context, fileName: String, userID: String, phoneNum: String): Boolean {

    /**
     * References:
     * The following code from lines 392 to 421 was taken from the following ed lesson with some modifications.
     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
     **/
    // return result whether is matched or not
    var result = false
    val assets = context.assets

    // open csv file and read l-b-l
    try{
        // open file from assets
        val inputStream = assets.open(fileName)

        // create buffered reader for efficient reading
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines {
            // skip header row
                lines -> lines.drop(1).forEach {line ->
            val values = line.split(",") // split each line into val
            // validate credential is matched
            if (values[1].trim() == userID.trim() && values[0].trim() == phoneNum.trim()){
                result = true
            }
        }
        }

    } catch (e:Exception){
        // handle exception when open file error
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }

    // return validated result
    return result
}

/**
* Function retrieving the current user's total food quality score
*
* @param context Context(from the CompositionLocal system) from the nearest available scope
* @param fileName file name to be opened and processed
* @param userID the current user ID
* @param userPhNumber the current user phone number
* @param userGender the current user gender
*
* @return the current user's total food quality score as String
*
* Acknowledgement:
* Full acknowledgment details can be found below.
*/
// get current user total food quality score
//fun retrieveUserFoodQualityScoreByGender(context: Context, fileName: String, userID: String, userPhNumber: String, userGender: String): String {
//
//    /**
//     * References:
//     * The following code from lines 489 to 530 was taken from the following ed lesson with some modifications.
//     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
//     **/
//    // get asset manager
//    val assets = context.assets
//    // food score
//    var retScore = ""
//    // curr user gender
//    var gender = userGender
//
//    // food score csv title
//    var csvTitleIndex = 0
//
//    // open csv file and read l-b-l
//    try{
//        // open file from assets
//        val inputStream = assets.open(fileName)
//
//        // create buffered reader for efficient reading
//        val reader = BufferedReader(InputStreamReader(inputStream))
//
//        // get title
//        val headerLine = reader.readLine() // read single line, title
//        val headerValues = headerLine.split(",") // split header
//        csvTitleIndex = headerValues.indexOfFirst { it.trim() == "HEIFAtotalscore${gender}"} // get sex title idx
//        println("Selected index: ${csvTitleIndex}")
//
//        reader.useLines {
//            // skip header row
//                lines -> lines.forEach {line ->
//            val values = line.split(",") // split each line into val
//            println("score gender: ${values[csvTitleIndex].trim()}")
//            // check current user match
//            if (values[1].trim() == userID.trim() && values[0].trim() == userPhNumber.trim()) {
//                // get score data
//                retScore = values[csvTitleIndex].trim()
//                println("Selected gender: ${retScore}")
//                println("Selected userID: ${userID}")
//            }
//        }
//        }
//
//    } catch (e:Exception){
//        // handle exception when open file error
//        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
//    }
//
//    // ret list of user total score
//    return retScore
//}

/**
 * Function retrieving the current user data saved in shared preferences and creating new User
 * data class object to store the current user's data
 *
 * @param context Context of the activity
 *
 * @return User data class object
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// get user gender
//fun retrieveUserData(context: Context): User {
//
//    val sharedPref = context
//        .getSharedPreferences("user", Context.MODE_PRIVATE)
//
//    // get current user id, ph num & gender
//    val currUserID: String = sharedPref.getString("userId", "").toString()
//    val currUserPhNum: String = sharedPref.getString("phNum", "").toString()
//    // user gender
//    val gender: String = sharedPref.getString("gender", "").toString()
//
//    // get curr user food quality score
//    var foodQualityScore = retrieveUserFoodQualityScoreByGender(context, "data.csv",currUserID, currUserPhNum, gender )
//
//    // user obj
//    val currentUser = User(currUserID, currUserPhNum, gender, foodQualityScore)
//
//    return currentUser
//}

/**
 * Function retrieving each food category score based on current user that exists in CSV file
 *
 * @param context Context(from the CompositionLocal system) from the nearest available scope
 * @param fileName file name to be opened and processed
 * @param currUser the current user
 * @param foodList list of food group list
 *
 * @return map of food category score of the current user with food group name as key and
 * the HEIFA score as value corresponding to the food group name.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// retrieve each food category score based on current user
// get current user food quality score
//fun retrieveUserIndividualFoodScore(context: Context, fileName: String, currUser: User, foodList: List<String>): Map<String, String> {
//
//    /**
//     * References:
//     * The following code from lines 309 to 363 was taken from the following ed lesson with some modifications.
//     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
//     **/
//    // get asset manager
//    val assets = context.assets
//
//    // map of food score
//    val retScore= mutableMapOf<String, String>()
//
//    // food score csv title
//    val csvTitleIndex = mutableListOf<Int>()
//
//    val currFoodList = mutableListOf<String>()
//
//    // open csv file and read l-b-l
//    try{
//        // open file from assets
//        val inputStream = assets.open(fileName)
//
//        // create buffered reader for efficient reading
//        val reader = BufferedReader(InputStreamReader(inputStream))
//
//        // get title
//        val headerLine = reader.readLine() // read single line, title
//        val headerValues = headerLine.split(",") // split header
//        // get all scores title idx
//        foodList.forEachIndexed {index, food ->
//            csvTitleIndex.add(headerValues.indexOfFirst { it.trim() == "${food}HEIFAscore${currUser.gender}"})
//            currFoodList.add(food)
//        }
//
//        reader.useLines {
//            // skip header row
//                lines -> lines.forEach {line ->
//            val values = line.split(",") // split each line into val
//            // check is current user data
//            if (values[1].trim() == currUser.userID.trim() && values[0].trim() == currUser.phoneNum.trim()) {
//                // loop title idx to retrieve data
//                var i = 0
//                csvTitleIndex.forEach{index ->
//                    // get score data
//                    val score = values[index].trim()
//                    val food = foodList[i]
//                    retScore[food] = score
//                    // increase counter
//                    i++
//                }
//            }
//        }
//        }
//
//    } catch (e:Exception){
//        // handle exception when open file error
//        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
//    }
//
//    // ret map of score
//    return retScore
//}

/**
 * Function retrieving food intake questionnaire data from shared preferences to be loaded
 * when edit button is clicked/the same user login back the app.
 *
 * @param context: Context of activity
 * @param foodList list of food categories to be chosen
 *
 * @return instance of UserFoodIntakeChoice
 *
 * */
// Function to retrieve questionnaire data for a specific user
//fun getQuestionnaireData(context: Context, foodList: List<String>): UserFoodIntakeChoice {
//
//    /**
//     * References:
//     * The following code from lines 915 to 935 was taken from the following ed lesson with some modifications.
//     *  Lab 3, Section 3.0 - Dealing with Assets: Reading CSV Files: https://edstem.org/au/courses/20813/lessons/77916/slides/525130
//     **/
//    // list of checked states of checkbox
//    var checkedBoxList = mutableListOf<Boolean>()
//
//    // get current user id from local context
//    val sharedPref = context
//        .getSharedPreferences("user", Context.MODE_PRIVATE)
//
//    val currUserID: String = sharedPref.getString("userId", "").toString()
//
//    foodList.forEachIndexed { index, food ->
//        checkedBoxList.add(sharedPref.getBoolean("${currUserID}_${food}", false))
//    }
//
//    return UserFoodIntakeChoice(
//        checkBoxStatesList = checkedBoxList ,
//        persona = sharedPref.getString("${currUserID}_persona", "").toString(),
//        mealTime = sharedPref.getString("${currUserID}_meal_time", "").toString(),
//        sleepTime = sharedPref.getString("${currUserID}_sleep_time", "").toString(),
//        wakeTime =  sharedPref.getString("${currUserID}_wake_time", "").toString(),
//        userId = currUserID
//    )
//}

