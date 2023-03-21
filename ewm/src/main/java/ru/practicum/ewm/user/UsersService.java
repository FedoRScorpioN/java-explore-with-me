package ru.practicum.ewm.user;

import java.util.Collection;
import java.util.List;

public interface UsersService {
    UsersDto addUser(final NewUsersRequest user);

    Collection<UsersDto> findUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);
}