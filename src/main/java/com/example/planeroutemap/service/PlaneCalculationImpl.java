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
                30.0);
        resultTemporaryPoints.add(firstPoint);
        int n = 0;
        for (int i = 0; i < wayPoints.size(); i++) {
            while (true) {
                WayPoint nextPoint = wayPoints.get(i);
                TemporaryPoint prevPoint = resultTemporaryPoints.getLast();
                final double flightHeightMeters =
                        getFlightHeightMeters(characteristics, nextPoint, prevPoint);

                final double nextSpeed = getNextSpeed(characteristics, prevPoint);

                double nextLat =
                        getFormulaByLatitude(nextPoint, prevPoint,
                                nextPoint.flightSpeedInMetersPerSecond());
                double nextLon =
                        getFormulaByLongitude(nextPoint, prevPoint, nextPoint.flightSpeedInMetersPerSecond());


                double targetDegrees = getTargetDegrees(nextPoint, prevPoint);

                double positionDegrees = getPositionDegrees(characteristics, prevPoint, targetDegrees);
                TemporaryPoint temporaryPoint = new TemporaryPoint(nextLat,
                        nextLon,
                        flightHeightMeters,
                        nextSpeed,
                        positionDegrees);
                resultTemporaryPoints.add(temporaryPoint);


                if (nextLat == nextPoint.latitude() && nextLon == nextPoint.longitude()) {
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

    private double getTargetDegrees(WayPoint nextPoint, TemporaryPoint prevPoint) {
        return getTargetOrientation(prevPoint.latitude(), prevPoint.longitude(),
                nextPoint.latitude(), nextPoint.longitude());
    }

    private static double getFormulaByLongitude(WayPoint nextPoint, TemporaryPoint prevPoint,
                                                double flightSpeedInMetersPerSecond) {
        boolean flag;
        double nextTemporaryPointLongitude = prevPoint.longitude();
        double change = flightSpeedInMetersPerSecond *
                cos(toRadians(prevPoint.courseIsDegrees()));
        if (prevPoint.latitude() == nextPoint.latitude()) {
            change = flightSpeedInMetersPerSecond;
        }
        if (nextPoint.longitude() > prevPoint.longitude()) {
            flag = true;
        } else {
            flag = false;
        }

        if (nextPoint.longitude() <= prevPoint.longitude() + change && flag) {
            nextTemporaryPointLongitude = nextPoint.longitude();
        } else if (nextPoint.longitude() > prevPoint.longitude() + change && flag) {
            nextTemporaryPointLongitude += change;
        } else if (prevPoint.longitude() - change <= nextPoint.longitude()  && !flag) {
            nextTemporaryPointLongitude = nextPoint.longitude();
        } else if (prevPoint.longitude() > nextPoint.longitude() && !flag) {
            nextTemporaryPointLongitude -= change;
        }
        return nextTemporaryPointLongitude;
    }

    private static double getFormulaByLatitude(WayPoint nextPoint, TemporaryPoint prevPoint,
                                               double flightSpeedInMetersPerSecond) {
        boolean flag;
        double nextTemporaryPointLatitude = prevPoint.latitude();

        double change = flightSpeedInMetersPerSecond *
                sin(toRadians(prevPoint.courseIsDegrees()));
        if (prevPoint.longitude() == nextPoint.longitude()) {
            change = flightSpeedInMetersPerSecond;
        }

        if (nextPoint.latitude() > prevPoint.latitude()) {
            flag = true;
        } else {
            flag = false;
        }

        if (nextPoint.latitude() <= prevPoint.latitude() + change && flag) {
            nextTemporaryPointLatitude = nextPoint.latitude();
        } else if (nextPoint.latitude() > prevPoint.latitude() + change && flag) {
            nextTemporaryPointLatitude += change;
        } else if (prevPoint.latitude() - change <= nextPoint.latitude()&& !flag) {
            nextTemporaryPointLatitude = nextPoint.latitude();
        } else if (prevPoint.latitude() - change > nextPoint.latitude() && !flag) {
            nextTemporaryPointLatitude -= change;
        }
        return nextTemporaryPointLatitude;
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
