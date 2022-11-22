package com.notbadcode.explorewithme.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class StatsClient {
    private final WebClient webClient;
    private final String app;
    private final String statsUrl;
    private final String hitUri;
    private final String statsUri;
    private final String eventUri;

    @Autowired
    public StatsClient(
            WebClient.Builder webClientBuilder,
            @Value("${spring.application.name}") String app,
            @Value("${ewm-config.stats.server.url}") String statsUrl,
            @Value("${ewm-config.stats.client.hit-uri}") String hitUri,
            @Value("${ewm-config.stats.client.stats-uri}") String statsUri,
            @Value("${ewm-config.stats.client.events-uri}") String eventUri
    ) {
        this.webClient = webClientBuilder.baseUrl(statsUrl).build();
        this.app = app;
        this.statsUrl = statsUrl;
        this.hitUri = hitUri;
        this.statsUri = statsUri;
        this.eventUri = eventUri;
    }

    public void sendHit(String uriForStat, String ip) throws WebClientRequestException {
        EndpointHitDto hit = EndpointHitDto.builder()
                .uri(uriForStat)
                .app(app)
                .ip(ip)
                .build();
        webClient.post()
                .uri(hitUri)
                .header("Content-Type", "application/json")
                .body(Mono.just(hit), EndpointHitDto.class)
                .retrieve()
                .toBodilessEntity()
                .block();
        log.info("Send to {} endpoint hit: {}", statsUrl, hit);
    }

    public Map<Long, Long> getStatsOfEvents(List<Long> eventIds) throws WebClientRequestException {
        StringBuilder stringBuilder = new StringBuilder(statsUri);
        // дата обязательна по ТЗ, а статистика запросов событий мне нужна за все время
        stringBuilder.append("?start=")
                .append(encode("1970-01-01 00:00:00"))
                .append("&end=")
                .append(encode("2070-12-31 23:59:59"));
        eventIds.forEach(id -> stringBuilder.append("&uris=")
                .append(eventUri)
                .append("/")
                .append(id));
        List<ViewStats> stats = webClient.get()
                .uri(stringBuilder.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStats>>() {})
                .block();
        Map<Long, Long> idsCount = Optional.ofNullable(stats).orElse(new ArrayList<>()).stream()
                        .collect(Collectors.toMap(s -> Long.getLong(s.getUri().split("/")[1]),
                                ViewStats::getHits));
        log.info("Get stats for {} events", idsCount.size());
        return idsCount;
    }

    private String encode(String text) {
        return URLEncoder.encode(text, UTF_8);
    }
}
