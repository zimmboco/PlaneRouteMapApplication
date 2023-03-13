package com.example.planeroutemap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
public class AirplaneCharacteristics {
    private Double maxSpeedMetersPerSecond;
    private Double rateOfChangeOfSpeed;
    private Double heightChangeRateMetersPerSecond;
    private Double rateOfChangeOfCourseDegreesPerSecond;

    public Double getMaxSpeedMetersPerSecond() {
        return maxSpeedMetersPerSecond;
    }
    public Double getRateOfChangeOfSpeed() {
        return rateOfChangeOfSpeed;
    }

    public Double getHeightChangeRateMetersPerSecond() {
        return heightChangeRateMetersPerSecond;
    }
    public Double getRateOfChangeOfCourseDegreesPerSecond() {
        return rateOfChangeOfCourseDegreesPerSecond;
    }
}
