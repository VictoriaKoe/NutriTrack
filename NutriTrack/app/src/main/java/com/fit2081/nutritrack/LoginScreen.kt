package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.utilities.StateManager
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientViewModel
import kotlinx.coroutines.runBlocking

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // access to patient view model
            // initialise PatientViewModel using ViewModerProvider with factory
            // survive configuration changes and maintain state
            val patientViewModel: PatientViewModel =
                ViewModelProvider(
                    this,
                    PatientViewModel.PatientViewModelFactory(this@LoginScreen)
                )[PatientViewModel::class.java]

            NutriTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Login(this@LoginScreen, patientViewModel)
                }
            }
        }
    }
}

/**
 * Composable function representing login screen.
 *
 * @param context: Context of activity
 * @param patientViewModel view model to access patient data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun Login(context: Context, patientViewModel: PatientViewModel) {

    /**
     * References:
     * The following code from lines [] was taken from the following ed lesson with some modifications.
     *  Lab 3, Section 1.0 - Input Validation: https://edstem.org/au/courses/20813/lessons/77916/slides/527067
     *  Lab 2, Section 2.0 - Basic Navigation: https://edstem.org/au/courses/20813/lessons/77147/slides/519130
     *  Lab 8: Part 3: Authentication Manager: https://edstem.org/au/courses/20813/lessons/80772/slides/547303
     *  Lab 8: Part 6: Teacher Dashboard: https://edstem.org/au/courses/20813/lessons/80772/slides/547333
     **/
    // state to hold user id & password value
    var registeredUserID by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // username/pass if any configuration changes
    val rememberUserID = remember { registeredUserID }
    val rememberPassword = remember { password }

    // user state management
    val isLoggedIn = remember { mutableStateOf(false) } // track login status

    // error flags for validation
    var isPasswordEmpty by remember { mutableStateOf(false)}

    // manage user login status
    val stateManager = StateManager(context)

    // column layout for entire screen
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // screen title
        Text(
            text = "Login",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // registered user id
        registeredUserID = dropdownMenuUserID(patientViewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // password text field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordEmpty = password.isEmpty()// password field empty
            },
            label = {Text("Password")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isPasswordEmpty,
            placeholder = { Text("Enter your password") }
        )

        // validate error
        // error msg for password
        if (isPasswordEmpty) {
            Text(
                text = "Please enter valid password.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // text
        Text(
            text = "This app is only for pre-registered users. Please enter\n" +
                    "your ID and password or Register to claim your\n" +
                    "account on your first visit.",
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // continue button
        // validate, no err, navigate to questionnaire
        // show err msg if error, incorrect input
        var errorMsg by remember { mutableStateOf("") }

        Button(
            enabled = registeredUserID.isNotEmpty() && password.isNotEmpty(),
            onClick = {
                // validate id & pass match in db
                isLoggedIn.value = isAuthorized(
                    userID = registeredUserID,
                    password = password,
                    patientViewModel = patientViewModel
                )

                // if both match, then navigate
                if (isLoggedIn.value) {
                    // store user id in auth manager for user session tracking
                    AuthManager.login(userId = registeredUserID)

                    // save to state manager for login tracking
                    stateManager.loginUserSession(userId = registeredUserID)

                    // navigate to questionnaire screen using Intent and startActivity
                    context.startActivity(Intent(context, FoodIntakeQuestionnaireScreen::class.java))
                }
                else {
                    // not match, show err msg
                     errorMsg = "ID and password do not match each other. Please try again"
                    Toast.makeText(context, "ID and Password do not match each other. Please try again", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text= "Continue",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.heightIn(7.dp))

        // Register user button
        // first-time user register & claim acc
        Button(
            onClick = {
                // check is first time user
                if (!isFirstTimeUser(registeredUserID,patientViewModel)) {
                    val intent = Intent(context, RegisterScreen::class.java)
                    context.startActivity(intent)
                }
                else {
                    // not match, show err msg
                    errorMsg = "Your account has already been claimed and cannot be registered again"
                    Toast.makeText(context, "One-time registration has been completed. It cannot be done again", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "Register",
                fontSize = 16.sp
            )
        }

        /**
         * AI Acknowledgment:
         * I acknowledge the use of Claude (https://claude.ai/) to aid me in debugging the
         * error validation message in the following code lines below.
         **/
        // Only show error text if res is false and errorMsg is not empty
        if (!isLoggedIn.value || errorMsg.isNotEmpty()) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Validates current user credentials against db
 *
 * @param userID user ID to authenticate
 * @param password password to validate
 * @param patientViewModel view model to access patient data
 *
 * @return boolean to indicate authentication as successful
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
fun isAuthorized(userID: String?, password: String, patientViewModel: PatientViewModel): Boolean {

    /**
     * References:
     * The following code from lines 267-287 was taken from the following ed lesson with some modifications.
     *  Lab 8: Part 7: Main Activity: https://edstem.org/au/courses/20813/lessons/80772/slides/547335
     **/

    // current user/patient
    var aPatient: Patient
    val userId: Int? = userID?.toIntOrNull()

    // use blocking call to retrieve user by id async
    runBlocking {
        val aFlowPatient: Patient = patientViewModel.getPatientByID(userId)
        aPatient = aFlowPatient
    }

    // validates credentials
    if (aPatient == null) { // user id not exist
        return false
    }

    // user password matched
    if (aPatient.password != password) {
        return false
    }

    // success (all validate)
    return true
}

/**
 * Validates current user whether is first time user
 *
 * @param userID user ID to authenticate
 * @param patientViewModel view model to access patient data
 *
 * @return boolean to indicate authentication as successful
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
fun isFirstTimeUser(userID: String?, patientViewModel: PatientViewModel): Boolean {

    /**
     * References:
     * The following code from lines 267-287 was taken from the following ed lesson with some modifications.
     *  Lab 8: Part 7: Main Activity: https://edstem.org/au/courses/20813/lessons/80772/slides/547335
     **/

    // current user/patient
    var aPatient: Patient
    val userId: Int? = userID?.toIntOrNull()

    // use blocking call to retrieve user by id async
    runBlocking {
        val aFlowPatient: Patient = patientViewModel.getPatientByID(userId)
        aPatient = aFlowPatient
    }

    // validate user is first time register
    if (aPatient == null) { // user id not exist, havent register
        return false
    }

    if (aPatient.isFirstTimeUser) {
        return true
    }

    return false // ret false as all user are first login user
}

/**
 * Composable function representing dropdown menu with registered user id.
 *
 * @return selected user id after user select from dropdown menu
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below including AI acknowledgement.
 */
// drop down menu user id
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropdownMenuUserID(patientViewModel: PatientViewModel): String {

    // expanded value, true if expanded, else false
    var expanded by remember { mutableStateOf(false) }

    // rmb state of user id selected
    var selectedUserID by remember { mutableStateOf("") }
    var idError by remember { mutableStateOf(false) }

    // get list of user id from view model
    // fetch all registered users and collect as state
    // update when underlying flow emits new data
    // initial val is empty list to avoid null states
    val userIDList by patientViewModel.getAllRegisteredPatients().collectAsStateWithLifecycle(
        initialValue = emptyList())

    /**
     * References:
     * The following code lines was taken from the external source as stated below to
     * with some modifications.
     *  https://medium.com/@santosh_yadav321/dropdown-menu-with-icon-in-jetpack-compose-5ebebae75851
     **/
    // drop down menu
    ExposedDropdownMenuBox (
        onExpandedChange = {
            expanded = !expanded
        },
        expanded = expanded,
    ) {
        OutlinedTextField(
            // properly anchoring the ExposedDropdownMenu,
            // handling semantics of the component, and requesting focus.
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = selectedUserID,
            onValueChange = {
                selectedUserID = it
                idError = selectedUserID.isEmpty()
            },
            readOnly = true,
            label = {
                Text("My ID (Provided by your Clinician)")
            },
            // icon displays at the end of text field
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = idError
        )

        // exposed dropdown menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                idError = selectedUserID.isEmpty()
            },
            modifier = Modifier
                /**
                 * AI Acknowledgment:
                 * I acknowledge the use of Claude (https://claude.ai/) to aid me in refining the
                 * dropdown menu in the following code lines below.
                 **/
                .exposedDropdownSize(true) // ensure width of dropdown followed outlined txt field
                .heightIn(max = 350.dp),
            scrollState = rememberScrollState()
        ) {
            // loop through data array to render all dropdown menu item
            userIDList.forEachIndexed {_, userID ->
                 DropdownMenuItem(
                     text = {
                         Row(
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             Text(text = "${userID.userID}")
                         }
                     },
                     onClick = {
                         // save user id
                         selectedUserID = userID.userID.toString()
                         expanded = false
                         idError = false // hide err
                     },
                     contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                 )
            }
        }
    }

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 3, Section 1.0 - Input Validation: https://edstem.org/au/courses/20813/lessons/77916/slides/527067
     **/
    // check id input is empty
    if (idError) {
        Text(
            text = "User ID cannot be empty. Please select it again.",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top=4.dp)
        )
    }

    // return selected user id
    return selectedUserID
}








