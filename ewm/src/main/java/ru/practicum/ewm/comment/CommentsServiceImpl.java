package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.event.EventsRepository;
import ru.practicum.ewm.event.EventsState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.Users;
import ru.practicum.ewm.user.UsersRepository;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {
    private static final String NOT_USER = "Пользователь не найден.";
    private static final String NOT_EVENT = "Событие не найдено.";
    private static final String NOT_COMMENT = "Комментарий не найден.";
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final CommentsRepository commentsRepository;

    @Override
    public CommentsFullDto addComment(Long userId, Long eventId, NewCommentsDto commentDto) {
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_EVENT));
        Users users = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_USER));
        if (events.getState() == EventsState.PENDING) {
            throw new ConflictException("Невозможно добавить комментарий к событию, которое не опубликовано.");
        }
        Comments comments = CommentsMapper.getInstance().toComment(commentDto, users, events);
        Comments savedComments = commentsRepository.save(comments);
        return CommentsMapper.getInstance().toCommentFullDto(savedComments);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        if (commentsRepository.findById(commentId)
                .map(Comments::getAuthor)
                .map(Users::getId)
                .filter(id -> id.equals(userId))
                .isPresent()) {
            commentsRepository.deleteById(commentId);
        } else {
            throw new NotFoundException(NOT_COMMENT);
        }
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(NOT_COMMENT));
        commentsRepository.delete(comments);
    }

    @Override
    public CommentsFullDto updateComment(Long userId, Long commentId, NewCommentsDto commentDto) {
        if (!usersRepository.existsById(userId)) {
            throw new NotFoundException(NOT_USER);
        }
        Comments comments = commentsRepository.findById(commentId)
                .map(comment -> {
                    if (!comment.getAuthor().getId().equals(userId)) {
                        throw new NotFoundException(NOT_COMMENT);
                    }
                    comment.setTextComment(commentDto.getText());
                    return commentsRepository.save(comment);
                })
                .orElseThrow(() -> new NotFoundException(NOT_COMMENT));
        return CommentsMapper.getInstance().toCommentFullDto(comments);
    }

    @Override
    public CommentsFullDto updateCommentByAdmin(Long commentId, NewCommentsDto commentDto) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(NOT_COMMENT));
        comments.setTextComment(commentDto.getText());
        return CommentsMapper.getInstance().toCommentFullDto(commentsRepository.save(comments));
    }

    @Override
    public Collection<CommentsFullDto> findCommentsByUserAndEvent(Long userId, Long eventId) {
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_EVENT));
        Users users = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_USER));
        return CommentsMapper.getInstance().toCommentFullDto(commentsRepository.findByAuthorAndEvent(users, events));
    }

    @Override
    public CommentsFullDto findCommentForAuthor(Long userId, Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .filter(comment -> Objects.equals(comment.getAuthor().getId(), userId))
                .orElseThrow(() -> new NotFoundException(NOT_COMMENT));

        return CommentsMapper.getInstance().toCommentFullDto(comments);
    }

    @Override
    public Collection<CommentsFullDto> findCommentsByEvent(Long eventId) {
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_EVENT));
        return CommentsMapper.getInstance().toCommentFullDto(commentsRepository.findByEvent(event));
    }

    @Override
    public CommentsFullDto findComment(Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(NOT_COMMENT));
        return CommentsMapper.getInstance().toCommentFullDto(comments);
    }
}