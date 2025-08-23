package com.fit2081.nutritrack

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.NutriCoachTips.NutriCoachTips
import com.fit2081.nutritrack.data.NutriCoachTips.NutriCoachTipsViewModel
import com.fit2081.nutritrack.data.foodIntake.FoodIntake
import com.fit2081.nutritrack.data.foodIntake.FoodIntakeViewModel
import com.fit2081.nutritrack.data.fruity.viewModel.FruityViewModel
import com.fit2081.nutritrack.data.genAIData.GenAIViewModel
import com.fit2081.nutritrack.data.genAIData.UiState
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/**
 * Data class represents fruit
 */
data class Fruit (
    var name: String,
    var id: Int,
    var family: String,
    var order: String,
    var genus: String,
    var nutrition: Map<String, Double>
)

/**
 * Composable function representing NutriCoach screen.
 *
 * @param innerPadding provides necessary padding values
 * @param patientViewModel view model to access patient data
 * @param nutriCoachTipsViewModel view model to access NutriCoach tips data
 * @param foodIntakeViewModel view model to access food intake data
 *
 */
@Composable
fun NutriCoach(
    innerPadding: PaddingValues,
    patientViewModel: PatientViewModel,
    nutriCoachTipsViewModel: NutriCoachTipsViewModel,
    foodIntakeViewModel: FoodIntakeViewModel
) {

    // get current user data
    val currUserID: Int? = AuthManager.getCurrentUserId()?.toInt()
    Log.d(TAG, "Current user id: ${currUserID}")

    var currentUser: Patient

    runBlocking {
        currentUser = patientViewModel.getPatientByID(currUserID)
    }

    Column(
        modifier =  Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(15.dp)
            .verticalScroll(rememberScrollState(), true),
    ) {
        // NutriCoach title
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = "NutriCoach",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        // 1st half: Fruity API
        Column (
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth()
        ) {
            FruityApiScreen(currUser = currentUser)
        }

        HorizontalDivider(modifier = Modifier.padding(10.dp))

        // 2nd Half: GenAI
        Column (
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth()
        ) {
            GenAIScreen(
                currUser = currentUser,
                nutriCoachTipsViewModel = nutriCoachTipsViewModel,
                foodIntakeViewModel = foodIntakeViewModel
            )
        }
    }
}

