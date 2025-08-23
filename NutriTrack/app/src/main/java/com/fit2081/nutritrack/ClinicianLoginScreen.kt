package com.fit2081.nutritrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme



// static key for clinician admin to login
private const val key_admin_view = "dollar-entry-apples"

/**
 * Composable function representing Clinician Login screen
 *
 * @param navController instance of navigation host controller
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLogin(
    navController: NavHostController
) {
    /**
     * References:
     * The following code below references to the following ed lesson to implement
     * navigation feature for the icon button and icon.
     *  Lab 2, Section 4.0 - Top Menu Bar: https://edstem.org/au/courses/20813/lessons/77147/slides/522889
     **/

    // onBackPressedDispatcher - handle back button press in the app
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // colors - customise the appearance of TopAppBar
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),

                // title displayed in the center of app bar
                title = {
                    Text(
                        text = "Clinician Login",
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
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
    ) { innerPadding ->
        ClinicianLoginView(
            innerPadding = innerPadding,
            navController = navController
        )
    }
}

/**
 * Composable function displaying login view screen
 *
 * @param innerPadding provides necessary padding values
 * @param navController instance of navigation host controller
 *
 */
@Composable
fun ClinicianLoginView(
    innerPadding: PaddingValues,
    navController: NavHostController
) {

    // status to track user input
    var accessKey by remember { mutableStateOf("") }

    // error validation
    var isValidKey by remember { mutableStateOf(false) }
    var isKeyEmpty by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val context = LocalContext.current


    // column layout for entire screen
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // admin key text field
        OutlinedTextField(
            value = accessKey,
            onValueChange = {
                accessKey = it
                isKeyEmpty = accessKey.isBlank() || accessKey.isEmpty()
            },
            label = {Text("Clinician Key")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isKeyEmpty,
            placeholder = { Text("Enter your clinician key") }
        )

        // validate error
        // error msg for access key
        if (isKeyEmpty) {
            Text(
                text = "Please enter valid access key.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // login button
        // validate user input match with pre-defined key
        Button(
            onClick = {
                // validate input key match with pre-defined key
                isValidKey = accessKey == key_admin_view

                if (isValidKey) {
                    // navigate to clinician dashboard screen
                    navController.navigate("ClinicianView")
                }
                else {
                    // not match, show err msg
                    errorMsg = "Invalid access key. Please try again."
                    Toast.makeText(context, "Invalid access key. Please try again.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            // icon
            Image(
                painter = painterResource(R.drawable.icons8_enter_50),
                contentDescription = "login icon",
                modifier = Modifier.offset(x = (-10).dp).size(30.dp)
            )

            // text
            Text(
                text = "Clinician Login",
                fontSize = 18.sp
            )
        }

        // err msg when not match
        if (!isValidKey || errorMsg.isNotEmpty()) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }
    }
}


