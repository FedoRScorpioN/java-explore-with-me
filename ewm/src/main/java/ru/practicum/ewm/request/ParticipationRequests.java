package ru.practicum.ewm.request;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.event.Events;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUESTS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime created;
    @Column(nullable = false)
    private Long requesterId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "STATUS", length = 100)
    private ParticipationRequestsStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    @JsonBackReference
    private Events event;

    public static ParticipationRequests of(Long userId, Long eventId) {
        ParticipationRequests request = new ParticipationRequests();
        request.setRequesterId(userId);
        Events events = new Events();
        events.setId(eventId);
        request.setEvent(events);
        request.setStatus(ParticipationRequestsStatus.PENDING);
        return request;
    }
}