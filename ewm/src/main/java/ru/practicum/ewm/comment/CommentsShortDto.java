package ru.practicum.ewm.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentsShortDto {
    private Long id;
    private String text;
    private UsersDto author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @Setter
    @Getter
    public static class UsersDto {
        private Long id;
        private String name;
        private String email;
    }
}