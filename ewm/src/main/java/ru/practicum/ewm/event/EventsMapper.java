package ru.practicum.ewm.event;

import ru.practicum.ewm.category.Categories;
import ru.practicum.ewm.category.CategoriesMapper;
import ru.practicum.ewm.comment.CommentsMapper;
import ru.practicum.ewm.compilation.CompilationsDto;
import ru.practicum.ewm.user.Users;
import ru.practicum.ewm.user.UsersMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventsMapper {
    private static final EventsMapper INSTANCE = new EventsMapper();

    private EventsMapper() {
    }

    public static EventsMapper getInstance() {
        return INSTANCE;
    }

    public Events toEvent(Users initiator, Categories category, NewEventsDto eventDto) {
        Events events = new Events();
        events.setAnnotation(eventDto.getAnnotation());
        events.setCategories(category);
        events.setDescription(eventDto.getDescription());
        events.setEventDate(eventDto.getEventDate());
        events.setLocation(eventDto.getLocation());
        events.setPaid(eventDto.getPaid());
        events.setParticipantLimit(eventDto.getParticipantLimit());
        events.setRequestModeration(eventDto.getRequestModeration());
        events.setTitle(eventDto.getTitle());
        events.setInitiator(initiator);
        events.setState(EventsState.PENDING);
        return events;
    }

    public EventsFullDto toEventFullDto(Events events, Long views) {
        EventsFullDto eventDto = new EventsFullDto();
        eventDto.setId(events.getId());
        eventDto.setAnnotation(events.getAnnotation());
        eventDto.setCategory(CategoriesMapper.getInstance().toCategoryInnerDto(events.getCategories()));
        eventDto.setConfirmedRequests((long) events.getConfirmedRequests().size());
        eventDto.setCreatedOn(events.getCreatedOn());
        eventDto.setDescription(events.getDescription());
        eventDto.setEventDate(events.getEventDate());
        eventDto.setInitiator(UsersMapper.getInstance().toUserShortInnerDto(events.getInitiator()));
        eventDto.setLocation(events.getLocation());
        eventDto.setPaid(events.getPaid());
        eventDto.setParticipantLimit(events.getParticipantLimit());
        eventDto.setPublishedOn(events.getPublishedOn());
        eventDto.setRequestModeration(events.getRequestModeration());
        eventDto.setState(events.getState());
        eventDto.setTitle(events.getTitle());
        eventDto.setViews(views == null ? 0 : views);
        if (events.getComments() == null) {
            eventDto.setComments(new ArrayList<>());
        } else {
            eventDto.setComments(CommentsMapper.getInstance().toCommentShortInnerDto(events.getComments()));
        }
        return eventDto;
    }

    public EventsShortDto toEventShortDto(Events events, Long views) {
        EventsShortDto eventDto = new EventsShortDto();
        eventDto.setId(events.getId());
        eventDto.setAnnotation(events.getAnnotation());
        eventDto.setCategory(CategoriesMapper.toCategoryShortInnerDto(events.getCategories()));
        eventDto.setConfirmedRequests((long) events.getConfirmedRequests().size());
        eventDto.setEventDate(events.getEventDate());
        eventDto.setInitiator(UsersMapper.getInstance().toUserEventShortInnerDto(events.getInitiator()));
        eventDto.setPaid(events.getPaid());
        eventDto.setTitle(events.getTitle());
        eventDto.setViews(views == null ? 0 : views);
        return eventDto;
    }

    public CompilationsDto.EventInnerShortDto toEventShortInnerDto(Events events, Long views) {
        CompilationsDto.EventInnerShortDto eventDto = new CompilationsDto.EventInnerShortDto();
        eventDto.setId(events.getId());
        eventDto.setAnnotation(events.getAnnotation());
        eventDto.setCategory(CategoriesMapper.toCategoryShortInnerDto(events.getCategories()));
        eventDto.setConfirmedRequests((long) events.getConfirmedRequests().size());
        eventDto.setEventDate(events.getEventDate());
        eventDto.setInitiator(UsersMapper.getInstance().toUserEventShortInnerDto(events.getInitiator()));
        eventDto.setPaid(events.getPaid());
        eventDto.setTitle(events.getTitle());
        eventDto.setViews(views == null ? 0 : views);
        return eventDto;
    }

    public Collection<EventsFullDto> toEventFullDto(Iterable<Events> events, Map<Long, Long> views) {
        return StreamSupport.stream(events.spliterator(), false)
                .map(event -> EventsMapper.getInstance().toEventFullDto(event, views.get(event.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    public EventsFullDto toEventFullDto(Events events, Map<Long, Long> views) {
        return toEventFullDto(events, views.get(events.getId()));
    }

    public Collection<EventsShortDto> toEventShortDto(Collection<Events> events, Map<Long, Long> views) {
        return events.stream()
                .map(event -> EventsMapper.getInstance().toEventShortDto(event, views.get(event.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    public static Collection<CompilationsDto.EventInnerShortDto> toEventShortInnerDto(Collection<Events> events, Map<Long, Long> views) {
        return events.stream()
                .map(event -> EventsMapper.getInstance().toEventShortInnerDto(event, views.get(event.getId())))
                .collect(Collectors.toUnmodifiableList());
    }
}