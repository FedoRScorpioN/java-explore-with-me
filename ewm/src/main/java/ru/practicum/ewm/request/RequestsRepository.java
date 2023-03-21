package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.Events;

import java.util.List;

@Repository
public interface RequestsRepository extends JpaRepository<ParticipationRequests, Long> {
    List<ParticipationRequests> findByEventAndStatus(Events events, ParticipationRequestsStatus status);

    List<ParticipationRequests> findByRequesterId(Long userId);
}