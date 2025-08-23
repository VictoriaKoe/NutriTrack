package com.fit2081.nutritrack

import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.utilities.StateManager
import com.fit2081.nutritrack.data.foodIntake.FoodIntake
import com.fit2081.nutritrack.data.foodIntake.FoodIntakeViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodIntakeQuestionnaireScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // access to food intake view model
            val foodIntakeViewModel: FoodIntakeViewModel =
                ViewModelProvider(
                    this,
                    FoodIntakeViewModel.FoodIntakeViewModelFactory(this@FoodIntakeQuestionnaireScreen)
                )[FoodIntakeViewModel::class.java]

            NutriTrackTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(foodIntakeViewModel) }
                ) {
                    innerPadding ->
                        Column (
                            modifier = Modifier.padding(innerPadding)
                        ){}
                }
            }
        }
    }
}

/**
 * Composable function representing top application bar of a screen.
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// top bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(foodIntakeViewModel: FoodIntakeViewModel){

    /**
     * References:
     * The following code below references to the following ed lesson to implement
     * navigation feature for the icon button and icon.
     *  Lab 2, Section 4.0 - Top Menu Bar: https://edstem.org/au/courses/20813/lessons/77147/slides/522889
     *
     **/
    // TopAppBarState obj - control behaviour of TopAppBar
    // RememberTopAppBarState() -  composable function creates TopAppBarState that is remembered

    // across compositions
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    // onBackPressedDispatcher - handle back button press in the app
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // colors - customise the appearance of TopAppBar
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),

                // title displayed in the center of app bar
                title = {
                    Text(
                        "Food Intake Questionnaire",
                        maxLines = 1,
                        //Ellipsis prop - truncate txt if exceeds the available space
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                },

                // navigation icon (back button) with appropriate behaviour
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // onBackPressedDispatcher - handle back button press in app
                            onBackPressedDispatcher?.onBackPressed()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localised description"
                        )
                    }
                },
            )
        }
    ) {
        innerPadding ->
        QuestionnaireScreen(
            modifier = Modifier.padding(innerPadding),
            foodIntakeViewModel = foodIntakeViewModel
        )
    }
}

/**
 * Composable function representing food intake questionnaire screen
 *
 * @param modifier modify the layout of composable
 * @param foodIntakeViewModel view model to access food intake data
 *
 */
