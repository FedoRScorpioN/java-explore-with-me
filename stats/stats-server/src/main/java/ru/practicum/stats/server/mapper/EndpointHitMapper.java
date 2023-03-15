package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto hitDto) {
        return new EndpointHit(hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp());
    }
}