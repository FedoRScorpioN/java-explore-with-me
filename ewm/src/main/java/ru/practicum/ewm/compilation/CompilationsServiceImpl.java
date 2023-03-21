package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.event.EventsRepository;
import ru.practicum.ewm.event.EventsService;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private static final String NOT_FOUND = "Подборка не найдена или недоступна";
    public final CompilationsRepository compilationsRepository;
    public final EventsRepository eventsRepository;
    public final EventsService eventsService;

    @Override
    public Collection<CompilationsDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        Collection<Compilations> compilations = pinned == null ? compilationsRepository.findAll(pageable).getContent()
                : compilationsRepository.findByPinned(pinned, pageable);
        Set<Events> events = compilations.stream()
                .flatMap(c -> c.getEvents()
                        .stream())
                .collect(Collectors.toSet());
        Map<Long, Long> views = eventsService.getViews(events);
        return CompilationsMapper.getInstance().toCompilationDto(compilations, views);
    }

    @Override
    public CompilationsDto addCompilation(NewCompilationsDto compilationDto) {
        Collection<Events> events = eventsRepository.findAllById(Arrays.asList(compilationDto.getEvents()));
        Compilations compilations = CompilationsMapper.getInstance().toCompilation(compilationDto, events);
        Map<Long, Long> views = eventsService.getViews(compilations.getEvents());
        return CompilationsMapper.getInstance().toCompilationDto(compilationsRepository.save(compilations), views);
    }

    @Override
    public void deleteCompilation(Long id) {
        compilationsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        compilationsRepository.deleteById(id);
    }

    @Override
    public CompilationsDto findCompilation(Long id) {
        Compilations compilations = compilationsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        Map<Long, Long> views = eventsService.getViews(compilations.getEvents());
        return CompilationsMapper.getInstance().toCompilationDto(compilations, views);
    }

    @Override
    public CompilationsDto updateCompilation(Long id, NewCompilationsDto compilationDto) {
        Compilations compilationToUpdate = compilationsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        compilationToUpdate.setPinned(compilationDto.getPinned() != null ? compilationDto.getPinned()
                : compilationToUpdate.getPinned());
        compilationToUpdate.setTitle(compilationDto.getTitle() != null ? compilationDto.getTitle()
                : compilationToUpdate.getTitle());
        if (compilationDto.getEvents() != null) {
            Collection<Events> newEvents = eventsRepository.findAllById(Arrays.asList(compilationDto.getEvents()));
            if (compilationDto.getEvents().length != newEvents.size()) {
                throw new NotFoundException("Одно из событий отсутствует в подборке.");
            }
            compilationToUpdate.setEvents(newEvents);
        }
        Map<Long, Long> views = eventsService.getViews(compilationToUpdate.getEvents());
        return CompilationsMapper.getInstance().toCompilationDto(compilationsRepository.save(compilationToUpdate), views);
    }
}