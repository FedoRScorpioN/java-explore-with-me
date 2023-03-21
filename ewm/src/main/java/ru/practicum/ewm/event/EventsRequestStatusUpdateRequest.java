package ru.practicum.ewm.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.request.ParticipationRequestsStatusUpdate;

import java.util.Collection;

@Setter
@Getter
public class EventsRequestStatusUpdateRequest {
    private Collection<Long> requestIds;
    private ParticipationRequestsStatusUpdate status;
}