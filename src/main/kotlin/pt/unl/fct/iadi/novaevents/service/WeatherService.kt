package pt.unl.fct.iadi.novaevents.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.client.WeatherClient

@Service
class WeatherService(
    private val weatherClient: WeatherClient,
    @Value("\${weather.api.key}") private val apiKey: String
) {

    private val log = LoggerFactory.getLogger(WeatherService::class.java)

    /**
     * Devolve:
     *   true  -> está a chover na localização
     *   false -> não está a chover
     *   null  -> não foi possível obter o estado do tempo
     */
    fun isRaining(location: String): Boolean? {
        if (location.isBlank()) return null
        return try {
            val response = weatherClient.getWeather(location, apiKey)
            val main = response.weather.firstOrNull()?.main ?: return null
            main.equals("Rain", ignoreCase = true) ||
                main.equals("Drizzle", ignoreCase = true) ||
                main.equals("Thunderstorm", ignoreCase = true)
        } catch (e: Exception) {
            log.warn("Weather API call failed for location='{}': {}", location, e.message)
            null
        }
    }
}