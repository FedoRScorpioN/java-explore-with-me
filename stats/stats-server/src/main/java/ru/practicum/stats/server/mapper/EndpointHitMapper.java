package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

public final class EndpointHitMapper {
    private static final EndpointHitMapper INSTANCE = new EndpointHitMapper();

    private EndpointHitMapper() {
    }

    public static EndpointHitMapper getInstance() {
        return INSTANCE;
    }

    public EndpointHit toEndpointHit(EndpointHitDto hitDto) {
        return new EndpointHit(hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp());
    }
}