package ru.practicum.ewm.compilation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class NewCompilationsDto {
    private Long[] events;
    private Boolean pinned;

    @NotBlank
    private String title;
}