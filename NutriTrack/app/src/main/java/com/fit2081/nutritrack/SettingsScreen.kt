package com.fit2081.nutritrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.utilities.StateManager
import com.fit2081.nutritrack.data.patient.Patient
import com.fit2081.nutritrack.data.patient.PatientViewModel
import kotlinx.coroutines.runBlocking

/**
 * Composable function representing Settings screen
 *
 * @param innerPadding provides necessary padding values
 * @param patientViewModel view model
 * @param navController instance of NavHostController
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun Settings(
    innerPadding: PaddingValues,
    patientViewModel: PatientViewModel,
    navController: NavHostController,
) {

    val context = LocalContext.current
    val activity = (context as? Activity)

    // get current user from database
    val currUserID: Int? = AuthManager.getCurrentUserId()?.toInt()

    // current user id x null
    if (currUserID != null) {

        val currentUser: Patient = retrieveUserData(patientViewModel, currUserID)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(15.dp),
        ) {
            // settings title
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 18.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            // account (render user details)
            Text(
                text = "ACCOUNT",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // username
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // icon
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "user icon",
                    modifier = Modifier.size(30.dp)
                )

                // username
                Text(
                    text = currentUser.username,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(modifier = Modifier.heightIn(12.dp))

            // phone num
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // icon
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = "phone num",
                    modifier = Modifier.size(30.dp)
                )

                // phone num
                Text(
                    text = currentUser.phoneNumber,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(modifier = Modifier.heightIn(12.dp))

            // user id
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // icon
                Image(
                    painter = painterResource(R.drawable.icons8_security_pass_50),
                    contentDescription = "user id",
                    modifier = Modifier.size(32.dp)
                )

                // user id
                Text(
                    text = currentUser.userID.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            // hz divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 15.dp),
                thickness = 2.dp
            )

            // other settings title
            Text(
                text = "OTHER SETTINGS",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // logout screen
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    true, "insight", null,
                    {
                        // Navigate to LoginActivity and clear history
                        val intent = Intent(context, LoginScreen::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        context.startActivity(intent)

                        // finish the app (removed from the back stack)
                        activity?.finish()

                        // logout clear user
                        AuthManager.logout()
                        println(AuthManager.getCurrentUserId())

                        // clear shared pref
                        // manage user login status
                        val stateManager = StateManager(context)
                        stateManager.logout()
                    })
                    .fillMaxWidth()
            ) {
                // icon
                Image(
                    painter = painterResource(R.drawable.icons8_logout_48),
                    contentDescription = "logout",
                    modifier = Modifier.size(28.dp)
                )

                // button
                Text(
                    text = "Logout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.weight(1f))

                // navigate button
                IconButton(
                    onClick = {
                        // Navigate to LoginActivity and clear history
                        val intent = Intent(context, LoginScreen::class.java)
                            .apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                    Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        context.startActivity(intent)

                        // finish the app (removed from the back stack)
                        activity?.finish()

                        // logout clear user
                        AuthManager.logout()
                        println(AuthManager.getCurrentUserId())

                        // clear shared pref
                        // manage user login status
                        val stateManager = StateManager(context)
                        stateManager.logout()
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "nav right",
                    )
                }
            }

            Spacer(modifier = Modifier.heightIn(12.dp))

            // clinician login
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    true, "insight", null,
                    { navController.navigate("Clinician") })
                    .fillMaxWidth()
            ) {
                // icon
                Image(
                    painter = painterResource(R.drawable.icons8_medical_doctor_32),
                    contentDescription = "logout",
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = "Clinician Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.weight(1f))

                // navigate button
                IconButton(
                    onClick = {
                        // nav to clinician screen
                        navController.navigate("Clinician")
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "nav right",
                    )
                }
            }
        }
    }
}

/**
 * Function retrieving the user data using view model
 *
 * @param patientViewModel view model to access user data
 * @param userID the current user id
 *
 * @return a [Patient] instance
 */
// retrieve user data using view model
fun retrieveUserData(patientViewModel: PatientViewModel, userID: Int?): Patient {

    // get current user data
    val currUser: Patient

    runBlocking {
        currUser = patientViewModel.getPatientByID(userID)
    }

    return currUser
}
