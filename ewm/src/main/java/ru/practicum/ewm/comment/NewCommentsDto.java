package ru.practicum.ewm.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class NewCommentsDto {
    @NotBlank
    private String text;
}