package ru.practicum.ewm.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.user.Users;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByAuthorAndEvent(Users author, Events event);

    List<Comments> findByEvent(Events event);
}