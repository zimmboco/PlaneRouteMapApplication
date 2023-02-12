package com.example.planeroutemap.controller;

import com.example.planeroutemap.model.Airplane;
import com.example.planeroutemap.model.AirplaneCharacteristics;
import com.example.planeroutemap.model.Flight;
import com.example.planeroutemap.model.TemporaryPoint;
import com.example.planeroutemap.model.WayPoint;
import com.example.planeroutemap.repository.AirplaneRepository;
import com.example.planeroutemap.service.PlaneCalculation;
import java.util.List;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plain")
public class PlaneController {

    private final PlaneCalculation planeCalculation;
    private final AirplaneRepository airplaneRepository;


    public PlaneController(PlaneCalculation planeCalculation, AirplaneRepository airplaneRepository) {
        this.planeCalculation = planeCalculation;
        this.airplaneRepository = airplaneRepository;
    }



    @GetMapping
    public List<TemporaryPoint> calculateRoute() {
        initAirplane();
        Airplane airplane = airplaneRepository.findAll().get(0);
        Flight flight = airplane.getFlights().get(0);
        List<TemporaryPoint> temporaryPointList = planeCalculation.calculateRoute(airplane.getAirplaneCharacteristics(), flight.getWayPoints());
        flight.setTemporaryPoints(temporaryPointList);
        airplaneRepository.save(airplane);
       return temporaryPointList;
    }

    private void initAirplane() {
        Airplane airplane = new Airplane();
        WayPoint wayPoint = new WayPoint(100, 300,
                2000.0, 20.0);
        WayPoint wayPoint1 = new WayPoint(400, 500, 3000, 30);
        WayPoint wayPoint2 = new WayPoint(500, 700, 1000, 10);

        List<WayPoint> wayPointList = List.of(wayPoint, wayPoint1, wayPoint2);

        TemporaryPoint firstPoint = new TemporaryPoint(
                400.0,
                600.0,
                0.0,
                0.0,
                30.0);

        AirplaneCharacteristics airplaneCharacteristics = new AirplaneCharacteristics(30.0, 10.0, 15.0, 5.0);
        Flight flight = new Flight();
        flight.setNumber(1L);
        flight.setWayPoints(wayPointList);
        airplane.setAirplaneCharacteristics(airplaneCharacteristics);
        airplane.setFlights(List.of(flight));
        airplane.setTemporaryPoint(firstPoint);
        airplaneRepository.save(airplane);
    }

}