@Composable
fun QuestionnaireScreen(modifier: Modifier = Modifier, foodIntakeViewModel: FoodIntakeViewModel) {

    // current context
    val context = LocalContext.current

    // manage user login status
    val stateManager = StateManager(context)

    // checkbox
    val foodList = listOf(
        "Fruits", "Vegetables", "Grains",
        "Red Meat", "Seafood", "Poultry",
        "Fish", "Eggs", "Nuts/Seeds"
    )

    // Track checked state for each item of checkbox
    // initialise all false, indicate checkbox is unchecked
    val checkedStates = remember { mutableStateListOf<Boolean>().apply {
        addAll(List(foodList.size) { false })
    }}

    // dropdown val
    // validate var not empty
    val selectedPersona = remember { mutableStateOf("")}

    // timings
    val mealTime = remember { mutableStateOf("") }
    val sleepTime = remember { mutableStateOf("") }
    val wakeTime = remember { mutableStateOf("") }
    var clickable by remember { mutableStateOf(false)}
    var timeId by remember { mutableStateOf("") }

    // call the function to return TimePickerDialog
    var showTimePickerDialog = ShowTimePickerDialog(mealTime)
    var showSleepTimePickerDialog = ShowTimePickerDialog(sleepTime)
    var showWakeTimePickerDialog = ShowTimePickerDialog(wakeTime)

    // get current user
    val currUserID: Int? = AuthManager.getCurrentUserId()?.toInt()

    // Loads all values including checkbox states for all food items
    // LaunchedEffect
    // launch coroutine in background
    // use it initialise the checkbox states if they're empty when it enter composition
    // Cancels the coroutine automatically when the composable is removed from the composition.
    // side effect in jetpack compose
    // recomposed with different keys, existing routine cancel
    LaunchedEffect(currUserID) {

        // double user record is existed in db
        if (currUserID != null) {
            val foodIntake = foodIntakeViewModel.getFoodIntakeByID(currUserID)
            println("Latest food intake: $foodIntake")

            if (foodIntake != null) {
                selectedPersona.value = foodIntake.persona
                mealTime.value = foodIntake.mealTime
                sleepTime.value = foodIntake.sleepTime
                wakeTime.value = foodIntake.wakeUpTime

                val checkBoxStatesList = listOf(
                    foodIntake.fruits,
                    foodIntake.vegetables,
                    foodIntake.grains,
                    foodIntake.redMeat,
                    foodIntake.seafood,
                    foodIntake.poultry,
                    foodIntake.fish,
                    foodIntake.eggs,
                    foodIntake.nutsSeeds
                )

                // Update checkbox states based on loaded data
                checkedStates.clear()
                checkedStates.addAll(checkBoxStatesList)
            }
        }
    }

    // First, add state variables to track validation errors
    val mealTimeError = remember { mutableStateOf(false) }
    val sleepTimeError = remember { mutableStateOf(false) }
    val wakeTimeError = remember { mutableStateOf(false) }

    // Reset all errors first
    mealTimeError.value = false
    sleepTimeError.value = false
    wakeTimeError.value = false

    // Check if any times are the same (and not empty)
    if (mealTime.value.isNotEmpty() && sleepTime.value.isNotEmpty() &&
        mealTime.value == sleepTime.value) {
        mealTimeError.value = true
        sleepTimeError.value = true
    }

    if (mealTime.value.isNotEmpty() && wakeTime.value.isNotEmpty() &&
        mealTime.value == wakeTime.value) {
        mealTimeError.value = true
        wakeTimeError.value = true
    }

    if (sleepTime.value.isNotEmpty() && wakeTime.value.isNotEmpty() &&
        sleepTime.value == wakeTime.value) {
        sleepTimeError.value = true
        wakeTimeError.value = true
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState(), true)
    ) {
        // food categories
        Text(
            text = "Tick all the food categories you can eat",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        /**
         * References:
         * The following code lines below were taken from the following ed lesson with some modifications.
         *  Lab 3, Section 3.0 - Shared Preferences, Date and Time pickers, Slider and Progress Bar:
         *  https://edstem.org/au/courses/20813/lessons/77916/slides/527595
         *
         * AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in checkbox
         * implementation in the following code lines below.
         **/
        // checkbox
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Create 3 rows
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Create 3 items in each row
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        // check idx not larger than list size
                        if (index < foodList.size) {
                            CheckboxItem(
                                text = foodList[index],
                                checked = checkedStates[index],
                                onCheckedChange = { checkedStates[index] = it },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Empty space to maintain grid alignment if fewer than 9 items
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // persona selections
        ShowPersonaModalAndButton()

        // select best-fit persona
        // dropdown
       DropdownMenuPersona(selectedPersona)

        Spacer(modifier = Modifier.height(15.dp))

        /**
         * References:
         * The following code lines below were taken from the following ed lesson with some modifications.
         *  Lab 3, Section 3.0 - Shared Preferences, Date and Time pickers, Slider and Progress Bar:
         *  https://edstem.org/au/courses/20813/lessons/77916/slides/527595
         *
         * AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in time pickers
         * implementation and debugging errors in the following code lines below.
         **/
        // time pickers (time modal)
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Timings",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            // validate error
            // Add error message below meal/sleep/wake up time if needed
            if (mealTimeError.value || sleepTimeError.value || wakeTimeError.value) {
                Text(
                    text = "Times must be different",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(5.dp))

        // meal time
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // question
            Text(
                text = "What time of day approx. do you normally eat your biggest meal?",
                fontSize = 15.sp,
                modifier = Modifier.weight(2f)
            )
            // time modal
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                    )
                    .clickable {
                        clickable = true
                        timeId = "eat"
                    }
                    .padding(horizontal = 15.dp, vertical = 3.dp)
                    .height(25.dp)
                    .weight(1f)
                    .fillMaxWidth(0.5f)
            ) {
                // time icon
                Image(
                    painter = painterResource(R.drawable.icons8_clock_48),
                    contentDescription = "time",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 3.dp)
                )

                // show time/placeholder
                Text(
                    text = if (mealTime.value.isEmpty()) "0:00" else mealTime.value,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (mealTime.value.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(6.dp))

        //sleep time
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // question
            Text(
                text = "What time of day approx. do you go to sleep at night?",
                fontSize = 15.sp,
                modifier = Modifier.weight(2f)
            )

            // time modal
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                    )
                    .clickable {
                        clickable = true
                        timeId = "sleep"
                    }
                    .padding(horizontal = 15.dp, vertical = 3.dp)
                    .height(25.dp)
                    .weight(1f)
                    .fillMaxWidth(0.5f)
            ) {

                // time icon
                Image(
                    painter = painterResource(R.drawable.icons8_clock_48),
                    contentDescription = "time",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 3.dp)
                )

                // show time/placeholder
                Text(
                    text = if (sleepTime.value.isEmpty()) "0:00" else sleepTime.value,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (sleepTime.value.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(10.dp))

        // wake time
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // question
            Text(
                text = "What time of day approx. do you wake up in the morning?",
                fontSize = 15.sp,
                modifier = Modifier.weight(2f)
            )

            // time modal
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                    )
                    .clickable {
                        clickable = true
                        timeId = "wake"
                    }
                    .padding(horizontal = 15.dp, vertical = 3.dp)
                    .height(25.dp)
                    .weight(1f)
                    .fillMaxWidth(0.5f)
            ) {

                // time icon
                Image(
                    painter = painterResource(R.drawable.icons8_clock_48),
                    contentDescription = "time",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 3.dp)
                )

                // show time/placeholder
                Text(
                    text = if (wakeTime.value.isEmpty()) "0:00" else wakeTime.value,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (wakeTime.value.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        // show time picker dialog if click
        if (clickable) {
            when (timeId) {
                "wake" -> showWakeTimePickerDialog.show()
                "sleep" -> showSleepTimePickerDialog.show()
                "eat" -> showTimePickerDialog.show()
                else -> showTimePickerDialog.show()
            }
            clickable = false
        }

        Spacer(modifier = Modifier.height(10.dp))

        /**
         * References:
         * The following code lines below ware taken from the following ed lesson with some modifications.
         *  Lab 2, Section 2.0 - Basic Navigation: https://edstem.org/au/courses/20813/lessons/77147/slides/519130
         *  Lab 8, Part 6: Teacher Dashboard: https://edstem.org/au/courses/20813/lessons/80772/slides/547333
         *
         * AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in save data to
         * the database with async function in the following code lines below.
         **/
        // save button
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.absolutePadding(left = 135.dp)
        ) {
            Button(
                // validate input, disabled button until all inputs are filled
                enabled = validateInput(checkedStates, selectedPersona.value, mealTime.value, sleepTime.value, wakeTime.value),
                onClick = {
                    // save to shared pref after complete the questionnaire
                    stateManager.completedQuestionnaire()

                    // save curr user food intake response into db
                    val foodIntake: FoodIntake = FoodIntake(
                        fruits = checkedStates[0],
                        vegetables = checkedStates[1],
                        grains = checkedStates[2],
                        redMeat = checkedStates[3],
                        seafood = checkedStates[4],
                        poultry = checkedStates[5],
                        fish = checkedStates[6],
                        eggs = checkedStates[7],
                        nutsSeeds = checkedStates[8],
                        persona = selectedPersona.value,
                        mealTime = mealTime.value,
                        sleepTime = sleepTime.value,
                        wakeUpTime = wakeTime.value,
                        userID = currUserID,
                    )

                     // launch coroutine to insert food intake in background, x block UI thread
                    CoroutineScope(Dispatchers.IO).launch {
                        // check if record exist and update, or else insert new record
                        val existingUserRecord: FoodIntake? = foodIntakeViewModel.getFoodIntakeByID(currUserID)
                        Log.d(TAG, "Exist user record: $existingUserRecord") // log success for debug

                        if (existingUserRecord != null && existingUserRecord.userID == currUserID) { // curr user record exist
                            // update existing record
                            foodIntakeViewModel.updateFoodById(
                                fruits = checkedStates[0],
                                vegetables = checkedStates[1],
                                grains = checkedStates[2],
                                redMeat = checkedStates[3],
                                seafood = checkedStates[4],
                                poultry = checkedStates[5],
                                fish = checkedStates[6],
                                eggs = checkedStates[7],
                                nutsSeeds = checkedStates[8],
                                persona = selectedPersona.value,
                                mealTime = mealTime.value,
                                sleepTime = sleepTime.value,
                                wakeUpTime = wakeTime.value,
                                userID = currUserID
                            )
                            Log.d(TAG, "Update FoodIntake: $foodIntake") // log success for debug
                        }
                        else { // record x exist
                            foodIntakeViewModel.insert(foodIntake) // insert new food intak in db
                            Log.d(TAG, "Added FoodIntake: $foodIntake") // log success for debug
                            Log.d(TAG, "Current user id: ${foodIntake.userID}")
                        }
                    }

                    // navigate to welcome screen using Intent and startActivity
                    context.startActivity(Intent(context, HomeScreen::class.java))
                },
            ) {
                // save icon
                Image(
                    painter = painterResource(id = R.drawable.save),
                    contentDescription = "Save icon",
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = (-5).dp)
                )
                Text("Save")
            }
        }
    }
}

