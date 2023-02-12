package com.example.planeroutemap.service;

import com.example.planeroutemap.model.Airplane;
import com.example.planeroutemap.repository.AirplaneRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class AirplaneServiceImpl implements AirplaneService {
    private final AirplaneRepository airplaneRepository;

    public AirplaneServiceImpl(AirplaneRepository airplaneRepository) {
        this.airplaneRepository = airplaneRepository;
    }

    @Override
    public Airplane save(Airplane airplane) {
        return airplaneRepository.save(airplane);
    }

    @Override
    public Airplane getById(ObjectId id) {
        return airplaneRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Airplane> getAll() {
        return airplaneRepository.findAll();
    }
}
