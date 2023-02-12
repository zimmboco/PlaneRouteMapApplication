package com.example.planeroutemap;

import com.example.planeroutemap.model.WayPoint;
import com.example.planeroutemap.service.PlaneCalculation;
import com.example.planeroutemap.service.PlaneCalculationImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlaneRouteMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlaneRouteMapApplication.class, args);
    }

}
