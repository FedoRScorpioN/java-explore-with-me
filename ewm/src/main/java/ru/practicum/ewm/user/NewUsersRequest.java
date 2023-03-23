package ru.practicum.ewm.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class NewUsersRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}