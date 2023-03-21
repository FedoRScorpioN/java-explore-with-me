package ru.practicum.ewm.compilation;

import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.event.EventsMapper;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationsMapper {
    private static final CompilationsMapper INSTANCE = new CompilationsMapper();

    private CompilationsMapper() {
    }

    public static CompilationsMapper getInstance() {
        return INSTANCE;
    }

    public static CompilationsDto toCompilationDto(Compilations compilations, Map<Long, Long> eventViews) {
        CompilationsDto compilationsDto = new CompilationsDto();
        compilationsDto.setId(compilations.getId());
        compilationsDto.setPinned(compilations.getPinned());
        compilationsDto.setTitle(compilations.getTitle());
        compilationsDto.setEvents(EventsMapper.getInstance().toEventShortInnerDto(compilations.getEvents(), eventViews));
        return compilationsDto;
    }

    public Collection<CompilationsDto> toCompilationDto(Collection<Compilations> compilations,
                                                        Map<Long, Long> eventViews) {
        return compilations.stream()
                .map(compilation -> CompilationsMapper.toCompilationDto(compilation, eventViews))
                .collect(Collectors.toUnmodifiableList());
    }

    public Compilations toCompilation(NewCompilationsDto compilationDto, Collection<Events> events) {
        Compilations compilations = new Compilations();
        compilations.setTitle(compilationDto.getTitle());
        compilations.setPinned(compilationDto.getPinned());
        compilations.setEvents(events);
        return compilations;
    }
}