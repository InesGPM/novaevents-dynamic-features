package pt.unl.fct.iadi.novaevents.client

import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import pt.unl.fct.iadi.novaevents.client.dto.WeatherResponse

@HttpExchange
interface WeatherClient {

    @GetExchange("/weather")
    fun getWeather(
        @RequestParam("q") location: String,
        @RequestParam("appid") apiKey: String,
        @RequestParam("units") units: String = "metric"
    ): WeatherResponse
}