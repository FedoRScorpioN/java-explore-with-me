package ru.practicum.ewm.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Setter
@Getter
@Embeddable
public class Location {
    private Float lat;
    private Float lon;
}