package pt.unl.fct.iadi.novaevents.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pt.unl.fct.iadi.novaevents.client.WeatherClient
import pt.unl.fct.iadi.novaevents.client.dto.WeatherEntry
import pt.unl.fct.iadi.novaevents.client.dto.WeatherResponse
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity

@SpringBootTest
@ActiveProfiles("test")
class TestWeatherController {

    @TestConfiguration
    class FakeClientConfig {
        @Bean @Primary
        fun fakeWeatherClient(): WeatherClient = object : WeatherClient {
            override fun getWeather(location: String, apiKey: String, units: String): WeatherResponse {
                return when {
                    location.equals("rainy", true) -> WeatherResponse(listOf(WeatherEntry("Rain")))
                    location.equals("sunny", true) -> WeatherResponse(listOf(WeatherEntry("Clear")))
                    else -> throw RuntimeException("not found")
                }
            }
        }
    }

    @Autowired lateinit var ctx: WebApplicationContext
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity()).build()
    }

    @Test
    @WithMockUser(username = "alice")
    fun `json raining`() {
        mockMvc.perform(get("/api/weather?location=rainy").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.raining").value(true))
    }

    @Test
    @WithMockUser(username = "alice")
    fun `json clear`() {
        mockMvc.perform(get("/api/weather?location=sunny").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.raining").value(false))
    }

    @Test
    @WithMockUser(username = "alice")
    fun `json unavailable`() {
        mockMvc.perform(get("/api/weather?location=unknown").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.raining").doesNotExist())
    }

    @Test
    @WithMockUser(username = "alice")
    fun `html fragment raining`() {
        mockMvc.perform(get("/api/weather?location=rainy").accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Raining")))
    }

    @Test
    fun `unauthenticated returns 401`() {
        mockMvc.perform(get("/api/weather?location=rainy").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized)
    }
    @Test
    @WithMockUser(username = "alice")
    fun `html fragment clear`() {
        mockMvc.perform(get("/api/weather?location=sunny").accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Clear")))
    }

    @Test
    @WithMockUser(username = "alice")
    fun `html fragment unavailable`() {
        mockMvc.perform(get("/api/weather?location=unknown").accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Weather data unavailable")))
    }
}