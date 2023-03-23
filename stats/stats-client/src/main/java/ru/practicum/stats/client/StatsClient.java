package ru.practicum.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
public final class StatsClient extends BaseClient {

    private final String applicationUrl;
    private final String statsServiceUrl;
    private final ObjectMapper jsonMapper;
    private final HttpClient httpClient;

    @Autowired
    public StatsClient(@Value("http://localhost:9090") String applicationUrl,
                       @Value("http://stats-server:9090") String statsServiceUrl,
                       ObjectMapper jsonMapper, RestTemplateBuilder builder) {
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
                .app(applicationUrl)
                .ip(userRequest.getRemoteAddr())
                .uri(userRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        post("/hit", hit);
    }

    public List<ViewStatsDto> getStats(List<String> uris) {
        if (uris == null || uris.size() == 0) return new ArrayList<>();
        log.info("URIS:");
        log.info(uris.toString());
        String baseUrl = statsServiceUrl + "/stats";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("uris", StringUtils.join(uris, ','))
                .queryParam("start", "2000-01-01 00:00:00")
                .queryParam("end", LocalDateTime.now().format(formatter)
                ).build();
        ViewStatsDto[] stats = rest.getForObject(uri.toString(), ViewStatsDto[].class);
        return Arrays.stream(stats).collect(Collectors.toUnmodifiableList());
    }
}