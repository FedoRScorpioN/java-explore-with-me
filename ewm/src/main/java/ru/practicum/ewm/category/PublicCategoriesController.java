package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Slf4j
public class PublicCategoriesController {
    private final CategoriesService categoriesService;
    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoriesDto> findCategories(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        statsClient.hit(request);
        log.info("{}", ControllerLog.createUrlInfo(request));
        return categoriesService.findCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoriesDto findCategory(@PathVariable Long catId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return categoriesService.findCategory(catId);
    }
}