package ru.practicum.ewm.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.Categories;
import ru.practicum.ewm.category.CategoriesRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.Users;
import ru.practicum.ewm.user.UsersRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventsServiceImplTest {

    @Mock
    private EventsRepository eventsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private CategoriesRepository categoriesRepository;
    @Mock
    private RequestsRepository requestsRepository;
    @Mock
    private StatsClient statsClient;

    @InjectMocks
    private EventsServiceImpl eventsService;

    private Users testUser;
    private Categories testCategory;
    private Events testEvent;
    private NewEventsDto newEventDto;

    @BeforeEach
    void setUp() {
        testUser = new Users(1L, "Test User", "test@test.com");

        testCategory = new Categories();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testEvent = new Events();
        testEvent.setId(1L);
        testEvent.setAnnotation("Test Annotation");
        testEvent.setDescription("Test Description");
        testEvent.setEventDate(LocalDateTime.now().plusDays(10));
        testEvent.setInitiator(testUser);
        testEvent.setCategories(testCategory);
        testEvent.setPaid(false);
        testEvent.setParticipantLimit(100);
        testEvent.setRequestModeration(true);
        testEvent.setState(EventState.PENDING);
        testEvent.setTitle("Test Event");
        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);
        testEvent.setLocation(location);
        testEvent.setCreatedOn(LocalDateTime.now());

        newEventDto = new NewEventsDto();
        newEventDto.setAnnotation("New Event Annotation");
        newEventDto.setDescription("New Event Description");
        newEventDto.setEventDate(LocalDateTime.now().plusDays(5));
        newEventDto.setCategory(1L);
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(50);
        newEventDto.setRequestModeration(true);
        newEventDto.setTitle("New Event");
        Location newLocation = new Location();
        newLocation.setLat(55.0f);
        newLocation.setLon(37.0f);
        newEventDto.setLocation(newLocation);
    }

    @Test
    @DisplayName("addEvent: Should create event successfully")
    void addEvent_ShouldCreateEventSuccessfully() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoriesRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(eventsRepository.save(any(Events.class))).thenAnswer(invocation -> {
            Events saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        EventsFullDto result = eventsService.addEvent(1L, newEventDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Event Annotation", result.getAnnotation());
        assertEquals("New Event", result.getTitle());
        verify(eventsRepository, times(1)).save(any(Events.class));
    }

    @Test
    @DisplayName("addEvent: Should throw NotFoundException when user not found")
    void addEvent_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> eventsService.addEvent(1L, newEventDto));
        verify(eventsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addEvent: Should throw NotFoundException when category not found")
    void addEvent_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoriesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> eventsService.addEvent(1L, newEventDto));
        verify(eventsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addEvent: Should throw ConflictException when event date is too soon")
    void addEvent_ShouldThrowConflictException_WhenEventDateTooSoon() {
        // Arrange
        newEventDto.setEventDate(LocalDateTime.now().plusHours(1)); // Less than 2 hours
        // No stubbing needed - exception is thrown before repository calls

        // Act & Assert
        assertThrows(ConflictException.class, () -> eventsService.addEvent(1L, newEventDto));
        verify(eventsRepository, never()).save(any());
    }

    @Test
    @DisplayName("findEvent: Should return event when found")
    void findEvent_ShouldReturnEvent_WhenFound() {
        // Arrange
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        EventsFullDto result = eventsService.findEvent(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Annotation", result.getAnnotation());
    }

    @Test
    @DisplayName("findEvent: Should throw NotFoundException when event not found")
    void findEvent_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        when(eventsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> eventsService.findEvent(999L));
    }

    @Test
    @DisplayName("updateEventByAdmin: Should update event successfully")
    void updateEventByAdmin_ShouldUpdateEventSuccessfully() {
        // Arrange
        UpdateEventsAdminRequest updateRequest = new UpdateEventsAdminRequest();
        updateRequest.setAnnotation("Updated Annotation");
        updateRequest.setTitle("Updated Title");
        updateRequest.setPaid(true);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventsRepository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        EventsFullDto result = eventsService.updateEventByAdmin(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Annotation", result.getAnnotation());
        assertEquals("Updated Title", result.getTitle());
        assertTrue(result.getPaid());
    }

    @Test
    @DisplayName("updateEventByAdmin: Should update location correctly")
    void updateEventByAdmin_ShouldUpdateLocationCorrectly() {
        // Arrange
        Location newLocation = new Location();
        newLocation.setLat(60.0f);
        newLocation.setLon(30.0f);
        UpdateEventsAdminRequest updateRequest = new UpdateEventsAdminRequest();
        updateRequest.setLocation(newLocation);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventsRepository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        EventsFullDto result = eventsService.updateEventByAdmin(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(60.0f, result.getLocation().getLat());
        assertEquals(30.0f, result.getLocation().getLon());
    }

    @Test
    @DisplayName("updateEventByAdmin: Should publish event with PUBLISH_EVENT action")
    void updateEventByAdmin_ShouldPublishEvent_WithPublishAction() {
        // Arrange
        testEvent.setState(EventState.PENDING);
        UpdateEventsAdminRequest updateRequest = new UpdateEventsAdminRequest();
        updateRequest.setStateAction(StateAdminAction.PUBLISH_EVENT);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventsRepository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        EventsFullDto result = eventsService.updateEventByAdmin(1L, updateRequest);

        // Assert
        assertEquals(EventState.PUBLISHED, result.getState());
    }

    @Test
    @DisplayName("updateEventByAdmin: Should throw ConflictException when publishing already published event")
    void updateEventByAdmin_ShouldThrowConflictException_WhenPublishingPublishedEvent() {
        // Arrange
        testEvent.setState(EventState.PUBLISHED);
        UpdateEventsAdminRequest updateRequest = new UpdateEventsAdminRequest();
        updateRequest.setStateAction(StateAdminAction.PUBLISH_EVENT);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        assertThrows(ConflictException.class, () -> eventsService.updateEventByAdmin(1L, updateRequest));
    }

    @Test
    @DisplayName("patchEventByInitiator: Should update multiple fields at once")
    void patchEventByInitiator_ShouldUpdateMultipleFields() {
        // Arrange
        testEvent.setState(EventState.PENDING);
        UpdateEventsUserRequest updateRequest = new UpdateEventsUserRequest();
        updateRequest.setAnnotation("Updated Annotation");
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPaid(true);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventsRepository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        EventsFullDto result = eventsService.patchEventByInitiator(1L, 1L, updateRequest);

        // Assert
        assertEquals("Updated Annotation", result.getAnnotation());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.getPaid());
    }

    @Test
    @DisplayName("patchEventByInitiator: Should update location correctly")
    void patchEventByInitiator_ShouldUpdateLocationCorrectly() {
        // Arrange
        testEvent.setState(EventState.PENDING);
        Location newLocation = new Location();
        newLocation.setLat(60.0f);
        newLocation.setLon(30.0f);
        UpdateEventsUserRequest updateRequest = new UpdateEventsUserRequest();
        updateRequest.setLocation(newLocation);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventsRepository.save(any(Events.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        EventsFullDto result = eventsService.patchEventByInitiator(1L, 1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(60.0f, result.getLocation().getLat());
        assertEquals(30.0f, result.getLocation().getLon());
    }

    @Test
    @DisplayName("patchEventByInitiator: Should throw NotFoundException when event not found")
    void patchEventByInitiator_ShouldThrowNotFoundException_WhenEventNotFound() {
        // Arrange
        when(eventsRepository.findById(999L)).thenReturn(Optional.empty());
        UpdateEventsUserRequest updateRequest = new UpdateEventsUserRequest();

        // Act & Assert
        assertThrows(NotFoundException.class, () -> eventsService.patchEventByInitiator(1L, 999L, updateRequest));
    }

    @Test
    @DisplayName("patchEventByInitiator: Should throw ConflictException when updating published event")
    void patchEventByInitiator_ShouldThrowConflictException_WhenEventPublished() {
        // Arrange
        testEvent.setState(EventState.PUBLISHED);
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        UpdateEventsUserRequest updateRequest = new UpdateEventsUserRequest();

        // Act & Assert
        assertThrows(ConflictException.class, () -> eventsService.patchEventByInitiator(1L, 1L, updateRequest));
    }

    @Test
    @DisplayName("patchEventByInitiator: Should throw NotFoundException when user is not initiator")
    void patchEventByInitiator_ShouldThrowNotFoundException_WhenUserNotInitiator() {
        // Arrange
        testEvent.setState(EventState.PENDING);
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        UpdateEventsUserRequest updateRequest = new UpdateEventsUserRequest();

        // Act & Assert - user 2 is not the initiator
        assertThrows(NotFoundException.class, () -> eventsService.patchEventByInitiator(2L, 1L, updateRequest));
    }

    @Test
    @DisplayName("findEvents: Should return events for user")
    void findEvents_ShouldReturnEventsForUser() {
        // Arrange
        List<Events> events = List.of(testEvent);
        when(eventsRepository.findByInitiatorId(eq(1L), any(Pageable.class))).thenReturn(events);
        when(statsClient.getStats(anyList())).thenReturn(Collections.emptyList());

        // Act
        Collection<EventsFullDto> result = eventsService.findEvents(1L, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
