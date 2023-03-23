package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
@Slf4j
public class AdminCommentsController {
    private final CommentsService commentsService;

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        commentsService.deleteCommentByAdmin(commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentsFullDto updateComment(@PathVariable Long commentId,
                                         @RequestBody @Valid NewCommentsDto commentDto,
                                         HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.updateCommentByAdmin(commentId, commentDto);
    }
}