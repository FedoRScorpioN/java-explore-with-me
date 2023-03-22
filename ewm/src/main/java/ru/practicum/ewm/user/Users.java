package ru.practicum.ewm.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "USERS")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @Email(message = "Поле 'email' должно содержать корректный email-адрес.")
    @NotEmpty(message = "Поле 'email' не может быть пустым.")
    @Column(nullable = false, unique = true, length = 320)
    private String email;
}