/**
 * Composable function representing fruit details retrieval
 *
 * @param currUser the current user
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun FruityApiScreen(currUser: Patient) {

    // state to hold user's input
    var fruitName by remember { mutableStateOf("")}

    // fruity view model
    val viewModel: FruityViewModel = viewModel()
    val fruitData by viewModel.aFruit.collectAsState(null)
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // image url
    var imageUrl by remember { mutableStateOf("") }
    var isImageLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf(false) }
    var imageIdList: List<String> = listOf(
        "237/200/300", "102/4320/3240",
        "106/2592/1728", "493/3872/2592")
    var selectedImageId by remember { mutableStateOf<String?>(null) }

    // clear previous user input
    LaunchedEffect(Unit) {
        viewModel.clearFruityData()
    }

    // get curr user fruit HEIFA score
    val fruitScore: Double = currUser.fruitHEIFAScore
    val optimalScore = 10.0
//    val fruitScore = 10.0

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 1a: Country Flag: https://edstem.org/au/courses/20813/lessons/80448/slides/544883
     **/

    /** AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in refactoring
     * the image to load once on the screen in the following code lines below.
     */
    // if optimal, show image
    if (fruitScore == optimalScore) {
        // render image
        // Initialize the image ID only once
        LaunchedEffect(Unit) {
            if (selectedImageId == null) {
                val randomIndex = Random.nextInt(0, imageIdList.size)
                selectedImageId = imageIdList[randomIndex]
            }
        }

        // image id is not null, render img
        selectedImageId?.let { imageId ->
            imageUrl = "https://picsum.photos/id/$imageId"

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "optimal fruit score image",
                        modifier = Modifier.fillMaxSize(),
                        onLoading = { isImageLoading = true },
                        onSuccess = {
                            isImageLoading = false
                            loadError = false
                        },
                        onError = {
                            isImageLoading = false
                            loadError = true
                        }
                    )
                    // Show loading indicator
                    if (isImageLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(50.dp)
                        )
                    }

                    // Show error message
                    if (loadError) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Could not load image",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(2.dp))

                // show optimal message
                Text (
                    text = "Fruit Score: $fruitScore",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "You've nailed your fruit score! Keep it up!",
                )
            }
        }
    }
    else {
        // not optimal, show fruit selection opt
        // fruit name
        Text(
            text = "Fruit Name",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            // search bar (txt field)
            Box(
                modifier = Modifier
                    .height(35.dp)
                    .weight(1.2f)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = Color.Gray,  // Change this to your desired outline color
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = fruitName,
                    onValueChange = { fruitName = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            // placeholder
                            if (fruitName.isEmpty()) {
                                Text(
                                    text = "eg. banana",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            /** AI Acknowledgment:
             * I acknowledge the use of Claude (https://claude.ai/) to aid me in enhancing
             * API service to render on screen using ViewModel in the following code lines below.
             */
            // search button
            Button(
                onClick = {
                    // call API to get fruit data
                    if (fruitName.isNotBlank()) {
                        // get from fruity view model
                        viewModel.getFruityData(fruitName)
                    }
                },
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    // icon
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search icon",
                        modifier = Modifier.offset(x = (-10).dp)
                    )

                    Text(
                        text = "Details",
                        fontSize = 15.sp
                    )
                }
            }
        }

        /** AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in enhancing
         * API service to render on screen using ViewModel in the following code lines below.
         */
        // fruits details
        when {
            isLoading -> {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            fruitData != null -> {
                // Convert ResponseModel to Fruit and display
                val fruit = Fruit(
                    name = fruitData!!.name,
                    id = fruitData!!.id,
                    family = fruitData!!.family,
                    order = fruitData!!.order,
                    genus = fruitData!!.genus,
                    nutrition = fruitData!!.nutritions
                )
                println("Fruits: $fruit")
                FruitItem(fruit)
            }

            error.isNotEmpty() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(
                        text = "Fruit is not found",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    Text(
                        text = "Nothing to be displayed",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Composable function representing fruit item's details which is searched by user
 *
 * @param fruit Fruit data class (fruit details get from Fruity API service)
 */
@Composable
fun FruitItem(fruit: Fruit) {

    // card - groups related content
    ElevatedCard  (
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 10.dp)
            .offset(x = 20.dp),
        // add subtle shadow for depth
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        // arrange fruit detail vertical
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // fruit name as headline
            Text (
                text = fruit.name,
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 5.dp),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            // family
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Family",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = fruit.family,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // order
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = fruit.order,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // genus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Genus",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = fruit.genus,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // nutrition
            // calories
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calories",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = "${fruit.nutrition.get("calories")}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // fat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fat",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = "${fruit.nutrition.get("fat")}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // sugar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sugar",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = "${fruit.nutrition.get("sugar")}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // carbohydrates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Carbohydrates",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = "${fruit.nutrition.get("carbohydrates")}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.heightIn(3.dp))

            // protein
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Protein",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = "${fruit.nutrition.get("protein")}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * Composable function representing GenAI screen
 * Integrating with GenAI to show one motivational message or fun food tip
 *
 * @param currUser the current user
 * @param genAiViewModel view model to interact with genAI
 * @param nutriCoachTipsViewModel view model to access nutriCoach data
 * @param foodIntakeViewModel view model to access food intake data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun GenAIScreen(
    currUser: Patient,
    genAiViewModel: GenAIViewModel = viewModel(),
    nutriCoachTipsViewModel: NutriCoachTipsViewModel,
    foodIntakeViewModel: FoodIntakeViewModel
) {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 3b: Coding (ADVANCED - GenAI): https://edstem.org/au/courses/20813/lessons/80448/slides/544886
     **/

    // state to track genAI prompt
    val placeholderResult = stringResource(R.string.results_placeholder)
    //  Persists even across configuration changes
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by genAiViewModel.uiState.collectAsState()
    var prompt by remember { mutableStateOf("") }

    // State to store valid AI response only
    var genResult by remember { mutableStateOf<String?>(null) }

    // get food intake
    var foodIntake: FoodIntake?

    runBlocking {
        foodIntake = foodIntakeViewModel.getFoodIntakeByID(patientId = currUser.userID)
    }
    println("Food Intake: $foodIntake")

    // predefined prompt list
    val promptList: List<String> = listOf(
        "Give a practical and actionable healthy eating tip for busy people. Make it specific and easy to follow.",
        "Generate a short encouraging message to help someone improve their fruit intake.",
        "Give any tips to the current user to improve lifestyle based on the food intake response as stated below: $foodIntake",
        "Give any advices/suggestions to the user based on the scores given: " +
                "total HEIFA scores: ${currUser.totalHEIFAScore}, " +
                "discretionary HEIFA scores: ${currUser.discretionaryHEIFAScore}, " +
                "fruits HEIFA scores: ${currUser.fruitHEIFAScore}" +
                "vegetable HEIFA scores: ${currUser.vegetableHEIFAScore}, " +
                "grains and cereals HEIFA scores: ${currUser.grainAndCerealsHEIFAScore}, " +
                "whole grains HEIFA scores: ${currUser.wholeGrainsHEIFAScore}, " +
                "meat and alternatives HEIFA scores: ${currUser.meatAndAlternativeHEIFAScore}, " +
                "sodium HEIFA scores: ${currUser.sodiumHEIFAScore}, " +
                "alcohol HEIFA scores: ${currUser.alcoholHEIFAScore}, " +
                "water HEIFA scores: ${currUser.waterHEIFAScore}, " +
                "sugar HEIFA scores: ${currUser.sugarHEIFAScore}, " +
                "saturated HEIFA scores: ${currUser.saturatedFatHEIFAScore}, " +
                "unsaturated fat HEIFA scores: ${currUser.unsaturatedFatHEIFAScore}"
    )

    // AI button
    Column (
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
    ) {
        Button(
            onClick = {
                // random choose prompt str
                val randomIndex = Random.nextInt(0,promptList.size)
                prompt = promptList[randomIndex]

                // send prompt to ai
                genAiViewModel.sendPrompt(prompt)
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
                Image(
                    painter = painterResource(R.drawable.icons8_speech_bubble_50),
                    contentDescription = "message icon",
                    modifier = Modifier.size(30.dp).offset(x = (-10).dp)
                )

                // text
                Text(
                    text = "Motivational Message (AI)",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        /** AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in refactoring and
         * debugging to save the genAI response into database in the following code lines below.
         */
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
                genResult = null // x store err msg in db
            }
            else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
                // store result into temp var to avoid double save
                if (result != placeholderResult && result.isNotBlank()) {
                    genResult = result
                }
            }

            // AI response
            Text(
                text = result,
                textAlign = TextAlign.Justify,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        }
    }

    // state to control visibility of AlertDialog
    var showGenAIResponseDialog by remember { mutableStateOf(false) }

    // show all tips button
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                // Validate that non-empty result before saving
                // Only save if we have a valid AI response
                genResult?.let { responseText ->
                    val response = NutriCoachTips(
                        generatedAIResponse = responseText,
                        userID = currUser.userID
                    )

                    // launch coroutine to insert genAI in background, x block UI thread
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // save response to db
                            nutriCoachTipsViewModel.insert(response)
                            Log.d(TAG, "Successfully saved GenAI Response: $responseText")
                            Log.d(TAG, "Current user id: ${currUser.userID}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error saving GenAI Response: ${e.message}")
                        }
                    }
                }

                    // show modal
                    showGenAIResponseDialog = true
                }
        ) {
            // icon
            Image(
                painter = painterResource(R.drawable.icons8_tip_50),
                contentDescription = "tips icon",
                modifier = Modifier.offset(x = (-10).dp)
            )

            Text (
                text = "Show All Tips"
            )
        }
    }

    // generate GenAI response list
    val resList by nutriCoachTipsViewModel.getAllResponsesById(currUser.userID)
        .collectAsState(initial = emptyList())

    /**
     * References:
     * The following code lines below were taken from the following ed lesson with some modifications.
     *  Lab 3, Section 2.0 - Dealing with Modals: Creating Focused User Interactions:
     *      https://edstem.org/au/courses/20813/lessons/77916/slides/527591
     *  Lab 9: Tweet Posts - Step 6: MainActivity.kt – UI Entry Point:
     *      https://edstem.org/au/courses/20813/lessons/81098/slides/550979
     **/
    if (showGenAIResponseDialog){
        // open dialog, show them as a list
        AlertDialog(
            modifier = Modifier.fillMaxSize(),
            // switch visibility of dialog if user close it
            onDismissRequest = { showGenAIResponseDialog = false },
            title = {
                Text(
                    text = "AI Tips",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(5.dp)
                )},
            text = {
                // AI response list
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(5.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resList) {
                        response -> GenAIResponse(response)
                    }
                }
            },
            confirmButton = {},

            dismissButton = {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            // close the modal after done viewing
                            showGenAIResponseDialog = false
                        }
                    ) {
                        Text(
                            text = "Done"
                        )
                    }
                }
            }
        )
    }
}

/**
 * Composable function representing GenAI response item
 *
 * @param nutriCoachTips [nutriCoachTips] instance
 *
 */
@Composable
fun GenAIResponse(nutriCoachTips: NutriCoachTips) {

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
            Text(
                text = nutriCoachTips.generatedAIResponse,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}




