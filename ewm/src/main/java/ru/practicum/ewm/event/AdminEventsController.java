package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventsController {
    private final EventsService eventsService;

    @GetMapping
    public Collection<EventsFullDto> findEvents(
            @RequestParam(required = false) Long[] users,
            @RequestParam(required = false) String[] states,
            @RequestParam(required = false) Long[] categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventsFullDto updateEvent(@PathVariable Long eventId,
                                     @RequestBody UpdateEventsAdminRequest updateRequest, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventsService.updateEventByAdmin(eventId, updateRequest);
    }
}