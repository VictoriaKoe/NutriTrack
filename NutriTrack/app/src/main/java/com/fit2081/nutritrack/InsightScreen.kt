package com.fit2081.nutritrack

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientViewModel
import kotlinx.coroutines.runBlocking

/**
 * Composable function representing Insight screen.
 *
 * @param modifier modify the layout of composable
 * @param onNutriCoachButtonClick navigation button to navigate to the NutriCoach Page
 * @param patientViewModel view model to access patient data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun Insight(modifier: Modifier, onNutriCoachButtonClick: () -> Unit, patientViewModel: PatientViewModel) {

    val context = LocalContext.current

    // get current user data
    val currUserID: Int? = AuthManager.getCurrentUserId()?.toInt()
    Log.d(TAG, "Current user id: ${currUserID}")

    var currentUser: Patient

    runBlocking {
        currentUser = patientViewModel.getPatientByID(currUserID)
        Log.d(TAG, "Current user: ${currentUser}")
    }

    // get curr user all individual HEIFA scores
    val progressBarScoreValues = mutableMapOf<String, Double>()

    // add values to display
    progressBarScoreValues["Discretionary"] = currentUser.discretionaryHEIFAScore
    progressBarScoreValues["Vegetables"] = currentUser.vegetableHEIFAScore
    progressBarScoreValues["Fruit"] = currentUser.fruitHEIFAScore
    progressBarScoreValues["Grainsandcereals"] = currentUser.grainAndCerealsHEIFAScore
    progressBarScoreValues["Wholegrains"] = currentUser.wholeGrainsHEIFAScore
    progressBarScoreValues["Meatandalternatives"] = currentUser.meatAndAlternativeHEIFAScore
    progressBarScoreValues["Sodium"] = currentUser.sodiumHEIFAScore
    progressBarScoreValues["Alcohol"] = currentUser.alcoholHEIFAScore
    progressBarScoreValues["Water"] = currentUser.waterHEIFAScore
    progressBarScoreValues["Sugar"] = currentUser.sugarHEIFAScore
    progressBarScoreValues["SaturatedFat"] = currentUser.saturatedFatHEIFAScore
    progressBarScoreValues["UnsaturatedFat"] = currentUser.unsaturatedFatHEIFAScore

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // title
        Text(
            text = "Insights: Food Score",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(20.dp))

        // progress bar for each categories
        for (entry in progressBarScoreValues) {
            val (food, score) = entry
            val foodScore = score.toFloat()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Text
                Text(
                    // display label
                    text = when (food) {
                        "Grainsandcereals" -> "Grains & Cereals"
                        "Wholegrains" -> "Whole Grains"
                        "Meatandalternatives" -> "Meat & Alternatives"
                        "SaturatedFat" -> "Saturated Fat"
                        "UnsaturatedFat" -> "Unsaturated Fat"
                        else -> food
                    },
                    modifier = Modifier.width(123.dp).padding(bottom = 2.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                /**
                 * References:
                 * The following code lines were taken from the following ed lesson with some modifications.
                 *  Lab 3, Section 3.0 - Shared Preferences, Date and Time pickers, Slider and Progress Bar:
                 *  https://edstem.org/au/courses/20813/lessons/77916/slides/527595
                 *
                 * AI Acknowledgment:
                 * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
                 * error and refining the UI of progress bar in the following code lines below.
                 **/
                Box(
                    modifier = Modifier.weight(1.5f)
                ) {
                    // progress bar
                    LinearProgressIndicator(
                        progress = {
                            if (food == "Alcohol" || food == "Water") foodScore / 5f
                            else foodScore / 10f
                        },
                        modifier = Modifier
//                            .weight(2f)
                            .clip(RoundedCornerShape(4.dp))
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray
                    )
                }

                // score display txt
                Text(
                    text =
                        if (food == "Alcohol" || food == "Water")"${foodScore}/5.0"
                        else "${foodScore}/10.0",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(0.55f)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.heightIn(20.dp))
        }

        Spacer(Modifier.height(10.dp))

        // total food quality score
        Text(
            text = "Total Food Quality Score",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        /**
         * References:
         * The following code lines were taken from the following ed lesson with some modifications.
         *  Lab 3, Section 3.0 - Shared Preferences, Date and Time pickers, Slider and Progress Bar:
         *  https://edstem.org/au/courses/20813/lessons/77916/slides/527595
         *
         * AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
         * error and refining the UI of progress bar in the following code lines below.
         **/
        // total score from current user data class
        val totalProgressScore = currentUser.totalHEIFAScore.toFloat()

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // progress bar
            LinearProgressIndicator(
                progress = { totalProgressScore/100f },
                modifier = Modifier
                        .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .width(180.dp)
                    .height(8.dp),
                trackColor = Color.LightGray
            )

            // text
            Text(
                text = "$totalProgressScore /100.0",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(10.dp))

        // button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            /**
             * References:
             * The following code lines were taken from the following ed lesson with some modifications.
             *  Lab 4, Section D - Sharing Data: https://edstem.org/au/courses/20813/lessons/78691/slides/531688
             *
             * AI Acknowledgment:
             * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
             * error and refining the sharing data functions in the following code lines below.
             **/
            // share with someone
            Button(
                onClick = {
                    shareInsightData(progressBarScoreValues, totalProgressScore, context)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .height(48.dp) // Fixed height for consistency
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // icon
                    Image(
                        painter = painterResource(R.drawable.icons8_share_50_2),
                        contentDescription = "share icon",
                        modifier = Modifier.size(25.dp).offset(x = -5.dp)
                    )

                    // text
                    Text(
                        text = "Share with someone",
                        modifier = Modifier.offset(x = 5.dp)
                    )
                }
            }

            Spacer(Modifier.heightIn(10.dp))

            /** AI Acknowledgment:
             * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
             * feature of navigation to the Insight page in the following code lines below.
             */
            // improve my diet
            Button(
                onClick = {
                    // navigate to nutricoach by passing the navigation controller from home screen
                    onNutriCoachButtonClick()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .height(48.dp) // Fixed height for consistency

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // icon
                    Image(
                        painter = painterResource(R.drawable.icons8_diet_32),
                        contentDescription = "diet icon",
                        modifier = Modifier.size(30.dp).offset(x = (-10).dp)
                    )

                    // text
                    Text(
                        text = "Improve My Diet",
                    )
                }
            }
        }
    }
}


