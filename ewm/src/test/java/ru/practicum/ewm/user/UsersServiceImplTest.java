package ru.practicum.ewm.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersServiceImpl usersService;

    private Users testUser;
    private NewUsersRequest newUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new Users(1L, "Test User", "test@test.com");

        newUserRequest = new NewUsersRequest();
        newUserRequest.setName("New User");
        newUserRequest.setEmail("new@test.com");
    }

    @Test
    @DisplayName("addUser: Should create user successfully")
    void addUser_ShouldCreateUserSuccessfully() {
        // Arrange
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        UsersDto result = usersService.addUser(newUserRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New User", result.getName());
        assertEquals("new@test.com", result.getEmail());
        verify(usersRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("findUsers: Should return users by ids")
    void findUsers_ShouldReturnUsersByIds() {
        // Arrange
        when(usersRepository.findByIdIn(eq(List.of(1L)), any(Pageable.class)))
                .thenReturn(List.of(testUser));

        // Act
        Collection<UsersDto> result = usersService.findUsers(List.of(1L), 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findUsers: Should return all users when ids is null")
    void findUsers_ShouldReturnAllUsers_WhenIdsNull() {
        // Arrange
        when(usersRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testUser)));

        // Act
        Collection<UsersDto> result = usersService.findUsers(null, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("deleteUser: Should delete user successfully")
    void deleteUser_ShouldDeleteSuccessfully() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(usersRepository).deleteById(1L);

        // Act
        usersService.deleteUser(1L);

        // Assert
        verify(usersRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: Should throw NotFoundException when user not found")
    void deleteUser_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> usersService.deleteUser(999L));
        verify(usersRepository, never()).deleteById(any());
    }
}
