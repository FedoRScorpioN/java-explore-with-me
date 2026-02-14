package ru.practicum.ewm.compilation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class NewCompilationsDto {
    private Long[] events;
    private Boolean pinned;
    @NotBlank(message = "title is required")
    @Size(min = 1, max = 50, message = "title size must be between 1 and 50")
    private String title;
}
