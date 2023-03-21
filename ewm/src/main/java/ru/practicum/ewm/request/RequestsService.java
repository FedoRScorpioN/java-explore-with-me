package ru.practicum.ewm.request;

import java.util.Collection;

public interface RequestsService {
    ParticipationRequestsDto addRequest(Long userId, Long eventId);

    Collection<ParticipationRequestsDto> findRequests(Long userId);

    ParticipationRequestsDto cancelRequest(Long userId, Long requestId);
}