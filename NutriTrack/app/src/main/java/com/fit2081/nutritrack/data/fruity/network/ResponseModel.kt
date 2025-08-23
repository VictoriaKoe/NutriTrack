package com.fit2081.nutritrack.data.fruity.network

/**
 * ResponseModel is data class representing the response from the Fruity API resource
 * It represents the data from any fruit of your choosing
 *
 * Acknowledgement:
 * Full acknowledgment details can be found below.
 */
data class ResponseModel(

    /**
     * References:
     * The following code lines were taken from the following ed lesson with some modifications.
     *  Lab 7, Part 2b: Coding (Currency Exchange Rate): https://edstem.org/au/courses/20813/lessons/80448/slides/544885
     **/

    // represent res from Fruity API
    // contains basic fruits info:
    // name, id, family,  order, genus, [calories, fat, sugar, carbohydrates, protein]
    var name: String,
    var id: Int,
    var family: String,
    var order: String,
    var genus: String,
    var nutritions: Map<String, Double>
)
