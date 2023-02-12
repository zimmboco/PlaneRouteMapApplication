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

    /**
     * максимальная скорость метров в секунду
     * @return
     */
    public Double getMaxSpeedMetersPerSecond() {
        return maxSpeedMetersPerSecond;
    }

    /**
     * уровень набора скорости метров в секунду
     * @return
     */
    public Double getRateOfChangeOfSpeed() {
        return rateOfChangeOfSpeed;
    }

    /**
     * скорость набора высоты в метрах в секунду
     * @return
     */
    public Double getHeightChangeRateMetersPerSecond() {
        return heightChangeRateMetersPerSecond;
    }

    /**
     * скорость изменения напрвления градусов в секунду
     * @return
     */
    public Double getRateOfChangeOfCourseDegreesPerSecond() {
        return rateOfChangeOfCourseDegreesPerSecond;
    }
}
