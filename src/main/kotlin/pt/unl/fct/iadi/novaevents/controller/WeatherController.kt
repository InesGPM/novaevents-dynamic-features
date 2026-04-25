package pt.unl.fct.iadi.novaevents.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.ui.Model
import pt.unl.fct.iadi.novaevents.service.WeatherService

@Controller
class WeatherController(private val weatherService: WeatherService) {

    // JSON: chamado com Accept: application/json (botão "Check Weather JS")
    @GetMapping("/api/weather", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun weatherJson(@RequestParam location: String): Map<String, Boolean?> {
        val raining = weatherService.isRaining(location)
        return mapOf("raining" to raining)
    }

    // HTML fragment: chamado com Accept: text/html (botão "Check Weather HTMX")
    @GetMapping("/api/weather", produces = [MediaType.TEXT_HTML_VALUE])
    fun weatherHtml(@RequestParam location: String, model: Model): String {
        val raining = weatherService.isRaining(location)
        model.addAttribute("raining", raining)
        return "fragments/weather :: badge"
    }
}