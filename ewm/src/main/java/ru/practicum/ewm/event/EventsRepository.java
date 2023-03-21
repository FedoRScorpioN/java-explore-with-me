package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long>, QuerydslPredicateExecutor<Events> {
    List<Events> findByInitiatorId(Long id, Pageable pageable);

    Long countByCategoriesId(Long id);
}