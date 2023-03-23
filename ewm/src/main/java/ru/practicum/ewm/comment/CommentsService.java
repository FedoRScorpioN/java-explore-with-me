package ru.practicum.ewm.comment;

import java.util.Collection;

public interface CommentsService {
    CommentsFullDto addComment(Long userId, Long eventId, NewCommentsDto commentDto);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentsFullDto updateComment(Long userId, Long commentId, NewCommentsDto commentDto);

    CommentsFullDto updateCommentByAdmin(Long commentId, NewCommentsDto commentDto);

    Collection<CommentsFullDto> findCommentsByUserAndEvent(Long userId, Long eventId);

    CommentsFullDto findCommentForAuthor(Long userId, Long commentId);

    Collection<CommentsFullDto> findCommentsByEvent(Long eventId);

    CommentsFullDto findComment(Long commentId);
}