/**
 * Composable function representing checkboxes of food categories
 *
 * @param text checkbox label
 * @param checked Boolean value to indicate checkbox is checked or unchecked
 * @param onCheckedChange invoke when this checkbox is clicked, handles its input events and update its states
 * @param modifier modify the layout of composable
 *
 */
// create checkbox item with label
@Composable
fun CheckboxItem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {

    /**
     * References:
     * The following code lines below were taken from the following ed lesson with some modifications.
     *  Lab 3, Section 3.0 - Shared Preferences, Date and Time pickers, Slider and Progress Bar:
     *  https://edstem.org/au/courses/20813/lessons/77916/slides/527595
     *
     * AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in
     * refining the checkbox implementation in the following code lines below.
     **/
    Row(
        modifier = modifier.padding(0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = text,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Visible,
        )
    }
}

/**
 * Function validating user input
 *
 * @param isCheckedList list of checkbox states of categories
 * @param selectedPersona user input persona
 * @param inputEatTime user input meal time
 * @param inputSleepTime user input sleep time
 * @param inputWakeTime user input wake up time
 *
 * @return true if all inputs are not empty otherwise false
 */
fun validateInput(
    isCheckedList: List<Boolean>,
    selectedPersona: String,
    inputEatTime: String,
    inputSleepTime: String,
    inputWakeTime: String
    ): Boolean {

    // def val
    var result = false

    // ensure all inputs are filled
    if (selectedPersona.isNotEmpty() && inputWakeTime.isNotEmpty() && inputSleepTime.isNotEmpty() && inputEatTime.isNotEmpty()){
        // loop list to check is filled or not
        isCheckedList.forEachIndexed {index, bool ->
            if (isCheckedList[index]) {
                result = true
            }
        }
    }

    return result
}

