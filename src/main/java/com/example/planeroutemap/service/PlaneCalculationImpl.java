package com.example.planeroutemap.service;

import static java.lang.Math.*;
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
                180.0);
        resultTemporaryPoints.add(firstPoint);
        for (int i = 0; i < wayPoints.size(); i++) {
            while (true) {
                WayPoint nextPoint = wayPoints.get(i);
                TemporaryPoint prevPoint = resultTemporaryPoints.getLast();
                final double flightHeightMeters =
                        getFlightHeightMeters(characteristics, nextPoint, prevPoint);
                final double nextSpeed = getNextSpeed(characteristics, prevPoint);
                double targetDegrees = getTargetDegrees(nextPoint, prevPoint, resultTemporaryPoints.getLast().courseIsDegrees());
                double positionDegrees = getPositionDegrees(characteristics, prevPoint, targetDegrees);
                double[] move =
                        move(targetDegrees, nextSpeed, positionDegrees, prevPoint.latitude(),
                                prevPoint.longitude(), nextPoint.latitude(), nextPoint.longitude());
                TemporaryPoint temporaryPoint = new TemporaryPoint(move[0],
                        move[1],
                        flightHeightMeters,
                        nextSpeed,
                        positionDegrees);
                resultTemporaryPoints.add(temporaryPoint);
                System.out.println(String.format("(%s, %s)", move[0], move[1]));
                if (move[0] == nextPoint.latitude() && move[1] == nextPoint.longitude()) {
                    break;
                }
            }
        }
        return resultTemporaryPoints;
    }

    private static double getPositionDegrees(AirplaneCharacteristics characteristics,
                                             TemporaryPoint prevPoint, double targetDegrees) {
        double courseIsDegrees = prevPoint.courseIsDegrees();
        boolean flag;
        if (abs(targetDegrees - courseIsDegrees) > 180) {
            flag = true;
        } else {
            flag = false;
        }
        Double rateOfChangeOfCourseDegreesPerSecond =
                characteristics.getRateOfChangeOfCourseDegreesPerSecond();
        if (!flag) {
            rateOfChangeOfCourseDegreesPerSecond *= -1;
        }
        if (abs(targetDegrees - courseIsDegrees) / abs(rateOfChangeOfCourseDegreesPerSecond) > 1) {
            courseIsDegrees += rateOfChangeOfCourseDegreesPerSecond;
        } else {
            courseIsDegrees = targetDegrees;
        }

        return abs(courseIsDegrees % 360);
    }

    private double getTargetDegrees(WayPoint nextPoint, TemporaryPoint prevPoint, double lastPoint) {
        return getTarget(prevPoint.latitude(), prevPoint.longitude(),
                nextPoint.latitude(), nextPoint.longitude(), lastPoint);
    }

    private static double checkSpeedLimit(AirplaneCharacteristics characteristics,
                                          double nextSpeed) {
        return nextSpeed >
                characteristics.getMaxSpeedMetersPerSecond() ?
                characteristics.getMaxSpeedMetersPerSecond() :
                nextSpeed;
    }

    private static double getNextSpeed(AirplaneCharacteristics characteristics,
                                       TemporaryPoint prevPoint) {
        double nextSpeed = prevPoint.flightSpeedInMetersPerSecond() +
                characteristics.getRateOfChangeOfSpeed();
        return checkSpeedLimit(characteristics, nextSpeed);
    }

    private static double getFlightHeightMeters(AirplaneCharacteristics characteristics,
                                                WayPoint nextPoint, TemporaryPoint prevPoint) {
        return nextPoint.flightHeightMeters() >
                prevPoint.flightHeightMeters() ? prevPoint.flightHeightMeters() +
                characteristics.getHeightChangeRateMetersPerSecond() :
                prevPoint.flightHeightMeters() -
                        characteristics.getHeightChangeRateMetersPerSecond();
    }

    public static double getTarget(double currenLat, double currentLon, double targetLat, double targetLon, double angle) {
        double latDif = targetLat - currenLat;
        double lonDif = targetLon - currentLon;
        double radianAngle = abs(toRadians(angle));
        double rotatedLat = latDif * cos(radianAngle) + lonDif * sin(radianAngle);
        double rotatedLon = - latDif * sin(radianAngle) + lonDif * cos(radianAngle);
        return atan2(rotatedLon, rotatedLat);

    }

    public static double[] move(double direction, double speed, double angle, double currentLat, double currentLon,
                                double targetLat, double targetLon) {
        boolean flagByLat;
        if (targetLat > currentLat) {
            flagByLat = true;
        } else {
            flagByLat = false;
        }
        boolean flagByLon;
        if (targetLon > currentLon) {
            flagByLon = true;
        } else {
            flagByLon = false;
        }
        double radianAngle = Math.toRadians(angle);
        double deltaX = speed * Math.cos(direction + radianAngle);
        double deltaY = speed * Math.sin(direction + radianAngle);
        double rotatedLat = deltaX * Math.cos(radianAngle) - deltaY * Math.sin(radianAngle);
        double rotatedLon = deltaX * Math.sin(radianAngle) + deltaY * Math.cos(radianAngle);
        double newLat = currentLat + rotatedLat;
        double newLon = currentLon + rotatedLon;
        if (newLat >= targetLat && flagByLat) {
            newLat = targetLat;
        }
        if (newLat <= targetLat && !flagByLat) {
            newLat = targetLat;
        }

        if (newLon >= targetLon && flagByLon) {
            newLon = targetLon;
        }
        if (newLon <= targetLon && !flagByLon) {
            newLon = targetLon;
        }
        return new double[] {newLat, newLon};
    }
}
