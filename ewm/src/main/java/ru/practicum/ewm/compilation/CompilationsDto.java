package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.EventsShortDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Setter
@Getter
public class CompilationsDto {
    private Long id;
    private Collection<EventInnerShortDto> events;
    private Boolean pinned;
    private String title;

    @Setter
    @Getter
    public static class EventInnerShortDto {
        private Long id;
        private String annotation;
        private EventsShortDto.CategoryDto category;
        private Long confirmedRequests;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime eventDate;
        private EventsShortDto.UserShortDto initiator;
        private boolean paid;
        private String title;
        private Long views;
    }
}