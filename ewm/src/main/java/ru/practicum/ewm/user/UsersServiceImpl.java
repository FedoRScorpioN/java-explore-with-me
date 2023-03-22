package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    @Override
    public UsersDto addUser(final NewUsersRequest userDto) {
        Users users = UsersMapper.getInstance().toUser(userDto);
        UsersDto newUser;
        try {
            Users savedUser = usersRepository.save(users);
            if (savedUser == null) {
                throw new NullPointerException("Значение не может быть равно NULL");
            }
            newUser = UsersMapper.toUserDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Ошибка при создании пользователя. Повторите позднее.");
        }
        return newUser;
    }

    @Override
    public Collection<UsersDto> findUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        if (ids != null && ids.size() > 0) {
            return UsersMapper.getInstance().toUserDto(usersRepository.findByIdIn(ids, pageable));
        }
        return UsersMapper.getInstance().toUserDto(usersRepository.findAll(pageable).getContent());
    }

    @Override
    public void deleteUser(Long id) {
        usersRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        usersRepository.deleteById(id);
    }
}