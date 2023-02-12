package com.example.planeroutemap.service;

import com.example.planeroutemap.model.Airplane;
import java.util.List;
import org.bson.types.ObjectId;

public interface AirplaneService {
    Airplane save(Airplane airplane);
    Airplane getById(ObjectId id);
    List<Airplane> getAll();
}
