package pt.unl.fct.iadi.novaevents.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.unl.fct.iadi.novaevents.client.WeatherClient
import pt.unl.fct.iadi.novaevents.client.dto.WeatherEntry
import pt.unl.fct.iadi.novaevents.client.dto.WeatherResponse

class TestWeatherService {

    private fun fakeClient(main: String?): WeatherClient = object : WeatherClient {
        override fun getWeather(location: String, apiKey: String, units: String): WeatherResponse {
            return if (main == null) WeatherResponse(emptyList())
            else WeatherResponse(listOf(WeatherEntry(main = main)))
        }
    }

    private fun failingClient(): WeatherClient = object : WeatherClient {
        override fun getWeather(location: String, apiKey: String, units: String): WeatherResponse {
            throw RuntimeException("API down")
        }
    }

    @Test
    fun `returns true when raining`() {
        val service = WeatherService(fakeClient("Rain"), "key")
        assertEquals(true, service.isRaining("Lisbon"))
    }

    @Test
    fun `returns false when clear`() {
        val service = WeatherService(fakeClient("Clear"), "key")
        assertEquals(false, service.isRaining("Lisbon"))
    }

    @Test
    fun `returns null when API fails`() {
        val service = WeatherService(failingClient(), "key")
        assertNull(service.isRaining("Lisbon"))
    }

    @Test
    fun `returns null when location is blank`() {
        val service = WeatherService(fakeClient("Rain"), "key")
        assertNull(service.isRaining(""))
    }

    @Test
    fun `returns null when no weather data`() {
        val service = WeatherService(fakeClient(null), "key")
        assertNull(service.isRaining("Lisbon"))
    }
}