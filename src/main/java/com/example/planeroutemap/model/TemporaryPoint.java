package com.example.planeroutemap.model;

public record TemporaryPoint(double latitude, double longitude, double flightHeightMeters,
                             double flightSpeedInMetersPerSecond, double courseIsDegrees) {
}
