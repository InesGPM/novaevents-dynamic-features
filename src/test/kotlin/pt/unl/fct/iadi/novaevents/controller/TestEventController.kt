package pt.unl.fct.iadi.novaevents.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pt.unl.fct.iadi.novaevents.client.WeatherClient
import pt.unl.fct.iadi.novaevents.client.dto.WeatherEntry
import pt.unl.fct.iadi.novaevents.client.dto.WeatherResponse
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestEventController {

    @TestConfiguration
    class Cfg {
        @Bean @Primary
        fun fakeClient(): WeatherClient = object : WeatherClient {
            override fun getWeather(location: String, apiKey: String, units: String): WeatherResponse {
                return if (location.equals("rainy", true))
                    WeatherResponse(listOf(WeatherEntry("Rain")))
                else WeatherResponse(listOf(WeatherEntry("Clear")))
            }
        }
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var clubRepo: ClubRepository

    @Test
    @WithMockUser(username = "alice", roles = ["EDITOR"])
    fun `outdoor club requires location`() {
        val club = clubRepo.save(Club(name = "Hiking & Outdoors Club", category = ClubCategory.SPORTS))
        mockMvc.perform(post("/clubs/${club.id}/events")
            .with(csrf())
            .param("name", "Hike")
            .param("date", "2026-12-01")
            .param("typeId", "1"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Location is required for outdoor events")))
    }

    @Test
    @WithMockUser(username = "alice", roles = ["EDITOR"])
    fun `outdoor club blocks rainy event`() {
        val club = clubRepo.save(Club(name = "Hiking & Outdoors Club", category = ClubCategory.SPORTS))
        mockMvc.perform(post("/clubs/${club.id}/events")
            .with(csrf())
            .param("name", "Hike2")
            .param("date", "2026-12-01")
            .param("typeId", "1")
            .param("location", "rainy"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("currently raining")))
    }
}