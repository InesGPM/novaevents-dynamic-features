package pt.unl.fct.iadi.novaevents.client.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherResponse(
    val weather: List<WeatherEntry> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherEntry(
    val main: String? = null
)