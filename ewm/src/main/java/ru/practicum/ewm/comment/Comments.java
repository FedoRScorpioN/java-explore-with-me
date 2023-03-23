package ru.practicum.ewm.comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.event.Events;
import ru.practicum.ewm.user.Users;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENTS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID")
    private Users author;
    @Column(nullable = false, name = "TEXT_COMMENT", length = 5000)
    private String textComment;
    @CreationTimestamp
    private LocalDateTime createdOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    @JsonBackReference
    private Events event;
}