package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.dto.GeoCodingResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeoCodingApiClient {

    private final String apiKey;
    private final WebClient webClient;

    public GeoCodingApiClient(WebClient webClient, @Value("${API_KEY}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    /**
     * Call external API to get geo-coordinates for a city based on its name.
     *
     * @param city Name of the city to get geo-coordinates of.
     * @return GeoCodingResponseDTO array which is expected to have one element containing latitude and longitude data.
     */

    public GeoCodingResponseDTO[] getGeoCoordinatesForCity(String city) {

        String url = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/geo/1.0/direct")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .build()
                .toUriString();

        return webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GeoCodingResponseDTO[].class)
                .block();
    }
}
