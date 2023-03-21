package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Slf4j
public class AdminCategoriesController {
    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriesDto createCategory(@RequestBody @Valid NewCategoriesDto categoryDto, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return categoriesService.addCategory(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoriesDto updateCategory(@PathVariable Long catId,
                                        @RequestBody @Valid NewCategoriesDto categoryDto, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return categoriesService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        categoriesService.deleteCategory(catId);
    }
}