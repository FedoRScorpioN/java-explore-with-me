package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Slf4j
public class AdminCompilationsController {
    private final CompilationsService compilationsService;
    private final StatsClient statsClient;

    @GetMapping
    public Collection<CompilationsDto> findCompilations(
            HttpServletRequest request,
            @RequestParam(required = false) boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        statsClient.hit(request);
        log.info("{}", ControllerLog.createUrlInfo(request));
        return compilationsService.findCompilations(pinned, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationsDto createCompilation(@RequestBody @Valid NewCompilationsDto compilationDto,
                                             HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return compilationsService.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationsDto updateCompilation(@PathVariable Long compId,
                                             @RequestBody NewCompilationsDto compilationDto, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return compilationsService.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        compilationsService.deleteCompilation(compId);
    }
}