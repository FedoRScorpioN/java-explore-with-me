package ru.practicum.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@Getter
public final class StatsClient extends BaseClient {
    private final String applicationUrl;
    private final String statsServiceUrl;
    private final ObjectMapper jsonMapper;
    private final HttpClient httpClient;

    @Autowired
    public StatsClient(@Value("http://localhost:9090")
                       String applicationUrl, String statsServiceUrl, ObjectMapper jsonMapper, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(statsServiceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
        this.applicationUrl = applicationUrl;
        this.statsServiceUrl = statsServiceUrl;
        this.jsonMapper = jsonMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public void hit(HttpServletRequest userRequest) {
        EndpointHitDto hit = EndpointHitDto.builder()
                .app(URI.create(applicationUrl).getHost())
                .ip(userRequest.getRemoteAddr())
                .uri(userRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        post("/hit", hit);
    }
}