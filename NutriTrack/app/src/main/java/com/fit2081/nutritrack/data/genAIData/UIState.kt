package com.fit2081.nutritrack.data.genAIData


/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 3b: Coding (ADVANCED - GenAI): https://edstem.org/au/courses/20813/lessons/80448/slides/544886
     **/

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    object Loading : UiState

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : UiState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState

    /**
     * Structured data patterns have been generated
     */
    data class SuccessBody(val outputPattern: List<AIResponseBody>): UiState
}