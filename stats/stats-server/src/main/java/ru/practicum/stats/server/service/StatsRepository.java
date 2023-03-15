package ru.practicum.stats.server.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.model.EndpointHit;

import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(h.id)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true")})
    List<ViewStatsDto> getViewStats(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true")})
    List<ViewStatsDto> getUniqueViewStats(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(h.id)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "  AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true")})
    List<ViewStatsDto> getViewStatsByUris(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") String[] uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "  AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true")})
    List<ViewStatsDto> getUniqueViewStatsByUris(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end,
                                                @Param("uris") String[] uris);
}