package ru.practicum.ewm.comment;

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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentsServiceImplTest {

    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private EventsRepository eventsRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CommentsServiceImpl commentsService;

    private Users testUser;
    private Events testEvent;
    private Comments testComment;
    private NewCommentsDto newCommentDto;

    @BeforeEach
    void setUp() {
        testUser = new Users(1L, "Test User", "test@test.com");

        testEvent = new Events();
        testEvent.setId(1L);
        testEvent.setState(EventState.PUBLISHED);
        testEvent.setTitle("Test Event");

        testComment = new Comments();
        testComment.setId(1L);
        testComment.setTextComment("Test Comment");
        testComment.setAuthor(testUser);
        testComment.setEvent(testEvent);
        testComment.setCreatedOn(LocalDateTime.now());

        newCommentDto = new NewCommentsDto();
        newCommentDto.setText("New Test Comment");
    }

    @Test
    @DisplayName("addComment: Should create comment successfully")
    void addComment_ShouldCreateCommentSuccessfully() {
        // Arrange
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentsRepository.save(any(Comments.class))).thenAnswer(invocation -> {
            Comments saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        CommentsFullDto result = commentsService.addComment(1L, 1L, newCommentDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Test Comment", result.getText());
        verify(commentsRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("addComment: Should throw NotFoundException when event not found")
    void addComment_ShouldThrowNotFoundException_WhenEventNotFound() {
        // Arrange
        when(eventsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> commentsService.addComment(1L, 999L, newCommentDto));
        verify(commentsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addComment: Should throw NotFoundException when user not found")
    void addComment_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> commentsService.addComment(999L, 1L, newCommentDto));
        verify(commentsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addComment: Should throw ConflictException when event is pending")
    void addComment_ShouldThrowConflictException_WhenEventPending() {
        // Arrange
        testEvent.setState(EventState.PENDING);
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(usersRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ConflictException.class, () -> commentsService.addComment(1L, 1L, newCommentDto));
        verify(commentsRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteComment: Should delete comment when user is author")
    void deleteComment_ShouldDeleteComment_WhenUserIsAuthor() {
        // Arrange
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(testComment));
        doNothing().when(commentsRepository).deleteById(1L);

        // Act
        commentsService.deleteComment(1L, 1L);

        // Assert
        verify(commentsRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteComment: Should throw NotFoundException when user is not author")
    void deleteComment_ShouldThrowNotFoundException_WhenUserNotAuthor() {
        // Arrange
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> commentsService.deleteComment(999L, 1L));
        verify(commentsRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("updateComment: Should update comment successfully")
    void updateComment_ShouldUpdateSuccessfully() {
        // Arrange
        NewCommentsDto updateDto = new NewCommentsDto();
        updateDto.setText("Updated Comment");

        when(usersRepository.existsById(1L)).thenReturn(true);
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentsRepository.save(any())).thenReturn(testComment);

        // Act
        CommentsFullDto result = commentsService.updateComment(1L, 1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Comment", testComment.getTextComment());
    }

    @Test
    @DisplayName("updateComment: Should throw NotFoundException when user not found")
    void updateComment_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        when(usersRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> commentsService.updateComment(999L, 1L, newCommentDto));
    }

    @Test
    @DisplayName("findCommentsByEvent: Should return comments for event")
    void findCommentsByEvent_ShouldReturnComments() {
        // Arrange
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(commentsRepository.findByEvent(testEvent)).thenReturn(List.of(testComment));

        // Act
        Collection<CommentsFullDto> result = commentsService.findCommentsByEvent(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findComment: Should return comment by id")
    void findComment_ShouldReturnComment() {
        // Arrange
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // Act
        CommentsFullDto result = commentsService.findComment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findComment: Should throw NotFoundException when comment not found")
    void findComment_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        when(commentsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> commentsService.findComment(999L));
    }
}
