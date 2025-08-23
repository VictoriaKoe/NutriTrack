package com.fit2081.nutritrack

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegisterScreen : ComponentActivity() {
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
                    PatientViewModel.PatientViewModelFactory(this@RegisterScreen)
                )[PatientViewModel::class.java]

            NutriTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Register(this@RegisterScreen, patientViewModel)
                }
            }
        }
    }
}

/**
 * Composable function representing register screen.
 *
 * @param context: Context of activity
 * @param patientViewModel view model to access patient data
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun Register(context: Context, patientViewModel: PatientViewModel) {

    /**
     * References:
     * The following code from lines [] was taken from the following ed lesson with some modifications.
     * Lab 8
     **/
    // state to hold user id, password, phone num, confirm password, name value
    var selectedUserID by remember { mutableStateOf("") }
    var phNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    // error flags for validation
    var phNumError by remember { mutableStateOf(false) }
    var isPasswordNotMatched by remember { mutableStateOf(false)}
    var usernameError by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false)}
    var isConfirmPasswordEmpty by remember { mutableStateOf(false) }

    // column layout for entire screen
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // screen title
        Text(
            text = "Register",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // register user id dropdown menu
        selectedUserID = dropdownMenuRegisterUserID(patientViewModel)

        Spacer(modifier = Modifier.height(15.dp))

        // ph num text field
        OutlinedTextField(
            value = phNumber,
            onValueChange = {
                phNumber = it
                // phone number must match the one in db
                phNumError = !validatePhoneNumInputFromDB(phNumber, patientViewModel)
            },
            label = {Text("Phone Number")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = phNumError,
            placeholder = { Text("Enter your phone number") }
        )

        // validate error from db
        if (phNumError) {
            Text(
                text = "Invalid phone number",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top=4.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // name
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = username.isEmpty() || validateUsernameExist(username, patientViewModel)
            },
            label = {Text("Username")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = usernameError,
            placeholder = { Text("Enter your username") }
        )

        // validate error from db
        if (usernameError) {
            Text(
                text = if (username.isEmpty())"Invalid username"
                        else if (validateUsernameExist(username, patientViewModel)) "Username has been taken. Please retry again"
                        else "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top=4.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // password text field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordEmpty = password.isEmpty() // validate empty input
            },
            label = {Text("Password")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = isPasswordEmpty,
            placeholder = { Text("Enter your password") }
        )

        // error msg for password
        if (isPasswordEmpty) {
            Text(
                text = "Please enter valid password.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // confirm password text field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                isConfirmPasswordEmpty = confirmPassword.isEmpty()
                isPasswordNotMatched = !validatePasswordMatched(password, confirmPassword)
            },
            label = {Text("Confirm Password")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = isConfirmPasswordEmpty || isPasswordNotMatched,
            placeholder = { Text("Enter your password again") }
        )

        // error msg for confirm password
        if (isConfirmPasswordEmpty) {
            Text(
                text = "Please enter valid password.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }

        if (isPasswordNotMatched) {
            Text(
                text = "Password is not matched. Please enter again.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // text
        Text(
            text = "This app is only for pre-registered users. Please enter\n" +
                    "your ID, phone number and password to claim your\n" +
                    "account.",
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // register button
        // validate, no err, password of the current user id is saved to db
        // show err msg if error, fail to register, user id and phone num not match with db
        var res by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf("")}

        Button(
            enabled = validateEmptyInput(selectedUserID, phNumber, password),
            onClick = {
                // validate ph & ph num match in db
                // if both match, then save
                if (validateIDAndPhoneNumFromDB(selectedUserID, phNumber, patientViewModel)) {
                    res = true

                    /**
                     * References:
                     * The following code lines were taken from the following ed lesson with some modifications.
                     *  Lab 8, Database, Navigation with Routing, MVVM - Part 7: MainActivity class:
                     *      https://edstem.org/au/courses/20813/lessons/80772/slides/547335
                     **/
                    // patient details to be inserted into db
                    // launch coroutine to update patient in background, x block UI thread
                    CoroutineScope(Dispatchers.IO).launch {
                        patientViewModel.updatePatientDetails(username, password, true, selectedUserID.toInt()) // insert new std in db
                        Log.d(TAG, "Added Details: $username, $password, $selectedUserID") // log success for debug

                        // reset input after validation
                        phNumber = ""
                        username = ""
                        password = ""
                        confirmPassword = ""
                    }
                }
                else {
                    // not match/register, show err msg
                    errorMsg = "Registration failed. Please try again."
                    Toast.makeText(context, "ID and Phone Number do not match each other. Fail to register.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text= "Register",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.heightIn(7.dp))

        // Login button
        // navigate to login screen after successful save to db
        Button(
            onClick = {
                val intent = Intent(context, LoginScreen::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp
            )
        }

        // Only show error text if res is false and errorMsg is not empty
        if (!res && errorMsg.isNotEmpty()) {
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
 * Composable function representing dropdown menu with all user id.
 *
 * @param patientViewModel view model to access patient data
 *
 * @return selected user id after user select from dropdown menu
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below including AI acknowledgement.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropdownMenuRegisterUserID(patientViewModel: PatientViewModel): String {

    // expanded value, true if expanded, else false
    var expanded by remember { mutableStateOf(false) }

    // rmb state of user id selected
    var selectedUserID by remember { mutableStateOf("") }
    var idError by remember { mutableStateOf(false) }

    // get list of user id from view model
    // fetch all registered users and collect as state
    // update when underlying flow emits new data
    // initial val is empty list to avoid null states
    val userIDList by patientViewModel.getAllUnregisteredPatients().collectAsStateWithLifecycle(
        initialValue = emptyList())

    /**
     * References:
     * The following code lines were taken from the external source as stated below to
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

/**
 * Function validating both user id and phone number matched before register
 *
 * @param userID user ID
 * @param phoneNum user input phone number
 * @param patientViewModel view model to access patient data
 *
 * @return true if match, else false
 *
 */
// validate user id and phone number matched in db
private fun validateIDAndPhoneNumFromDB(userID: String?, phoneNum: String?, patientViewModel: PatientViewModel): Boolean {

    var isMatched = false

    // current user
    var aNewUser: Patient
    val userId: Int? = userID?.toIntOrNull()

    // Collect the latest value from the Flow
    runBlocking {
        val newFlowUser: Patient = patientViewModel.getPatientByID(userId)
        aNewUser = newFlowUser
    }

    // validate
    if (aNewUser.phoneNumber == phoneNum) {
        println("Phone number: ${aNewUser.phoneNumber}")
        isMatched = true // both match
    }

    return isMatched
}

/**
 * Function validating user input phone number exist in Room database
 *
 * @param phoneNum user input phone number
 * @param patientViewModel view model to access patient data
 *
 * @return true if match, else false
 *
 */
// validate phone number matched in db
private fun validatePhoneNumInputFromDB(phoneNum: String, patientViewModel: PatientViewModel): Boolean {

    // phone list
    var phoneList: List<String>

    // Collect the latest value from the Flow
    runBlocking {
        val phoneFlowList = patientViewModel.getAllPatientsPhoneNumber()
        phoneList = phoneFlowList
    }

    return phoneList.contains(phoneNum)
}

/**
 * Function validating both user input passwords are matched
 *
 * @param password user input phone number
 * @param confirmPassword user input reconfirm password
 *
 * @return true if match, else false
 *
 */
// validate password input is matched
private fun validatePasswordMatched(password: String, confirmPassword: String): Boolean {

    var isMatched = false

    // double check password is empty
    if (password.isNotEmpty() && confirmPassword.isNotEmpty()){
        // check password match
        if (password == confirmPassword)
            isMatched = true
    }
    else {
        isMatched = false
    }

    return isMatched
}

/**
 * Function validating empty inputs
 *
 * @param userID user ID
 * @param phoneNum user input phone number
 * @param password user input phone number
 *
 * @return true if match, else false
 *
 */
// validate empty input
private fun validateEmptyInput(userID: String, phoneNum: String, username: String): Boolean {

    return userID.isNotEmpty() && phoneNum.isNotEmpty() && username.isNotEmpty()
}

/**
 * Function validating username exist in the database or not. If yes, registration failed.
 *
 * @param userID user ID
 * @param phoneNum user input phone number
 * @param password user input phone number
 *
 * @return true if match, else false
 *
 */
// validate username cannot be same
fun validateUsernameExist(username: String, patientViewModel: PatientViewModel): Boolean {

    val userList: List<Patient?>

    runBlocking {
        // Use first() to get the first emission from the flow
        val newFlowUser: List<Patient> = patientViewModel.getAllUnregisteredPatients().first()
        userList = newFlowUser
    }

    return userList.any { patient ->
        patient?.username == username
    }
}




