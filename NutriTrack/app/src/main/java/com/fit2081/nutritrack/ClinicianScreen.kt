package com.fit2081.nutritrack

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nutritrack.data.NutriCoachTips.NutriCoachTips
import com.fit2081.nutritrack.data.genAIData.AIResponseBody
import com.fit2081.nutritrack.data.genAIData.GenAIViewModel
import com.fit2081.nutritrack.data.genAIData.UiState
import com.fit2081.nutritrack.data.patient.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/**
 * Composable function representing Clinician View screen.
 *
 * @param innerPadding provides necessary padding values
 * @param patientViewModel view model to access patient data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun ClinicianView(
    innerPadding: PaddingValues,
    patientViewModel: PatientViewModel,
    navController: NavHostController
) {


    val avgHEIFAScoreMale: Double
    val avgHEIFAScoreFemale: Double

    // retrieve avg score of male and female
    runBlocking {
        avgHEIFAScoreMale = patientViewModel.getAverageHEIFAScore("Male")
        avgHEIFAScoreFemale = patientViewModel.getAverageHEIFAScore("Female")
        Log.d(TAG, "avg male HEIFA scores: $avgHEIFAScoreMale")
        Log.d(TAG, "avg male HEIFA scores: $avgHEIFAScoreFemale")
    }

    Column(
        modifier =  Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(15.dp)
            .verticalScroll(rememberScrollState(), true),
    ) {
        // Clinician Dashboard title
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = "Clinician Dashboard ",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        // 1st part: Avg HEIFA score Male and Female
        Column (
            modifier = Modifier
                .weight(0.25f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ListHEIFAScoresScreen(
                avgHEIFAScoreMale = avgHEIFAScoreMale,
                avgHEIFAScoreFemale = avgHEIFAScoreFemale
            )
        }

        HorizontalDivider(modifier = Modifier.padding(10.dp))

        // 2nd part: AI Data Pattern
        Column (
            modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth()
        ) {
            AIDataPatternScreen(
                patientViewModel = patientViewModel,
                navController = navController
            )
        }
    }
}

/**
 * Composable function displaying average HEIFA scores of all Male and Female users
 *
 * @param avgHEIFAScoreMale average HEIFA score of all Male users
 * @param avgHEIFAScoreFemale average HEIFA score of all Female users
 *
 */
@Composable
fun ListHEIFAScoresScreen(avgHEIFAScoreMale: Double, avgHEIFAScoreFemale: Double) {

    // add score to list
    val scoreList = mutableMapOf<String, Double>()
    scoreList["Male"] = avgHEIFAScoreMale
    scoreList["Female"]= avgHEIFAScoreFemale

    scoreList.forEach { score ->
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(65.dp),
            // add subtle shadow for depth
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            // arrange details HZ
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                // title, average HEIFA (Male/Female)
                Text (
                    text = "Average HEIFA (${score.key}): ",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )

                // score
                Text(
                    text = "${score.value}",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}


/**
 * Composable function representing Al-Powered Data Analysis
 *
 * @param patientViewModel view model to access patient data
 * @param genAiViewModel view model to interact genAI model
 *
 */
@Composable
fun AIDataPatternScreen(
    patientViewModel: PatientViewModel,
    genAiViewModel: GenAIViewModel = viewModel(),
    navController: NavHostController
) {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 3b: Coding (ADVANCED - GenAI): https://edstem.org/au/courses/20813/lessons/80448/slides/544886
     **/

    // state to track genAI prompt
    val placeholderPattern = stringResource(R.string.pattern_placeholder)
    //  Persists even across configuration changes
    var result by rememberSaveable { mutableStateOf(placeholderPattern) }
    val uiState by genAiViewModel.uiState.collectAsState()

    // retrieve all users data
    val allUsers by patientViewModel.allPatients
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var usersData = ""

    if (allUsers.isNotEmpty()) {
        allUsers.forEach {user ->
            usersData += "user gender: ${user.gender}, \n" +
                    "total HEIFA scores: ${user.totalHEIFAScore}, \n" +
                    "discretionary HEIFA scores: ${user.discretionaryHEIFAScore}, \n" +
                    "fruits HEIFA scores: ${user.fruitHEIFAScore}, \n" +
                    "vegetable HEIFA scores: ${user.vegetableHEIFAScore}, \n" +
                    "grains and cereals HEIFA scores: ${user.grainAndCerealsHEIFAScore} \n" +
                    "whole grains HEIFA scores: ${user.wholeGrainsHEIFAScore}, \n" +
                    "meat and alternatives HEIFA scores: ${user.meatAndAlternativeHEIFAScore}, \n" +
                    "sodium HEIFA scores: ${user.sodiumHEIFAScore}, \n" +
                    "alcohol HEIFA scores: ${user.alcoholHEIFAScore}, \n" +
                    "water HEIFA scores: ${user.waterHEIFAScore}, \n" +
                    "sugar HEIFA scores: ${user.sugarHEIFAScore}, \n" +
                    "saturated HEIFA scores: ${user.saturatedFatHEIFAScore}, \n" +
                    "unsaturated fat HEIFA scores: ${user.unsaturatedFatHEIFAScore}"
        }
    }

    // predefined prompt
    val prompt = "Generate three key data patterns based on the given dataset: $usersData"

    // AI button
    Column (
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.90f)
    ) {
        Button(
            onClick = {
                // send prompt to ai
                /** AI Acknowledgment:
                 * I acknowledge the use of Claude (https://claude.ai/) to aid me in
                 * implementing sending prompt and return the result in JSON format in the following code lines below.
                 */
                genAiViewModel.sendPromptForPatterns(prompt)
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterHorizontally)
                .height(50.dp) // Fixed height for consistency
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // icon
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search icon",
                    modifier = Modifier.size(30.dp)
                )

                // text
                Text(
                    text = "Find Data Pattern",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
            }
        }

        // display 3 key data pattern in list
        // AI response
        // conditional ui rendering based on current ui state
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 15.dp)
            )
        } else {
            var textColor = MaterialTheme.colorScheme.onSurface

            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage

                Text(
                    text = result,
                    textAlign = TextAlign.Justify,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                        .fillMaxSize()
                )
            }
            else if (uiState is UiState.SuccessBody) {
                val patternList = (uiState as UiState.SuccessBody).outputPattern

                // list of AI response body obj
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(patternList) {
                            pattern -> DataPattern(pattern)
                    }
                }
            }
        }
    }

    // done button
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                // assume logout from clinician view -> clinician login view
                navController.popBackStack()
            }
        ) {
            Text (
                text = "Done"
            )
        }
    }
}

/**
 * Composable function representing key data pattern item generated by genAI
 *
 * @param response [AIResponseBody] instance
 *
 */
@Composable
fun DataPattern(pattern: AIResponseBody) {

    // card - groups related content
    ElevatedCard (
        modifier = Modifier.fillMaxWidth(),
        // add subtle shadow for depth
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        // arrange list of genAI response
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // title
            Text(
                text = pattern.patternName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp),
                fontSize = 16.sp
            )

            // description
            Text (
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
        }
    }
}




