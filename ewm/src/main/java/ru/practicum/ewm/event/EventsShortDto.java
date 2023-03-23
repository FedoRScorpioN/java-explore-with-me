package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class EventsShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    @Setter
    @Getter
    public static class CategoryDto {
        private Long id;

        private String name;
    }

    @Setter
    @Getter
    public static class UserShortDto {
        private Long id;
        private String name;
    }
}