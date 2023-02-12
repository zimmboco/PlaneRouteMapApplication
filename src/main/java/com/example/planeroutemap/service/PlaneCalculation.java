package com.example.planeroutemap.service;

import com.example.planeroutemap.model.AirplaneCharacteristics;
import com.example.planeroutemap.model.TemporaryPoint;
import com.example.planeroutemap.model.WayPoint;
import java.util.List;

public interface PlaneCalculation {
   List<TemporaryPoint> calculateRoute(AirplaneCharacteristics characteristics, List<WayPoint> wayPoints);
}
