package ru.practicum.ewm.event;

import com.mysema.commons.lang.Pair;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Categories;
import ru.practicum.ewm.category.CategoriesMapper;
import ru.practicum.ewm.category.CategoriesRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.Users;
import ru.practicum.ewm.user.UsersRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {
    private static final String INCORRECT_TIME = "Некорректное время событий.";
    private static final String NOT_USER = "Пользователь не найден.";
    private static final String NOT_EVENTS = "Событие не найдено.";
    private static final String NOT_CATEGORY = "Категория не найдена.";
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    private final RequestsRepository requestsRepository;
    private final StatsClient statsClient;

    @Override
    public Map<Long, Long> getViews(Collection<Events> events) {
        List<String> uris = events.stream()
                .map(Events::getId)
                .map(id -> "/events/" + id.toString())
                .collect(Collectors.toUnmodifiableList());
        List<ViewStatsDto> eventStats = statsClient.getStats(uris);
        return eventStats.stream()
                .filter(statRecord -> statRecord.getApp().equals("ewm-service"))
                .collect(Collectors.toMap(statRecord -> {
                                    Pattern pattern = Pattern.compile("/events/([0-9]*)");
                                    Matcher matcher = pattern.matcher(statRecord.getUri());
                                    return Long.parseLong(matcher.group(1));
                                },
                                ViewStatsDto::getHits
                        )
                );
    }

    @Override
    public EventsFullDto addEvent(final Long userId, final NewEventsDto eventDto) {
        LocalDateTime twoHoursFromNow = LocalDateTime.now().plusHours(2);
        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(twoHoursFromNow)) {
            throw new ConflictException(INCORRECT_TIME);
        }
        Users initiator = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_USER));
        Categories categories = categoriesRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(NOT_CATEGORY));
        Events events = EventsMapper.getInstance().toEvent(initiator, categories, eventDto);
        return EventsMapper.getInstance().toEventFullDto(eventsRepository.save(events), 0L);
    }

    @Override
    public Collection<EventsFullDto> findEvents(Long[] users,
                                                String[] states,
                                                Long[] categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Integer from,
                                                Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        QEvents qEvent = QEvents.events;
        BooleanExpression expression = qEvent.id.isNotNull();
        if (users != null && users.length > 0) {
            expression = expression.and(qEvent.initiator.id.in(users));
        }
        if (states != null && states.length > 0) {
            expression = expression.and(qEvent.state.in(Arrays.stream(states)
                    .map(EventState::valueOf)
                    .collect(Collectors.toUnmodifiableList())));
        }
        if (categories != null && categories.length > 0) {
            expression = expression.and(qEvent.categories.id.in(categories));
        }
        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.loe(rangeEnd));
        }
        Collection<Events> events = eventsRepository.findAll(expression, pageable).getContent();
        Map<Long, Long> views = this.getViews(events);
        return EventsMapper.getInstance().toEventFullDto(events, views);
    }

    @Override
    public Collection<EventsShortDto> findEvents(String text,
                                                 Boolean paid,
                                                 Boolean onlyAvailable,
                                                 EventsSort sort,
                                                 Long[] categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        QEvents qEvent = QEvents.events;
        BooleanExpression expression = qEvent.state.eq(EventState.PUBLISHED);
        if (text != null) {
            expression = expression.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (paid != null) {
            expression = expression.and(qEvent.paid.eq(paid));
        }
        if (categories != null && categories.length > 0) {
            expression = expression.and(qEvent.categories.id.in(categories));
        }
        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.loe(rangeEnd));
        }
        Collection<Events> events = eventsRepository.findAll(expression, pageable).getContent();
        return EventsMapper.getInstance().toEventShortDto(events, this.getViews(events));
    }

    @Override
    public EventsFullDto patchEventByInitiator(Long userId, Long eventId, UpdateEventsUserRequest updateRequest) {
        final Events eventToUpdate = eventsRepository.findById(eventId).orElseThrow(RuntimeException::new);
        if (updateRequest.getEventDate() != null
                && updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException(INCORRECT_TIME);
        }
        if (eventToUpdate.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Невозможно обновить опубликованное мероприятие.");
        }
        if (!userId.equals(eventToUpdate.getInitiator().getId())) {
            throw new NotFoundException(NOT_EVENTS);
        }
        final Events updatedEvents = Stream.<Pair<Predicate<UpdateEventsUserRequest>, Consumer<Events>>>of(
                        Pair.of(req -> req.getAnnotation() != null, evt -> evt.setAnnotation(updateRequest.getAnnotation())),
                        Pair.of(req -> req.getDescription() != null, evt -> evt.setDescription(updateRequest.getDescription())),
                        Pair.of(req -> req.getCategory() != null, evt -> evt.setCategories(CategoriesMapper.getInstance()
                                .toCategory(updateRequest.getCategory()))),
                        Pair.of(req -> req.getEventDate() != null, evt -> evt.setEventDate(updateRequest.getEventDate())),
                        Pair.of(req -> req.getLocation() != null, evt -> evt.setPaid(updateRequest.getPaid())),
                        Pair.of(req -> req.getParticipantLimit() != null, evt -> evt.setParticipantLimit(updateRequest
                                .getParticipantLimit())),
                        Pair.of(req -> req.getRequestModeration() != null, evt -> evt.setRequestModeration(updateRequest
                                .getRequestModeration())),
                        Pair.of(req -> req.getStateAction() != null, evt -> {
                            if (StateUserAction.SEND_TO_REVIEW.equals(updateRequest.getStateAction())) {
                                evt.setState(EventState.PENDING);
                            } else if (StateUserAction.CANCEL_REVIEW.equals(updateRequest.getStateAction())) {
                                evt.setState(EventState.CANCELED);
                            }
                        }),
                        Pair.of(req -> req.getTitle() != null, evt -> evt.setTitle(updateRequest.getTitle()))
                ).filter(pair -> pair.getFirst().test(updateRequest))
                .findFirst()
                .map(pair -> {
                    pair.getSecond().accept(eventToUpdate);
                    return eventsRepository.save(eventToUpdate);
                })
                .orElseThrow(IllegalArgumentException::new);
        return EventsMapper.getInstance().toEventFullDto(updatedEvents, this.getViews(List.of(updatedEvents)));
    }

    @Override
    public EventsFullDto updateEventByAdmin(Long eventId, UpdateEventsAdminRequest updateRequest) {
        Events eventsToUpdate = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_EVENTS));
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(nowPlusOneHour)) {
            throw new ConflictException(INCORRECT_TIME);
        }
        if (updateRequest.getStateAction() != null) {
            StateAdminAction stateAction = updateRequest.getStateAction();
            EventState eventState = eventsToUpdate.getState();
            if ((stateAction == StateAdminAction.PUBLISH_EVENT && eventState != EventState.PENDING)
                    || (stateAction == StateAdminAction.REJECT_EVENT && eventState == EventState.PUBLISHED)) {
                throw new ConflictException(stateAction == StateAdminAction.PUBLISH_EVENT ?
                        "Конфликт с сервисом статистики." : "Невозможно отклонить опубликованное мероприятие.");
            }
            if (stateAction == StateAdminAction.PUBLISH_EVENT) {
                eventsToUpdate.setState(EventState.PUBLISHED);
            } else if (stateAction == StateAdminAction.REJECT_EVENT) {
                eventsToUpdate.setState(EventState.CANCELED);
            }
        }
        if (updateRequest.getAnnotation() != null) {
            eventsToUpdate.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Categories category = categoriesRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(NOT_CATEGORY));
            eventsToUpdate.setCategories(category);
        }
        if (updateRequest.getDescription() != null) {
            eventsToUpdate.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            eventsToUpdate.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            eventsToUpdate.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            eventsToUpdate.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            eventsToUpdate.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            eventsToUpdate.setTitle(updateRequest.getTitle());
        }
        Events updatedEvents = eventsRepository.save(eventsToUpdate);
        return EventsMapper.getInstance()
                .toEventFullDto(updatedEvents, this.getViews(List.of(updatedEvents)));
    }

    @Override
    public EventsRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                               EventsRequestStatusUpdateRequest statusUpdateRequest) {
        usersRepository.findById(userId).orElseThrow(RuntimeException::new);
        Events events = eventsRepository.findById(eventId).orElseThrow(RuntimeException::new);
        EventsRequestStatusUpdateResult result = new EventsRequestStatusUpdateResult();
        List<ParticipationRequests> allPendingRequestsLeftUnprocessed = new ArrayList<>();
        for (Long requestId : statusUpdateRequest.getRequestIds()) {
            ParticipationRequests request = requestsRepository.findById(requestId).orElseThrow(RuntimeException::new);

            if (request.getStatus() != ParticipationRequestsStatus.PENDING) {
                throw new ConflictException("Невозможно изменить статус запроса.");
            }
            if (statusUpdateRequest.getStatus() == ParticipationRequestsStatusUpdate.CONFIRMED &&
                    (events.getParticipantLimit() == 0 || !events.getRequestModeration() ||
                            events.getParticipantLimit() > events.getRequests().size())) {
                updateRequestStatus(request, ParticipationRequestsStatus.CONFIRMED, result);
            } else if (statusUpdateRequest.getStatus() == ParticipationRequestsStatusUpdate.CONFIRMED &&
                    events.getParticipantLimit() > 0 && events.getParticipantLimit() > events.getConfirmedRequests().size()) {
                updateRequestStatus(request, ParticipationRequestsStatus.CONFIRMED, result);
            } else if (statusUpdateRequest.getStatus() == ParticipationRequestsStatusUpdate.CONFIRMED &&
                    events.getConfirmedRequests().size() < events.getParticipantLimit()) {
                updateRequestStatus(request, ParticipationRequestsStatus.CONFIRMED, result);
            } else if (statusUpdateRequest.getStatus() == ParticipationRequestsStatusUpdate.CONFIRMED) {
                allPendingRequestsLeftUnprocessed.add(request);
            } else {
                updateRequestStatus(request, ParticipationRequestsStatus.REJECTED, result);
            }
        }
        if (statusUpdateRequest.getStatus() == ParticipationRequestsStatusUpdate.CONFIRMED &&
                allPendingRequestsLeftUnprocessed.size() > 0 &&
                events.getConfirmedRequests().size() >= events.getParticipantLimit()) {
            rejectAllPendingRequests(allPendingRequestsLeftUnprocessed, events);
            throw new ConflictException("Достигнут лимит участников.");
        }
        return result;
    }

    private void updateRequestStatus(ParticipationRequests request, ParticipationRequestsStatus status,
                                     EventsRequestStatusUpdateResult result) {
        request.setStatus(status);
        EventsRequestStatusUpdateResult.ParticipationRequestDto requestDto =
                RequestsMapper.getInstance().toRequestInnerDto(requestsRepository.save(request));
        if (status == ParticipationRequestsStatus.CONFIRMED) {
            result.getConfirmedRequests().add(requestDto);
        } else {
            result.getRejectedRequests().add(requestDto);
        }
    }

    private void rejectAllPendingRequests(List<ParticipationRequests> allPendingRequests,
                                          Events events) {
        for (ParticipationRequests pendingRequest : allPendingRequests) {
            pendingRequest.setStatus(ParticipationRequestsStatus.REJECTED);
            requestsRepository.save(pendingRequest);
        }
    }

    @Override
    public EventsFullDto findEvent(Long id) {
        Events events = eventsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_EVENTS));
        Map<Long, Long> views = getViews(List.of(events));
        return EventsMapper.getInstance().toEventFullDto(events, views);
    }

    @Override
    public Collection<ParticipationRequestsDto> findEventRequests(Long userId, Long eventId) {
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_EVENTS));
        if (!events.getInitiator().getId().equals(userId)) {
            throw new NotFoundException(NOT_EVENTS);
        }
        return RequestsMapper.getInstance().toRequestDto(events.getRequests());
    }

    @Override
    public Collection<EventsFullDto> findEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        Collection<Events> events = eventsRepository.findByInitiatorId(userId, pageable);
        return EventsMapper.getInstance().toEventFullDto(events, this.getViews(events));
    }
}