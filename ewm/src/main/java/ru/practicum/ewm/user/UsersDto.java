package ru.practicum.ewm.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsersDto {
    private Long id;
    private String name;
    private String email;
}