/**
 * Data class representing persona of the modal to be displayed
 *
 */
// persona modal item data class
// persona name, image, description
data class PersonaModalItem (
    val personaName: String,
    val description: String,
    val personaImage: Int
)

/**
 * Composable function representing each button displays persona information modal.
 *
 */
// button
@Composable
fun ShowPersonaModalAndButton() {

    // persona list
    val personaList = listOf(
        PersonaModalItem(
            "Health Devotee",
            "I’m passionate about healthy eating & health plays a big part in my life. " +
                    "I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. " +
                    "I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
            R.drawable.persona_1
        ),
        PersonaModalItem(
            "Mindful Eater",
            "I’m health-conscious and being healthy and eating healthy is important to me. " +
                    "Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. " +
                    "I look for new recipes and healthy eating information on social media.",
            R.drawable.persona_2
        ),
        PersonaModalItem(
            "Wellness Striver",
            "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! " +
                    "I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. " +
                    "Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
            R.drawable.persona_3
        ),
        PersonaModalItem(
            "Balance Seeker",
            "I try and live a balanced lifestyle, and I think that all foods are okay in moderation." +
                    "I shouldn’t have to feel guilty about eating a piece of cake now and again. " +
                    "I get all sorts of inspiration from social media like finding out about new restaurants, " +
                    "fun recipes and sometimes healthy eating tips.",
            R.drawable.persona_4
        ),
        PersonaModalItem(
            "Health Procrastinator",
            "I’m contemplating healthy eating but it’s not a priority for me right now. " +
                    "I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now." +
                    " I have taken a few steps to be healthier but I am not motivated to make it a high " +
                    "priority because I have too many other things going on in my life.",
            R.drawable.persona_5
        ),
        PersonaModalItem(
            "Food Carefree",
            "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. " +
                    "I don’t really notice healthy eating tips or recipes and I don’t care what I eat.",
            R.drawable.persona_6
        )
    )

    /**
     * References:
     * The following code lines below were taken from the following ed lesson with some modifications.
     *  Lab 3, Section 2.0 - Dealing with Modals: Creating Focused User Interactions:
     *  https://edstem.org/au/courses/20813/lessons/77916/slides/527591
     *
     * AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
     * error and refining the UI of progress bar in the following code lines below.
     **/
    // state to control visibility of AlertDialog
    var showPersonaDialog by remember { mutableStateOf(false) }

    var persona: PersonaModalItem by remember { mutableStateOf(PersonaModalItem("", "", 0)) }

    Text(
        text = "Your Persona",
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )

    Text(
        text = "People can be broadly classified into 6 different types based on their eating preferences. " +
                "Click on each button below to find out the different types, " +
                "and select the type that best fits you!\n",
        fontSize = 15.sp,
        modifier = Modifier
            .height(100.dp)
            .padding(0.dp)
    )

    /**
     * AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
     * feature of rendering personal modal button in the following code lines below.
     **/
    // 6 buttons
    // Create 3 rows
    for (row in 0 until 3) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Create 3 items in each row
            for (col in 0 until 3) {
                val index = row * 3 + col
                // check idx not larger than list size
                if (index < personaList.size) {
                    Button(
                        onClick = {
                            showPersonaDialog = true
                            persona = personaList[index]
                        },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp)
                            .padding(0.dp)
                    ) {
                        Text(
                            text = personaList[index].personaName,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center,
                            softWrap = true,
                            maxLines = 2,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // Empty space to maintain grid alignment if fewer than 9 items
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.heightIn(10.dp))
    }

    /**
     * References:
     * The following code lines below were taken from the following ed lesson with some modifications.
     *  Lab 3, Section 2.0 - Dealing with Modals: Creating Focused User Interactions:
     *  https://edstem.org/au/courses/20813/lessons/77916/slides/527591
     **/
    // toggle state to show dialog
    if (showPersonaDialog) {
        AlertDialog(
            // switch visibility of dialog if user close it
            onDismissRequest = { showPersonaDialog = false },
            title = {
                Text(
                    text = "Persona Information",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x=25.dp),
                )},
            text = {
                Column {
                    // persona image
                    Image (
                        painter = painterResource(persona.personaImage),
                        contentDescription = "persona #",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.heightIn((10.dp)))

                    // title
                    Text(
                        text = persona.personaName,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Justify,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.heightIn((5.dp)))

                    // description
                    Text(
                        text = persona.description,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            },

            confirmButton = {},

            dismissButton = {
                Button(
                    onClick = {
                        // directly close dialog after user click cancel but
                        showPersonaDialog = false
                    },
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

/**
 * Composable function representing dropdown menu with persona.
 *
 * @param selectedPersona persona to be selected
 */
// drop down menu persona
@Composable
fun DropdownMenuPersona(selectedPersona: MutableState<String>) {

    // expanded value, true if expanded, else false
    var expanded by remember { mutableStateOf(false) }

    // persona list
    val personaList = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    Text(
        text = "Which persona fits you?",
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )

    Spacer(modifier = Modifier.heightIn(5.dp))

    /**
     * References:
     * The following code lines below references to the following ed lesson to implement
     * dropdown menu with some modifications.
     *  Lab 2, Section 4.0 - Top Menu Bar: https://edstem.org/au/courses/20813/lessons/77147/slides/522889
     *
     * AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
     * error and refining the feature of dropdown menu in the following code lines below.
     **/
    // drop down menu
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 15.dp, vertical = 3.dp)
            .height(25.dp)
            .clickable { expanded = true }
    ) {

        Text(
            text = if (selectedPersona.value.isEmpty()) "Select Option" else selectedPersona.value,
            modifier = Modifier.align(Alignment.TopStart),
            color = if (selectedPersona.value.isEmpty()) Color.Gray else Color.Black,
            fontSize = 14.sp
        )

        IconButton(
            onClick = {expanded = true},
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown Arrow",
                modifier = Modifier.align(Alignment.CenterEnd),
                tint = Color.Gray
            )
        }

        // dropdown menu
        DropdownMenu (
            expanded = expanded,
            onDismissRequest  = { expanded = false },
            modifier = Modifier
                .verticalScroll(rememberScrollState(), true)
                .heightIn(max = 150.dp)
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.85f)
        ) {
            // loop through data array
            personaList.forEachIndexed {index, persona ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = persona)
                        }
                    },
                    onClick = {
                        // save persona
                        selectedPersona.value = personaList[index]
                        expanded = false
                    },
                    contentPadding = MenuDefaults.DropdownMenuItemContentPadding
                )
            }
        }
    }
}

/**
 * Function representing time picker dialog for user to select time.
 *
 * @param mTime state of user input time to be tracked
 *
 * @return time picker dialog to be rendered
 *
 */
// time picker dialog
@Composable
fun ShowTimePickerDialog(mTime: MutableState<String>): TimePickerDialog {

    /**
     * References:
     * The following code lines below was taken from the following ed lesson with some modifications.
     *  Lab 3, Section 3.0 - Shared Preferences, Date and Time pickers, Slider and Progress Bar:
     *  https://edstem.org/au/courses/20813/lessons/77916/slides/527595
     **/
    // get current context
    val mContext = LocalContext.current

    // get calendar instance
    val mCalendar = Calendar.getInstance()

    // get current hour and min
    val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = mCalendar.get(Calendar.MINUTE)

    // set calendar time to curr time
    mCalendar.time = Calendar.getInstance().time

    return TimePickerDialog(
        // context
        // listener to be invoked when time is set
        mContext,
        { _, mHour: Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
        },
        mHour,
        mMinute,
        true
    )
}



