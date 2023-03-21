package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.category.Categories;
import ru.practicum.ewm.request.ParticipationRequests;
import ru.practicum.ewm.request.ParticipationRequestsStatus;
import ru.practicum.ewm.user.Users;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "EVENTS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 3000)
    private String annotation;
    @ManyToOne
    @JoinColumn(nullable = false, name = "CATEGORY_ID")
    private Categories categories;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Collection<ParticipationRequests> requests = new ArrayList<>();
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdOn;
    @Column(length = 10000)
    private String description;
    @Column(nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(nullable = false, name = "INITIATOR_ID")
    private Users initiator;
    @Embedded
    private Location location;
    @Column(nullable = false)
    private Boolean paid;
    @Column
    private Integer participantLimit;
    @Column
    private LocalDateTime publishedOn;
    @Column
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private EventState state;
    @Column(nullable = false, length = 200)
    private String title;

    public List<ParticipationRequests> getConfirmedRequests() {
        return this.requests.stream()
                .filter((request) -> request.getStatus() == ParticipationRequestsStatus.CONFIRMED)
                .collect(Collectors.toUnmodifiableList());
    }
}