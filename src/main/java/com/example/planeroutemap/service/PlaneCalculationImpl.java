package com.example.planeroutemap.service;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;


import com.example.planeroutemap.model.AirplaneCharacteristics;
import com.example.planeroutemap.model.TemporaryPoint;
import com.example.planeroutemap.model.WayPoint;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlaneCalculationImpl implements PlaneCalculation {
    @Override
    public List<TemporaryPoint> calculateRoute(AirplaneCharacteristics characteristics,
                                               List<WayPoint> wayPoints) {
        LinkedList<TemporaryPoint> resultTemporaryPoints = new LinkedList<>();

        TemporaryPoint firstPoint = new TemporaryPoint(
                400.0,
                600.0,
                0.0,
                0.0,
                30.0);
        resultTemporaryPoints.add(firstPoint);
        boolean flagByLatitude;
        boolean flagByLongitude;
        int n = 0;
        for (int i = 0; i < wayPoints.size(); i++) {
            flagByLatitude =
                    wayPoints.get(i).latitude() >= resultTemporaryPoints.getLast().latitude();
            flagByLongitude = wayPoints.get(i).longitude() >= resultTemporaryPoints.getLast().longitude();
            while (true) {
                WayPoint nextPoint = wayPoints.get(i);
                TemporaryPoint prevPoint = resultTemporaryPoints.getLast();
                double flightHeightMeters = nextPoint.flightHeightMeters() >
                        prevPoint.flightHeightMeters() ? prevPoint.flightHeightMeters() +
                        characteristics.getHeightChangeRateMetersPerSecond() :
                        prevPoint.flightHeightMeters() -
                                characteristics.getHeightChangeRateMetersPerSecond();

                double nextSpeed = prevPoint.flightSpeedInMetersPerSecond() +
                        characteristics.getRateOfChangeOfSpeed();

                double flightSpeedInMetersPerSecond = nextSpeed >
                        characteristics.getMaxSpeedMetersPerSecond() ?
                        characteristics.getMaxSpeedMetersPerSecond() :
                        nextSpeed;

                double nextLatitude = prevPoint.latitude();
                double nextLongitude = prevPoint.longitude();
                double formulaByLatitude = nextLongitude == nextPoint.longitude() ?
                        flightSpeedInMetersPerSecond : flightSpeedInMetersPerSecond *
                        Math.sin(Math.toRadians(prevPoint.courseIsDegrees()));
                double formulaByLongitude = nextLatitude == nextPoint.latitude() ?
                        flightSpeedInMetersPerSecond : flightSpeedInMetersPerSecond *
                        Math.cos(Math.toRadians(prevPoint.courseIsDegrees()));

                if (nextLatitude + formulaByLatitude > nextPoint.latitude() && flagByLatitude) {
                    nextLatitude = nextPoint.latitude();
                } else if (nextLatitude + formulaByLatitude < nextPoint.latitude() && flagByLatitude) {
                    nextLatitude += formulaByLatitude;
                } else if (nextLatitude - formulaByLatitude > nextPoint.latitude() && !flagByLatitude) {
                    nextLatitude -= formulaByLatitude;
                } else {
                    nextLatitude = nextPoint.latitude();
                }

                if (nextLongitude + formulaByLongitude > nextPoint.longitude() && flagByLongitude) {
                    nextLongitude = nextPoint.longitude();
                } else if (nextLongitude + formulaByLongitude < nextPoint.longitude() && flagByLongitude) {
                    nextLongitude += formulaByLongitude;
                } else if (nextLongitude - formulaByLongitude > nextPoint.longitude() && !flagByLongitude) {
                    nextLongitude -= formulaByLongitude;
                } else {
                    nextLongitude = nextPoint.longitude();
                }

                double positionDegrees = prevPoint.courseIsDegrees();

                double targetDegrees =
                        getTargetOrientation(prevPoint.latitude(), prevPoint.longitude(),
                                nextPoint.latitude(), nextPoint.longitude());

                if (prevPoint.courseIsDegrees() < targetDegrees) {
                    if (positionDegrees + characteristics.getRateOfChangeOfCourseDegreesPerSecond() >
                            targetDegrees) {
                        positionDegrees = targetDegrees;
                    } else {
                        positionDegrees += characteristics.getRateOfChangeOfCourseDegreesPerSecond();
                    }
                } else {
                    positionDegrees = Math.max(
                            positionDegrees - characteristics.getRateOfChangeOfCourseDegreesPerSecond(),
                            targetDegrees);
                }
                TemporaryPoint temporaryPoint = new TemporaryPoint(nextLatitude,
                        nextLongitude,
                        flightHeightMeters,
                        flightSpeedInMetersPerSecond,
                        positionDegrees);
                resultTemporaryPoints.add(temporaryPoint);

                if (nextLatitude == nextPoint.latitude() && nextLongitude == nextPoint.longitude()) {
                    break;
                }
                n += 1;
            }
        }
        return resultTemporaryPoints;
    }

    private double getTargetOrientation(double currentLat, double currentLon, double targetLat, double targetLon) {
        var latDif = targetLat - currentLat;
        var lonDif = targetLon - currentLon;
        var extraDegrees = 0.0;

        if (lonDif < 0 && latDif < 0) extraDegrees += 180.0;
        else if (latDif < 0) extraDegrees += 270.0;
        else if (lonDif < 0) extraDegrees += 90.0;

        double ratio = extraDegrees % 180 != 0 ? lonDif / latDif : latDif / lonDif;
        var angle = toDegrees(atan(abs(ratio)));
        return angle + extraDegrees;
    }
}
