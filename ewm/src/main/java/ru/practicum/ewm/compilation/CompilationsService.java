package ru.practicum.ewm.compilation;

import java.util.Collection;

public interface CompilationsService {
    Collection<CompilationsDto> findCompilations(Boolean pinned, Integer from, Integer size);

    CompilationsDto addCompilation(NewCompilationsDto compilationDto);

    void deleteCompilation(Long id);

    CompilationsDto findCompilation(Long id);

    CompilationsDto updateCompilation(Long id, NewCompilationsDto compilationDto);
}