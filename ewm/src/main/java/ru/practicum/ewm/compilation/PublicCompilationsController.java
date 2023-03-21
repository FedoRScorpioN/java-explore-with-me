package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationsController {
    private final CompilationsService compilationsService;
    private final StatsClient statsClient;

    @GetMapping
    public Collection<CompilationsDto> findCompilations(
            HttpServletRequest request,
            @RequestParam(required = false) boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        statsClient.hit(request);
        return compilationsService.findCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationsDto findCompilation(@PathVariable Long compId) {
        return compilationsService.findCompilation(compId);
    }
}