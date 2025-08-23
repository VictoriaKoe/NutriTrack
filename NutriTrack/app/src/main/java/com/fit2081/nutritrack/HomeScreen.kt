package com.fit2081.nutritrack

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.NutriCoachTips.NutriCoachTipsViewModel
import com.fit2081.nutritrack.data.foodIntake.FoodIntakeViewModel
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.runBlocking

class HomeScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // access to patient view model
            val patientViewModel: PatientViewModel =
                ViewModelProvider(
                    this,
                    PatientViewModel.PatientViewModelFactory(this@HomeScreen)
                )[PatientViewModel::class.java]

            // access to nutriCoachTips view model
            val nutriCoachTipsViewModel: NutriCoachTipsViewModel =
                ViewModelProvider(
                    this,
                    NutriCoachTipsViewModel.NutriCoachViewModelFactory(this@HomeScreen)
                )[NutriCoachTipsViewModel::class.java]

            // access to FoodIntake view model
            val foodIntakeViewModel: FoodIntakeViewModel =
                ViewModelProvider(
                    this,
                    FoodIntakeViewModel.FoodIntakeViewModelFactory(this@HomeScreen)
                )[FoodIntakeViewModel::class.java]

            NutriTrackTheme {
                // initialise NavHostController for managing nav within app
                val navController: NavHostController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // defines bottom nav bar
                        MainBottomAppBar(navController)
                    }
                ) { innerPadding ->
                    // place bottom bar nav host within scaffold
                    // apply padding, content x overlap with sys UI elems
                    Column {
                        // calls NavHostHome define nav graph
                        HomeScreenContent(
                            innerPadding,
                            navController,
                            patientViewModel,
                            nutriCoachTipsViewModel,
                            foodIntakeViewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function wrapping MainNavHost by column
 *
 * @param innerPadding provides necessary padding values
 * @param navController instance of Navigation Host Controller
 * @param patientViewModel view model to access patient data
 * @param nutriCoachTipsViewModel view model to access nutriCoachTips data
 * @param foodIntakeViewModel view model to access food intake data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// wraps MainNavHost by col
@Composable
fun HomeScreenContent(
    innerPadding: PaddingValues,
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    nutriCoachTipsViewModel: NutriCoachTipsViewModel,
    foodIntakeViewModel: FoodIntakeViewModel,
){
    /**
     * References:
     * The following code lines below ware taken from the following ed lesson with some modifications.
     *  Lab 8, Part 6: Teacher Dashboard: https://edstem.org/au/courses/20813/lessons/80772/slides/547333
     **/

    Column {
        // calls NavHostHome define nav graph
        NavHostHome(
            innerPadding = innerPadding,
            navController = navController,
            patientViewModel = patientViewModel,
            nutriCoachTipsViewModel = nutriCoachTipsViewModel,
            foodIntakeViewModel = foodIntakeViewModel,
        )
    }
}

/**
 * Composable function representing bottom navigation bar
 *
 * @param navController instance of Navigation Host Controller
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// create bottom nav bar
@Composable
fun MainBottomAppBar(navController: NavHostController) {

    /**
     * References:
     * The following code lines were taken from the following ed lesson.
     *  Lab 4, Section C - Navigation with Jetpack Compose: https://edstem.org/au/courses/20813/lessons/78691/slides/531706
     **/
    // state to track currently selected item in the bottom nav bar
//    var selectedItem by remember { mutableStateOf(0) }
    // list of nav items
    val items = listOf("Home", "Insights", "NutriCoach", "Settings")

    /** AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in enhancing
     * bottom navigation especially navigating to Insight screen in the following code lines below.
     */
    // Get current route
    // currentBackStackEntry - holds latest route (screen) info
    // navController.currentBackStackEntryAsState() - State<NavBackStackEntry?>
    // currentBackStackEntryAsState() - composable state automatically updates whenever the navigation changes
    // navigate to a different screen, this State object gets updated
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    // get curr route name
    val currentRoute = currentBackStackEntry?.destination?.route

    // Calculate selected item based on current route
    var selectedItem = when (currentRoute) {
        "Home" -> 0
        "Insights" -> 1
        "NutriCoach" -> 2
        "Settings" -> 3
        else -> 0 // Default to Home if route doesn't match
    }

    // NavigationBar - define bottom nav bar
    NavigationBar {
        // iterate each item in the list along with idx
        items.forEachIndexed{index, item ->
            // NavigationBarItem -> each item in list
            NavigationBarItem(
                // define icon based on item's name
                icon = {
                    when (item) {
                        // if item == home, show home icon
                        "Home" -> Icon(Icons.Filled.Home, contentDescription = "home")
                        // if item == Insights, show Insights icon
                        "Insights" -> Image(
                            painter = painterResource(R.drawable.insight_icon),
                            contentDescription = "insights",
                            modifier = Modifier.size(24.dp))
                        // if item == NutriCoach, show NutriCoach icon
                        "NutriCoach" -> Image(
                            painter = painterResource(R.drawable.icons8_support_48),
                            contentDescription = "insights",
                            modifier = Modifier.size(24.dp)
                        )
                        // if item == Settings, show Settings icon
                        "Settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                // display item name as label
                label = {
                    Text(
                        text = item,
                        fontWeight = if (selectedItem == index) FontWeight.SemiBold else FontWeight.Normal
                    )},
                // determine if curr item is selected
                selected = selectedItem == index,
                // actions to perform when this item is clicked
                onClick =  {
                    // update selectedItem state to the curr idx
                    selectedItem = index
                    // navigate to corresponding screen based on item's name
                    navController.navigate(item)
                },
                /** AI Acknowledgment:
                 * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
                 * navigation colours to have better contrast in the following code below.
                 */
                // Enhanced colors for better contrast
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
        // close navigation
    }
}

/**
 * Composable function representing navigation within the app
 *
 * @param innerPadding provides necessary padding values
 * @param navController instance of navigation host controller
 * @param patientViewModel view model to access patient data
 * @param nutriCoachTipsViewModel view model to access nutriCoachTips data
 * @param foodIntakeViewModel view model to access food intake data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// composable fun for navigation within app
@Composable
fun NavHostHome(
    innerPadding: PaddingValues,
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    nutriCoachTipsViewModel: NutriCoachTipsViewModel,
    foodIntakeViewModel: FoodIntakeViewModel
) {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 4, Section C - Navigation with Jetpack Compose: https://edstem.org/au/courses/20813/lessons/78691/slides/531706
     **/
    // NavHost -> define navigation graph
    // displays the current destination based on the navigation state.
    NavHost(
        // usr provide NavHostController
        navController = navController,
        // set start destination to "home"
        startDestination = "Home"
    ) {
        // define composable "home" route
        composable("Home") {
            Home(
                innerPadding = innerPadding,
                onInsightButtonClick = {
                    /** AI Acknowledgment:
                     * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
                     * navigation to the Insight page feature in the following code below.
                     */
                    // Navigate to Insight screen when button is clicked
                    navController.navigate("Insights")
                },
                patientViewModel = patientViewModel
            )
        }

        composable("Insights") {
            Insight(
                modifier = Modifier.padding(innerPadding),
                onNutriCoachButtonClick = {
                    /** AI Acknowledgment:
                     * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
                     * navigation to the NutriCoach page feature in the following code lines below.
                     */
                    // Navigate to NutriCoach screen when button is clicked
                    navController.navigate("NutriCoach")
                },
                patientViewModel = patientViewModel
            )
        }

        composable("NutriCoach") {
            NutriCoach(
                innerPadding = innerPadding,
                patientViewModel = patientViewModel,
                nutriCoachTipsViewModel = nutriCoachTipsViewModel,
                foodIntakeViewModel = foodIntakeViewModel
            )
        }

        composable("Settings") {
            Settings(
                innerPadding = innerPadding,
                patientViewModel = patientViewModel,
                navController = navController
            )
        }

        /** AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
         * navigation to the Clinician screen feature using navHostController in the following code below.
         */
        // Add new composable for Clinician screen, for setting composable
        composable("Clinician") {
            ClinicianLogin(
                navController = navController
            )
        }

        // Add new composable for Clinician View screen, for clinician composable
        composable("ClinicianView") {
            ClinicianView(
                innerPadding = innerPadding,
                patientViewModel = patientViewModel,
                navController = navController
            )
        }
    }
}

/**
 * Composable function representing home screen.
 *
 * @param innerPadding provides necessary padding values
 * @param onInsightButtonClick navigation button to navigate to the Insight Page
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun Home(innerPadding: PaddingValues, onInsightButtonClick: () -> Unit, patientViewModel: PatientViewModel) {

    val context = LocalContext.current

    // get current user data
    val currUserID: Int? = AuthManager.getCurrentUserId()?.toInt()
    Log.d(TAG, "Current user id: ${currUserID}")

    var currentUser: Patient

    runBlocking {
        currentUser = patientViewModel.getPatientByID(currUserID)
    }

    // get curr user food quality score
    val foodQualityScore = currentUser.totalHEIFAScore

    // make screen scrollable
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(15.dp)
            .verticalScroll(scrollState, enabled = true)
    ) {
        // welcome user title
        Text(
            text = "Hello, ",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray,
            fontSize = 20.sp
        )

        // user id
        Text(
            text = "User ${currentUser.username}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        // row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // short txt
            Text(
                text = "You've already filled in your Food Intake " +
                        "Questionnaire, but you can change details here:",
                modifier = Modifier.weight(2f),
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 0.20.sp
            )

            /**
             * References:
             * The following code lines were taken from the following ed lesson with some modifications.
             *  Lab 2, Section 2.0 - Basic Navigation: https://edstem.org/au/courses/20813/lessons/77147/slides/519130
             *
             * AI Acknowledgment:
             * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
             * error and refining the UI of progress bar in the following code lines below.
             **/
            // edit button
            Button(
                onClick = {
                    // nav back to questionnaire screen
                    context.startActivity(
                        Intent(
                            context,
                            FoodIntakeQuestionnaireScreen::class.java
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(start = 3.dp).weight(0.62f)
            ) {
                // edit icon
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "edit",
                    modifier = Modifier.size(16.dp).offset(x = (-5).dp)
                )
                Text(
                    text = "Edit",
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // dietary image
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 70.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.austria_dietary_guidelines_plant_based_food_protein_meat_dairy_1),
                contentDescription = "dietary",
                modifier = Modifier.size(250.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        // score
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My Score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(2f)
            )

            /**
             * References:
             * The following code from lines 252 to 211 references to the following ed lesson to implement
             * navigation feature for the icon button and icon.
             *  Lab 2, Section 4.0 - Top Menu Bar: https://edstem.org/au/courses/20813/lessons/77147/slides/522889
             *
             **/
            Row(
                modifier = Modifier
                    .clickable(true, "insight", null, { onInsightButtonClick() })
                    .offset(x = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // see all scores
                Text(
                    text = "See all scores",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 15.sp
                )

                /** AI Acknowledgment:
                 * I acknowledge the use of Claude (https://claude.ai/) to aid me in implementing
                 * navigation to the Insight page feature in the following code lines 269 to 281 below.
                 */
                // icon
                IconButton(
                    onClick = {
                        // nav to insight screen
                        onInsightButtonClick()
                    },
                    modifier = Modifier.offset(x = -10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "nav right",
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        /** AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging and refining
         * UI of rendering total food quality score in the following code lines below.
         */
        // food quality score
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon
            Image(
                painter = painterResource(R.drawable.icons8_up_50),
                contentDescription = "up icon",
                modifier = Modifier.size(25.dp).offset(x = (-5).dp)
            )

            // title
            Text(
                text = "Your Food Quality Score",
                modifier = Modifier.weight(2f).padding(start = 5.dp),
                fontSize = 16.sp
            )

            // score
            Text(
                text = "${foodQualityScore}/100",
                color =
                    if (foodQualityScore >= 80) {
                        Color.Green
                    } else if (foodQualityScore >= 50 && foodQualityScore < 80) {
                        Color(0xF5FD5D34)
                    } else {
                        Color.Red
                    },
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(20.dp))

        // food score definition
        Text(
            text = "What is Food Quality Score?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        Spacer(Modifier.heightIn(5.dp))

        Text(
            text = "Your Food Quality Score provides a snapshot of how well your " +
                    "eating patterns align with established food guidelines, helping " +
                    "you identify both strengths and opportunities for improvement " +
                    "in your diet.\n\n" +
                    "This personalized measurement considers various food groups " +
                    "including vegetables, fruits, whole grains,and proteins to give " +
                    "you practical Insights for making healthier food choices.\n",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
    }
}





