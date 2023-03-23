package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;
import ru.practicum.ewm.request.ParticipationRequestsDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventsController {
    private final EventsService eventsService;

    @GetMapping
    public Collection<EventsFullDto> findEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.findEvents(userId, from, size);
    }

    @GetMapping("/{eventsId}")
    public EventsFullDto findEvent(@PathVariable Long eventsId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.findEvent(eventsId);
    }

    @GetMapping("/{eventsId}/requests")
    public Collection<ParticipationRequestsDto> findEventRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventsId,
                                                                  HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.findEventRequests(userId, eventsId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventsFullDto createEvent(@PathVariable Long userId,
                                     @RequestBody @Valid NewEventsDto eventDto,
                                     HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.addEvent(userId, eventDto);
    }

    @PatchMapping("/{eventsId}")
    public EventsFullDto patchEvent(@PathVariable Long userId,
                                    @PathVariable Long eventsId,
                                    @RequestBody UpdateEventsUserRequest updateEventsUserRequest,
                                    HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.patchEventByInitiator(userId, eventsId, updateEventsUserRequest);
    }

    @PatchMapping("/{eventsId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventsRequestStatusUpdateResult changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventsId,
            @RequestBody EventsRequestStatusUpdateRequest statusUpdateRequest,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.changeRequestStatus(userId, eventsId, statusUpdateRequest);
    }
}