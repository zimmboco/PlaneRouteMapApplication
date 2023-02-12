package com.example.planeroutemap.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
public class Flight {
    private Long number;
    private List<WayPoint> wayPoints;
    private List<TemporaryPoint> temporaryPoints;
}
