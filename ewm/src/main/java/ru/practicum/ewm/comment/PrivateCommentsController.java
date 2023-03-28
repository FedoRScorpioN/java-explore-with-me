package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{usersId}")
public class PrivateCommentsController {
    private final CommentsService commentsService;

    @GetMapping("/comments")
    public Collection<CommentsFullDto> findComments(@PathVariable Long usersId,
                                                    @RequestParam Long eventId,
                                                    HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.findCommentsByUserAndEvent(usersId, eventId);
    }

    @GetMapping("/comments/{commentId}")
    public CommentsFullDto findComment(@PathVariable Long usersId,
                                       @PathVariable Long commentId,
                                       HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.findCommentForAuthor(usersId, commentId);
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentsFullDto createComment(@PathVariable Long usersId,
                                         @RequestParam Long eventId,
                                         @RequestBody @Valid NewCommentsDto commentDto,
                                         HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.addComment(usersId, eventId, commentDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long usersId,
                              @PathVariable Long commentId,
                              HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        commentsService.deleteComment(usersId, commentId);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentsFullDto patchComment(@PathVariable Long usersId,
                                        @PathVariable Long commentId,
                                        @RequestBody @Valid NewCommentsDto commentDto,
                                        HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.updateComment(usersId, commentId, commentDto);
    }
}