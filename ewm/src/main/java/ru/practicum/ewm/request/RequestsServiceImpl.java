package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.event.EventsRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UsersRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService {
    private static final String LIMIT_USERS = "Достигнут лимит участников.";
    private static final String NOT_REQUEST = "Заявка на участие не найдена.";
    private static final String NOT_USER = "Пользователь не найден.";
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;

    @Override
    public ParticipationRequestsDto addRequest(Long userId, Long eventId) {
        usersRepository.findById(userId).orElseThrow(RuntimeException::new);
        Events events = eventsRepository.findById(eventId).orElseThrow(RuntimeException::new);
        if (events.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Собитыие не создано или не опубликовано.");
        }
        if (userId.equals(events.getInitiator().getId())) {
            throw new ConflictException("Вы не можете запросить участие в собственном мероприятии.");
        }
        if (events.getRequestModeration()) {
            if (events.getConfirmedRequests().size() >= events.getParticipantLimit()) {
                throw new ConflictException(LIMIT_USERS);
            }
        } else {
            if (events.getRequests().size() >= events.getParticipantLimit()) {
                throw new ConflictException(LIMIT_USERS);
            }
        }
        ParticipationRequestsDto request;
        try {
            request = RequestsMapper.getInstance().toRequestDto(requestsRepository.save(ParticipationRequests.of(userId, eventId)));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Повторите запрос.");
        }
        return request;
    }

    @Override
    public Collection<ParticipationRequestsDto> findRequests(Long userId) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_USER));
        return RequestsMapper.getInstance().toRequestDto(requestsRepository.findByRequesterId(userId));
    }

    @Override
    public ParticipationRequestsDto cancelRequest(Long userId, Long requestId) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_USER));
        ParticipationRequests request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(NOT_REQUEST));
        if (!request.getRequesterId().equals(userId)) {
            throw new NotFoundException(NOT_REQUEST);
        }
        if (request.getStatus() == ParticipationRequestsStatus.REJECTED ||
                request.getStatus() == ParticipationRequestsStatus.CANCELED) {
            throw new ConflictException("Ошибка отмены запроса.");
        }
        request.setStatus(ParticipationRequestsStatus.CANCELED);
        return RequestsMapper.getInstance().toRequestDto(requestsRepository.save(request));
    }
}