package ru.practicum.stats.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    private EndpointHitDto endpointHitDto;
    private ViewStatsDto viewStatsDto;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();

        endpointHitDto = EndpointHitDto.builder()
                .app("test-app")
                .uri("/test/1")
                .ip("127.0.0.1")
                .timestamp(testTime)
                .build();

        viewStatsDto = new ViewStatsDto("test-app", "/test/1", 10L);
    }

    @Test
    @DisplayName("saveHit: Should save endpoint hit successfully")
    void saveHit_ShouldSaveSuccessfully() {
        // Arrange
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setId(1L);
        endpointHit.setApp("test-app");
        endpointHit.setUri("/test/1");
        endpointHit.setIp("127.0.0.1");
        endpointHit.setTimestamp(testTime);

        when(statsRepository.save(any(EndpointHit.class))).thenReturn(endpointHit);

        // Act
        statsService.saveHit(endpointHitDto);

        // Assert
        verify(statsRepository, times(1)).save(any(EndpointHit.class));
    }

    @Test
    @DisplayName("getViewStats: Should return stats with unique ips")
    void getViewStats_ShouldReturnStatsWithUniqueIps() {
        // Arrange
        when(statsRepository.getUniqueViewStatsByUris(any(), any(), any(String[].class)))
                .thenReturn(List.of(viewStatsDto));

        // Act
        List<ViewStatsDto> result = statsService.getViewStats(
                testTime.minusDays(1),
                testTime.plusDays(1),
                new String[]{"/test/1"},
                true
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("/test/1", result.get(0).getUri());
        assertEquals(10L, result.get(0).getHits());
    }

    @Test
    @DisplayName("getViewStats: Should return stats without unique ips")
    void getViewStats_ShouldReturnStatsWithoutUniqueIps() {
        // Arrange
        when(statsRepository.getViewStatsByUris(any(), any(), any(String[].class)))
                .thenReturn(List.of(viewStatsDto));

        // Act
        List<ViewStatsDto> result = statsService.getViewStats(
                testTime.minusDays(1),
                testTime.plusDays(1),
                new String[]{"/test/1"},
                false
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("/test/1", result.get(0).getUri());
    }

    @Test
    @DisplayName("getViewStats: Should return all stats when uris is null")
    void getViewStats_ShouldReturnAllStats_WhenUrisNull() {
        // Arrange
        when(statsRepository.getUniqueViewStats(any(), any()))
                .thenReturn(List.of(viewStatsDto));

        // Act
        List<ViewStatsDto> result = statsService.getViewStats(
                testTime.minusDays(1),
                testTime.plusDays(1),
                null,
                true
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getViewStats: Should return all stats when uris is empty")
    void getViewStats_ShouldReturnAllStats_WhenUrisEmpty() {
        // Arrange
        when(statsRepository.getViewStats(any(), any()))
                .thenReturn(List.of(viewStatsDto));

        // Act
        List<ViewStatsDto> result = statsService.getViewStats(
                testTime.minusDays(1),
                testTime.plusDays(1),
                new String[]{},
                false
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
