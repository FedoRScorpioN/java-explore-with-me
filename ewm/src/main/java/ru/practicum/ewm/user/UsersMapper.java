package ru.practicum.ewm.user;

import ru.practicum.ewm.comment.CommentsFullDto;
import ru.practicum.ewm.event.EventsFullDto;
import ru.practicum.ewm.event.EventsShortDto;

import java.util.Collection;
import java.util.stream.Collectors;

public class UsersMapper {
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
        EventsFullDto.CommentInnerDto.UserDto userDto = new EventsFullDto.CommentInnerDto.UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static CommentsFullDto.UsersDto toCommentFullUserInnerDto(Users user) {
        CommentsFullDto.UsersDto usersDto = new CommentsFullDto.UsersDto();
        usersDto.setId(user.getId());
        usersDto.setName(user.getName());
        usersDto.setEmail(user.getEmail());
        return usersDto;
    }
}