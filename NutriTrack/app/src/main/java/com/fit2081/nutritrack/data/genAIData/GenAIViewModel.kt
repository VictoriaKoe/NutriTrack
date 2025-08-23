package com.fit2081.nutritrack.data.genAIData

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.fit2081.nutritrack.BuildConfig
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * GenAIViewModel is class for handling interactions with genAI model
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
// viewmodel cls for handling interactions with gen AI model
class GenAIViewModel: ViewModel() {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 3b: Coding (ADVANCED - GenAI): https://edstem.org/au/courses/20813/lessons/80448/slides/544886
     **/

    // mutable state flow to hold curr ui state
    // initial set to 'UiState.Initial'
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)

    // public exposed immutable state flow for observing ui state
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    // instance of GenerativeModel used to generate content
    // initialised with specific model name and API Key
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GENAI_API_KEY
    )


    /**
     * Send prompt to GenAi & update UI state based on response
     *
     * @param prompt prompt to be sent to GenAI
     */
    // send prompt to GenAi & update UI state based on response
    fun sendPrompt(
        prompt: String
    ) {
        // set ui state to Loading b4 making API call
        _uiState.value = UiState.Loading

        // launch coroutine in IO dispatcher to perform API call
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // generate content using genai
                val response = generativeModel.generateContent(
                    content {
                        text(prompt) // set input txt for model
                    }
                )
                // update ui state with generated content if successful
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                // update ui state with err msg if exception occurs
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    /** AI Acknowledgment:
     * I acknowledge the use of Claude (https://claude.ai/) to aid me in
     * implementing sending prompt and return the result in JSON format in the following code lines below.
     */

    // Gson instance for JSON parsing
    private val gson = Gson()

    /**
     *  Send prompt in specified format to be return for data analysis
     *
     *  @param prompt prompt to be sent to genAI
     */
    // send prompt specifically with data analysis
    fun sendPromptForPatterns(prompt: String) {
        // set ui state to Loading b4 making API call
        _uiState.value = UiState.Loading

        // Enhanced prompt for structured response
        val structuredPrompt = """
            $prompt
            
            Please format your response as JSON with the following structure:
            [
              {
                "patternName": "Pattern Name",
                "description": "Brief description of the pattern"
              }
            ]

            Ensure the response is valid JSON format only, no additional text.
        """.trimIndent()

        // launch coroutine in IO dispatcher to perform API call
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // generate content using genai
                val response = generativeModel.generateContent(
                    content {
                        text(structuredPrompt)
                    }
                )

                // update ui state with structured patterns if successful
                response.text?.let { outputContent ->
                    val patterns = parseStructuredResponse(outputContent)
                    _uiState.value = UiState.SuccessBody(patterns)
                }
            } catch (e: Exception) {
                Log.d("GenAIViewModel", "Error generating patterns", e)
                // update ui state with err msg if exception occurs
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Parse JSON response into structured data patterns
     *
     *  @param response genAI response
     *
     *  @return list of [AIResponseBody]
     */
    // Parse JSON response into structured data patterns -> object
    private fun parseStructuredResponse(jsonResponse: String): List<AIResponseBody> {
        return try {
            // Clean the response - remove any markdown formatting
            val cleanedResponse = jsonResponse
                .replace("```json", "")
                .replace("```", "")
                .trim()

            Log.d("GenAIViewModel", "Attempting to parse: $cleanedResponse")

            val type = object : TypeToken<List<AIResponseBody>>() {}.type
            val patterns: List<AIResponseBody> = gson.fromJson(cleanedResponse, type)

            // Validate that we got valid patterns
            if (patterns.isNotEmpty()) {
                Log.d("GenAIViewModel", "Successfully parsed ${patterns.size} patterns")
                patterns
            } else {
                Log.w("GenAIViewModel", "No patterns found, falling back to manual parsing")
                parseManuallyIfNeeded(jsonResponse)
            }
        } catch (e: Exception) {
            Log.e("GenAIViewModel", "JSON parsing failed, attempting manual parsing", e)
            // Fallback: parse manually or return error patterns
            parseManuallyIfNeeded(jsonResponse)
        }
    }

    /**
     *  Fallback method if JSON parsing fails
     *
     *  @param response genAI response
     *
     *  @return list of [AIResponseBody]
     */
    // Fallback method if JSON parsing fails
    private fun parseManuallyIfNeeded(response: String): List<AIResponseBody> {
        Log.d("GenAIViewModel", "Manual parsing of response")

        val patterns = mutableListOf<AIResponseBody>()

        try {
            // Method 1: Look for numbered patterns like "1.", "2.", "3."
            val numberedPatterns = parseNumberedPatterns(response)
            if (numberedPatterns.isNotEmpty()) {
                return numberedPatterns
            }

            // Method 2: Split by double newlines and create patterns
            val sections = response.split("\n\n").filter { it.isNotBlank() }

            sections.forEachIndexed { index, section ->
                val lines = section.split("\n").filter { it.isNotBlank() }
                if (lines.isNotEmpty()) {
                    patterns.add(
                        AIResponseBody(
                            patternName = "Pattern ${index + 1}",
                            description = lines.firstOrNull() ?: "No description available",
                        )
                    )
                }
            }

            // Method 3: If still empty, create a single pattern with the full response
            if (patterns.isEmpty()) {
                val insights = response.split("\n")
                    .filter { it.isNotBlank() }
                    .map { it.trim() }
                    .take(10)

                patterns.add(
                    AIResponseBody(
                        patternName = "Nutrition Data Analysis",
                        description = "Generated insights from your nutrition data",
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("GenAIViewModel", "Manual parsing also failed", e)
            // Last resort: return the raw response as a single pattern
            patterns.add(
                AIResponseBody(
                    patternName = "Raw Analysis Results",
                    description = "Unable to parse structured data",
                )
            )
        }

        Log.d("GenAIViewModel", "Manual parsing created ${patterns.size} patterns")
        return patterns
    }

    /**
     *   Helper method to parse numbered patterns
     *
     *  @param response genAI response
     *
     *  @return list of [AIResponseBody]
     */
    // Helper method to parse numbered patterns
    private fun parseNumberedPatterns(response: String): List<AIResponseBody> {
        val patterns = mutableListOf<AIResponseBody>()

        // Look for patterns like "1. Title" or "Pattern 1:"
        val patternRegex = """(?:(\d+)\.\s*([^\n]+)|Pattern\s*(\d+):\s*([^\n]+))""".toRegex(RegexOption.IGNORE_CASE)
        val matches = patternRegex.findAll(response).toList()

        matches.forEachIndexed { index, match ->
            val title = match.groupValues[2].ifEmpty { match.groupValues[4] }.trim()

            // Extract content between this pattern and the next
            val startIndex = match.range.last + 1
            val endIndex = if (index < matches.lastIndex) {
                matches[index + 1].range.first
            } else {
                response.length
            }

            if (startIndex < response.length) {
                val content = response.substring(startIndex, endIndex).trim()
                val insights = content.split("\n")
                    .filter { it.isNotBlank() }
                    .map { it.trim() }
                    .filter { it.length > 10 } // Filter out very short lines
                    .take(5)

                if (title.isNotEmpty()) {
                    patterns.add(
                        AIResponseBody(
                            patternName = title,
                            description = insights.firstOrNull() ?: "Analysis pattern identified",
                        )
                    )
                }
            }
        }

        return patterns
    }


}