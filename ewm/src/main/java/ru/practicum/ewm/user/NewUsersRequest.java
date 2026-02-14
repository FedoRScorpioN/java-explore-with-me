package ru.practicum.ewm.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class NewUsersRequest {
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 250, message = "name size must be between 2 and 250")
    private String name;
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(min = 6, max = 254, message = "email size must be between 6 and 254")
    private String email;
}
