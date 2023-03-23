package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Slf4j
public class AdminUsersController {
    private final UsersService usersService;

    @GetMapping
    public Collection<UsersDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return usersService.findUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsersDto createUser(@RequestBody @Valid NewUsersRequest userDto, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return usersService.addUser(userDto);
    }

    @DeleteMapping("/{usersId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long usersId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        usersService.deleteUser(usersId);
    }
}