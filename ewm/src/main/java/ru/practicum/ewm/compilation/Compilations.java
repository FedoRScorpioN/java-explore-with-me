package ru.practicum.ewm.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.Events;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "COMPILATIONS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Compilations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(
            name = "COMPILATION_EVENT",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Events> events;
    @Column(nullable = false)
    private Boolean pinned;
    @Column(nullable = false, name = "TITLE", length = 128)
    private String title;
}