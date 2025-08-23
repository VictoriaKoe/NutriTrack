package com.fit2081.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.utilities.AuthManager
import com.fit2081.nutritrack.data.utilities.StateManager
import com.fit2081.nutritrack.data.patient.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme

class WelcomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ViewModel instance that handles for
            val patientViewModel: PatientViewModel =
                ViewModelProvider(
                    this,
                    PatientViewModel.PatientViewModelFactory(this@WelcomeScreen)
                )[PatientViewModel::class.java]

            NutriTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
                    Welcome(
                        modifier = Modifier.padding(innerPadding),
                        patientViewModel
                    )
                }
            }
        }
    }
}

// Gemini API KEY: AIzaSyBEiQxF4bLJzMD-hVAle1q3537cL0IYP7k
/**
 * Composable function representing welcome screen.
 * It is a entry point of NutriTrack app with logo and disclaimer text.
 *
 * @param modifier decorate or add behavior to Compose UI elements
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
@Composable
fun Welcome(modifier: Modifier = Modifier, patientViewModel: PatientViewModel) {

    val context = LocalContext.current
    val stateManager = StateManager(context)

    val text: String = "This app provides general health and nutrition information for " +
            "educational purposes only. It is not intended as medical advice, " +
            "diagnosis, or treatment. Always consult a qualified healthcare " +
            "professional before making any changes to your diet, exercise, or " +
            "health regimen.\n" +
            "Use this app at your own risk.\n" +
            "If you’d like to an Accredited Practicing Dietitian (APD), please " +
            "visit the Monash Nutrition/Dietetics Clinic (discounted rates for " +
            "students):\n"

    /**
     * References:
     * The following code lines below were taken from the following ed lesson with some modifications.
     *  Lab 2, Section 1.0 - Develop Login Screen: https://edstem.org/au/courses/20813/lessons/77147/slides/518841
     **/
    Surface (
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // add app name text
            Text(
                text = "NutriTrack",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )

            // add image after text
            Image(
                painter = painterResource(id = R.drawable.nutritrack),
                contentDescription = "NutriTrack Logo",
                modifier = Modifier.size(200.dp)
            )

            // add space btw logo and disclaimer
            Spacer(modifier = Modifier.height(24.dp))

            /**
             * References:
             * The following code from lines 128 to 144 references the links below to create hyperlink text.
             *  https://developer.android.com/reference/kotlin/androidx/compose/ui/text/AnnotatedString.Builder
             *  https://developer.android.com/develop/ui/compose/text/user-interactions
             *  https://developer.android.com/reference/kotlin/androidx/compose/ui/text/LinkAnnotation
             **/
            // disclaimer text & link
            Text(
                text = buildAnnotatedString {
                    append(text)
                    withLink(
                        // LinkAnnotation - annotation that represents a clickable part of the text.
                        // .URL - contains a url string.
                        LinkAnnotation.Url(
                            "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition ",
                            // styling, append new text, hyperlink, with pre-defined colour
                            TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
                        )
                    ) {
                        // Attach the given string annotation to any appended text
                        append("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition")
                    }
                },
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )

            // space
            Spacer(modifier = Modifier.height(30.dp))

            // login button and navigate to login screen
            Button(
                 onClick = {
                     // check user is login, if yes, check questionnaire done, both yes -> home
                     // questionnaire x, -> food intake
                     // get from shared preferences
                     if (stateManager.isUserLoggedIn()) {
                         println("Hello :${stateManager.isUserLoggedIn()}")
                         if (stateManager.hasCompletedQuestionnaire()) {
                             AuthManager.login(userId = stateManager.getUserId())
                             context.startActivity(Intent(context, HomeScreen::class.java))
                         }
                         else {
                             AuthManager.login(userId = stateManager.getUserId())
                             context.startActivity(Intent(context, FoodIntakeQuestionnaireScreen::class.java))
                         }
                     } else {
                         // navigate to login screen using Intent and startActivity
                         context.startActivity(Intent(context, LoginScreen::class.java))
                     }
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Login"
                )
            }

            // space
            Spacer(modifier = Modifier.height(40.dp))

            // designer text
            Text(
                text = "Designed with ❤\uFE0F \nby Rui En Koe(32839677)",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}



