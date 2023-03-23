package ru.practicum.ewm.category;

import ru.practicum.ewm.event.EventsFullDto;
import ru.practicum.ewm.event.EventsShortDto;
import ru.practicum.ewm.event.UpdateEventsUserRequest;

import java.util.Collection;
import java.util.stream.Collectors;

public final class CategoriesMapper {
    private static final CategoriesMapper INSTANCE = new CategoriesMapper();

    private CategoriesMapper() {
    }

    public static CategoriesMapper getInstance() {
        return INSTANCE;
    }

    public Categories toCategory(NewCategoriesDto categoryDto) {
        Categories categories = new Categories();
        categories.setName(categoryDto.getName());
        return categories;
    }

    public Categories toCategory(UpdateEventsUserRequest.CategoryDto categoryDto) {
        Categories categories = new Categories();
        categories.setName(categoryDto.getName());
        categories.setId(categoryDto.getId());
        return categories;
    }

    public CategoriesDto toCategoryDto(Categories categories) {
        CategoriesDto categoriesDto = new CategoriesDto();
        categoriesDto.setId(categories.getId());
        categoriesDto.setName(categories.getName());
        return categoriesDto;
    }

    public EventsFullDto.CategoryDto toCategoryInnerDto(Categories categories) {
        EventsFullDto.CategoryDto categoryDto = new EventsFullDto.CategoryDto();
        categoryDto.setId(categories.getId());
        categoryDto.setName(categories.getName());
        return categoryDto;
    }

    public static EventsShortDto.CategoryDto toCategoryShortInnerDto(Categories categories) {
        EventsShortDto.CategoryDto categoryDto = new EventsShortDto.CategoryDto();
        categoryDto.setId(categories.getId());
        categoryDto.setName(categories.getName());
        return categoryDto;
    }

    public Collection<CategoriesDto> toCategoryDto(Collection<Categories> categories) {
        return categories.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toUnmodifiableList());
    }
}