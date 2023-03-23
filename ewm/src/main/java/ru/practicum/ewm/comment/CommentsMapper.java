package ru.practicum.ewm.comment;

import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.event.EventsFullDto;
import ru.practicum.ewm.user.Users;
import ru.practicum.ewm.user.UsersMapper;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommentsMapper {
    private static final CommentsMapper INSTANCE = new CommentsMapper();

    private CommentsMapper() {
    }

    public static CommentsMapper getInstance() {
        return INSTANCE;
    }

    public Comments toComment(NewCommentsDto commentDto, Users users, Events events) {
        Comments comments = new Comments();
        comments.setTextComment(commentDto.getText());
        comments.setAuthor(users);
        comments.setEvent(events);

        return comments;
    }

    public static EventsFullDto.CommentInnerDto toCommentShortDto(Comments comments) {
        EventsFullDto.CommentInnerDto commentShortDto = new EventsFullDto.CommentInnerDto();
        commentShortDto.setId(comments.getId());
        commentShortDto.setAuthor(UsersMapper.toCommentShortUserInnerDto(comments.getAuthor()));
        commentShortDto.setText(comments.getTextComment());
        commentShortDto.setCreatedOn(comments.getCreatedOn());
        return commentShortDto;
    }

    public Collection<EventsFullDto.CommentInnerDto> toCommentShortInnerDto(Collection<Comments> comments) {
        return comments.stream()
                .map(CommentsMapper::toCommentShortDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public static CommentsFullDto toCommentFullDto(Comments comments) {
        CommentsFullDto commentsFullDto = new CommentsFullDto();
        commentsFullDto.setId(comments.getId());
        commentsFullDto.setAuthor(UsersMapper.toCommentFullUserInnerDto(comments.getAuthor()));
        commentsFullDto.setText(comments.getTextComment());
        commentsFullDto.setCreatedOn(comments.getCreatedOn());
        commentsFullDto.setEventId(comments.getEvent().getId());
        return commentsFullDto;
    }

    public Collection<CommentsFullDto> toCommentFullDto(Collection<Comments> comments) {
        return comments.stream()
                .map(CommentsMapper::toCommentFullDto)
                .collect(Collectors.toUnmodifiableList());
    }
}