/**
 * Function sharing whole insights data with someone.
 *
 * @param progressScoreValue a map consisting of a complete food quality score of the current user to be shared
 * @param totalScore total food quality score of the current user to be shared
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// functions to share insight data
fun shareInsightData(progressScoreValue: Map<String, Double>, totalScore: Float, context: Context) {

    /** AI Acknowledgment:
    * I acknowledge the use of Claude (https://claude.ai/) to aid me in
     * creating the sharing data functions in the following code lines below.
    **/
    // string value to be shared
    val shareDataText = """
        Discretionary: ${progressScoreValue.get("Discretionary")}/10.0
        Vegetables: ${progressScoreValue.get("Vegetables")}/10.0
        Fruit: ${progressScoreValue.get("Fruit")}/10.0
        Grains & Cereals: ${progressScoreValue.get("Grainsandcereals")}/10.0
        Whole Grains: ${progressScoreValue.get("Wholegrains")}/10.0
        Meat & Alternatives: ${progressScoreValue.get("Meatandalternatives")}/10.0
        Sodium: ${progressScoreValue.get("Sodium")}/10.0
        Alcohol: ${progressScoreValue.get("Alcohol",)}/5.0
        Water: ${progressScoreValue.get("Water")}/5.0
        Sugar: ${progressScoreValue.get("Sugar")}/10.0
        Saturated Fat: ${progressScoreValue.get("SaturatedFat")}/10.0
        Unsaturated Fat: ${progressScoreValue.get( "UnsaturatedFat")}/10.0
        
         Total Food Quality Score: $totalScore/100
    """.trimIndent() // remove any indentation

    /**
     * References:
     * The following code lines below were taken from the following ed lesson with some modifications.
     *  Lab 4, Section D - Sharing Data: https://edstem.org/au/courses/20813/lessons/78691/slides/531688
     */
    // create share Intent instance to share insight data

    // create intent to share text
    // shareIntent == Intent obj, msg obj to request action from another app component
    val shareIntentInstance = Intent(ACTION_SEND)

    // set type of data to share
    shareIntentInstance.type = "text/plain"

    // set data to share, insight score
    // carry text
    shareIntentInstance.putExtra(Intent.EXTRA_TEXT, shareDataText)

    /*
    start activity to share data, with a chooser to select app sharing process
    Intent.createChooser - creates chooser dialog, allows user to select which
        app they want to user to share txt
    shareIntent - contain data and data type to be shared
    title - title of chooser dialog
    */
    context.startActivity(Intent.createChooser(shareIntentInstance, "Share Food Score via"))
}

