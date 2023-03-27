package ru.practicum.ewm.user;

import ru.practicum.ewm.comment.CommentsFullDto;
import ru.practicum.ewm.event.EventsFullDto;
import ru.practicum.ewm.event.EventsShortDto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;

public final class UsersMapper {
    private static final UsersMapper INSTANCE = new UsersMapper();

    private UsersMapper() {
    }

    public static UsersMapper getInstance() {
        return INSTANCE;
    }

    public Users toUser(NewUsersRequest userRequest) {
        Users users = new Users();
        users.setName(userRequest.getName());
        users.setEmail(userRequest.getEmail());
        return users;
    }

    public static UsersDto toUserDto(Users users) {
        UsersDto usersDto = new UsersDto();
        usersDto.setId(users.getId());
        usersDto.setName(users.getName());
        usersDto.setEmail(users.getEmail());
        return usersDto;
    }

    public EventsFullDto.UserShortDto toUserShortInnerDto(Users users) {
        EventsFullDto.UserShortDto userDto = new EventsFullDto.UserShortDto();
        userDto.setId(users.getId());
        userDto.setName(users.getName());
        return userDto;
    }

    public EventsShortDto.UserShortDto toUserEventShortInnerDto(Users users) {
        EventsShortDto.UserShortDto userDto = new EventsShortDto.UserShortDto();
        userDto.setId(users.getId());
        userDto.setName(users.getName());
        return userDto;
    }

    public Collection<UsersDto> toUserDto(Collection<Users> users) {
        return users.stream()
                .map(UsersMapper::toUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public static EventsFullDto.CommentInnerDto.UserDto toCommentShortUserInnerDto(Users user) {
        return toInnerDto(user, EventsFullDto.CommentInnerDto.UserDto.class);
    }

    public static CommentsFullDto.UsersDto toCommentFullUserInnerDto(Users user) {
        return toInnerDto(user, CommentsFullDto.UsersDto.class);
    }

    public static <T> T toInnerDto(Users users, Class<T> innerDtoClass) {
        try {
            T innerDto = innerDtoClass.newInstance();
            Method setIdMethod = innerDtoClass.getMethod("setId", Long.class);
            setIdMethod.invoke(innerDto, users.getId());
            Method setNameMethod = innerDtoClass.getMethod("setName", String.class);
            setNameMethod.invoke(innerDto, users.getName());
            if (innerDtoClass.equals(EventsFullDto.UserShortDto.class)) {
                Method setEmailMethod = innerDtoClass.getMethod("setEmail", String.class);
                setEmailMethod.invoke(innerDto, users.getEmail());
            }
            return innerDto;
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException exception) {
            throw new IllegalArgumentException("Недопустимый класс innerDto", exception);
        }
    }
}