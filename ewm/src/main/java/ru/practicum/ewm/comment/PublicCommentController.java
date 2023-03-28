package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@Slf4j
public class PublicCommentController {
    private final CommentsService commentsService;

    @GetMapping
    public Collection<CommentsFullDto> findComments(@RequestParam Long eventId,
                                                    HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.findCommentsByEvent(eventId);
    }

    @GetMapping("/{commentId}")
    public CommentsFullDto findComment(@PathVariable Long commentId,
                                       HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return commentsService.findComment(commentId);
    }
}