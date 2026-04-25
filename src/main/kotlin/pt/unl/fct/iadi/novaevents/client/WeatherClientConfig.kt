package pt.unl.fct.iadi.novaevents.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class WeatherClientConfig {

    @Bean
    fun weatherClient(
        builder: RestClient.Builder,
        @Value("\${weather.api.url}") baseUrl: String
    ): WeatherClient {
        val restClient = builder
            .baseUrl(baseUrl)
            .build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(WeatherClient::class.java)
    }
}