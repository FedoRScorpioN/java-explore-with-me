package ru.practicum.stats.server;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "HITS", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank
    @Column(length = 255, nullable = false)
    String app;
    @Column(length = 255)
    String uri;
    @Column(length = 255)
    String ip;
    @Column(updatable = false)
    LocalDateTime timestamp;
}