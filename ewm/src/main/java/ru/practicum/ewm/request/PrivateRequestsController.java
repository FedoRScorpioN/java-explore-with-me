package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
public class PrivateRequestsController {
    private final RequestsService requestsService;

    @GetMapping
    public Collection<ParticipationRequestsDto> findRequests(@PathVariable Long userId,
                                                             HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return requestsService.findRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestsDto createRequest(@PathVariable Long userId,
                                                  @RequestParam Long eventId,
                                                  HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return requestsService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestsDto cancelRequest(@PathVariable Long userId,
                                                  @PathVariable Long requestId,
                                                  HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return requestsService.cancelRequest(userId, requestId);
    }
}