package ru.practicum.ewm.category;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class NewCategoriesDto {
    @NotBlank(message = "name is required")
    @Size(min = 1, max = 50, message = "name size must be between 1 and 50")
    private String name;
}
