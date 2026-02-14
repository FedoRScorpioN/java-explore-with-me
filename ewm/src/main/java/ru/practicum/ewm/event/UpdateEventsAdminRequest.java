package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Setter
@Getter
public class UpdateEventsAdminRequest {
    @Size(min = 20, max = 2000, message = "annotation size must be between 20 and 2000")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "description size must be between 20 and 7000")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @Min(value = 0, message = "participantLimit must be >= 0")
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAdminAction stateAction;
    @Size(min = 3, max = 120, message = "title size must be between 3 and 120")
    private String title;
}
