package ru.practicum.ewm.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.event.EventsRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.Users;
import ru.practicum.ewm.user.UsersRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestsServiceImplTest {

    @Mock
    private RequestsRepository requestsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private EventsRepository eventsRepository;

    @InjectMocks
    private RequestsServiceImpl requestsService;

    private Users testUser;
    private Users initiator;
    private Events testEvent;
    private ParticipationRequests testRequest;

    @BeforeEach
    void setUp() {
        testUser = new Users(2L, "Test User", "test@test.com");
        initiator = new Users(1L, "Initiator", "init@test.com");

        testEvent = new Events();
        testEvent.setId(1L);
        testEvent.setInitiator(initiator);
        testEvent.setState(EventState.PUBLISHED);
        testEvent.setParticipantLimit(100);
        testEvent.setRequestModeration(true);
        testEvent.setRequests(new ArrayList<>());

        testRequest = new ParticipationRequests();
        testRequest.setId(1L);
        testRequest.setRequesterId(2L);
        testRequest.setEvent(testEvent);
        testRequest.setStatus(ParticipationRequestsStatus.PENDING);
        testRequest.setCreated(LocalDateTime.now());
    }

    @Test
    @DisplayName("addRequest: Should create participation request successfully")
    void addRequest_ShouldCreateRequestSuccessfully() {
        // Arrange
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(requestsRepository.save(any(ParticipationRequests.class))).thenAnswer(invocation -> {
            ParticipationRequests saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        ParticipationRequestsDto result = requestsService.addRequest(2L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getRequester());
        verify(requestsRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("addRequest: Should throw NotFoundException when user not found")
    void addRequest_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> requestsService.addRequest(999L, 1L));
        verify(requestsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addRequest: Should throw NotFoundException when event not found")
    void addRequest_ShouldThrowNotFoundException_WhenEventNotFound() {
        // Arrange
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(eventsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> requestsService.addRequest(2L, 999L));
        verify(requestsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addRequest: Should throw ConflictException when event not published")
    void addRequest_ShouldThrowConflictException_WhenEventNotPublished() {
        // Arrange
        testEvent.setState(EventState.PENDING);
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        assertThrows(ConflictException.class, () -> requestsService.addRequest(2L, 1L));
        verify(requestsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addRequest: Should throw ConflictException when user is initiator")
    void addRequest_ShouldThrowConflictException_WhenUserIsInitiator() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        assertThrows(ConflictException.class, () -> requestsService.addRequest(1L, 1L));
        verify(requestsRepository, never()).save(any());
    }

    @Test
    @DisplayName("findRequests: Should return user requests")
    void findRequests_ShouldReturnUserRequests() {
        // Arrange
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(requestsRepository.findByRequesterId(2L)).thenReturn(List.of(testRequest));

        // Act
        Collection<ParticipationRequestsDto> result = requestsService.findRequests(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findRequests: Should throw NotFoundException when user not found")
    void findRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> requestsService.findRequests(999L));
    }

    @Test
    @DisplayName("cancelRequest: Should cancel request successfully")
    void cancelRequest_ShouldCancelSuccessfully() {
        // Arrange
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(requestsRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestsRepository.save(any())).thenReturn(testRequest);

        // Act
        ParticipationRequestsDto result = requestsService.cancelRequest(2L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(ParticipationRequestsStatus.CANCELED, testRequest.getStatus());
    }

    @Test
    @DisplayName("cancelRequest: Should throw NotFoundException when request not found")
    void cancelRequest_ShouldThrowNotFoundException_WhenRequestNotFound() {
        // Arrange
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(requestsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> requestsService.cancelRequest(2L, 999L));
    }

    @Test
    @DisplayName("cancelRequest: Should throw NotFoundException when user is not requester")
    void cancelRequest_ShouldThrowNotFoundException_WhenUserNotRequester() {
        // Arrange
        Users otherUser = new Users(999L, "Other", "other@test.com");
        when(usersRepository.findById(999L)).thenReturn(Optional.of(otherUser)); // Different user
        when(requestsRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> requestsService.cancelRequest(999L, 1L));
    }

    @Test
    @DisplayName("cancelRequest: Should throw ConflictException when request already rejected")
    void cancelRequest_ShouldThrowConflictException_WhenAlreadyRejected() {
        // Arrange
        testRequest.setStatus(ParticipationRequestsStatus.REJECTED);
        when(usersRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(requestsRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        // Act & Assert
        assertThrows(ConflictException.class, () -> requestsService.cancelRequest(2L, 1L));
    }
}
