package ru.practicum.ewm.request;

import ru.practicum.ewm.event.EventsRequestStatusUpdateResult;

import java.util.Collection;
import java.util.stream.Collectors;

public class RequestsMapper {
    private static final RequestsMapper INSTANCE = new RequestsMapper();

    private RequestsMapper() {
    }

    public static RequestsMapper getInstance() {
        return INSTANCE;
    }

    public static ParticipationRequestsDto toRequestDto(ParticipationRequests request) {
        ParticipationRequestsDto requestDto = new ParticipationRequestsDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequesterId());
        requestDto.setStatus(request.getStatus());
        return requestDto;
    }

    public EventsRequestStatusUpdateResult.ParticipationRequestDto toRequestInnerDto(ParticipationRequests request) {
        EventsRequestStatusUpdateResult.ParticipationRequestDto requestDto =
                new EventsRequestStatusUpdateResult.ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequesterId());
        requestDto.setStatus(request.getStatus());
        return requestDto;
    }

    public Collection<ParticipationRequestsDto> toRequestDto(Collection<ParticipationRequests> requests) {
        return requests.stream()
                .map(RequestsMapper::toRequestDto)
                .collect(Collectors.toUnmodifiableList());
    }
}