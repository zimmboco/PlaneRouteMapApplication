package com.example.planeroutemap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


public record WayPoint ( double latitude, double longitude,
                         double flightHeightMeters, double flightSpeedInMetersPerSecond) {
}
