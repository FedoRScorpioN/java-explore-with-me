package ru.practicum.ewm.event;

import ru.practicum.ewm.request.ParticipationRequestsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface EventsService {
    Map<Long, Long> getViews(Collection<Events> events);

    EventsFullDto addEvent(Long userId, NewEventsDto eventDto);

    Collection<EventsFullDto> findEvents(Long[] users,
                                         String[] states,
                                         Long[] categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size);

    Collection<EventsFullDto> findEvents(Long userId,
                                         Integer from,
                                         Integer size);

    Collection<EventsShortDto> findEvents(String text,
                                          Boolean paid,
                                          Boolean onlyAvailable,
                                          EventsSort sort,
                                          Long[] categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Integer from,
                                          Integer size);

    EventsFullDto patchEventByInitiator(Long userId,
                                        Long eventId,
                                        UpdateEventsUserRequest updateEventsUserRequest);

    EventsFullDto updateEventByAdmin(Long eventId,
                                     UpdateEventsAdminRequest updateRequest);

    EventsRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                        Long eventId,
                                                        EventsRequestStatusUpdateRequest statusUpdateRequest);

    EventsFullDto findEvent(Long id);

    Collection<ParticipationRequestsDto> findEventRequests(Long userId,
                                                           Long eventId);
}