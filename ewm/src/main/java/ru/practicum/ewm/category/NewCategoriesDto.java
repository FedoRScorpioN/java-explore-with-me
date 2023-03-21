package ru.practicum.ewm.category;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class NewCategoriesDto {
    @NotBlank
    private String name;
}