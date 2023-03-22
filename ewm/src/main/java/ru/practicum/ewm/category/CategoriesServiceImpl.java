package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventsRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private static final String NOT_FOUND = "Категория не найдена или недоступна";
    private static final String NOT_UNIQUE = "Имя категории должно быть уникальным";
    private final CategoriesRepository categoriesRepository;
    private final EventsRepository eventsRepository;

    @Override
    public CategoriesDto addCategory(final NewCategoriesDto categoryDto) {
        Categories categories = CategoriesMapper.getInstance().toCategory(categoryDto);
        try {
            Categories createdCategories = categoriesRepository.save(categories);
            return CategoriesMapper.getInstance().toCategoryDto(createdCategories);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(NOT_UNIQUE);
        }
    }

    @Override
    public Collection<CategoriesDto> findCategories(Integer from, Integer size) {
        return CategoriesMapper.getInstance().toCategoryDto(categoriesRepository
                .findAll(PageRequest.of(from, size)).getContent());
    }

    @Override
    public CategoriesDto updateCategory(Long id, NewCategoriesDto categoryDto) {
        Categories categoriesToUpdate = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        categoriesToUpdate.setName(categoryDto.getName());
        try {
            Categories updatedCategories = categoriesRepository.save(categoriesToUpdate);
            return CategoriesMapper.getInstance().toCategoryDto(updatedCategories);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(NOT_UNIQUE);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        Categories categories = categoriesRepository.findById(id).orElseThrow(()
                -> new NotFoundException(NOT_FOUND));
        if (eventsRepository.countByCategoriesId(id) > 0) {
            throw new ConflictException("Удаление невозможно. Существуют события, связанные с категорией");
        }
        categoriesRepository.delete(categories);
    }

    @Override
    public CategoriesDto findCategory(Long id) {
        Categories categories = categoriesRepository.findById(id).orElseThrow(()
                -> new NotFoundException(NOT_FOUND));
        return CategoriesMapper.getInstance().toCategoryDto(categories);
    }
}